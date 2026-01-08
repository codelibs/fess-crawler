# CLAUDE.md - Fess Crawler Development Guide

Quick reference for AI assistants working on the Fess Crawler project.

## Project Overview

**Fess Crawler** is a Java-based web crawling framework for enterprise content extraction.

### Essential Info

- **Language**: Java 21+
- **Build**: Maven 3.x
- **License**: Apache 2.0
- **DI**: LastaFlute DI
- **Repo**: https://github.com/codelibs/fess-crawler
- **Version**: 15.5.0-SNAPSHOT

### Tech Stack

- **HTTP**: Apache HttpComponents 4.5+ and 5.x (switchable)
- **Extraction**: Apache Tika 3.0+, POI 5.3+, PDFBox 3.0+
- **Testing**: JUnit 4, UTFlute, Mockito 5.7.0, Testcontainers 2.0.2
- **Storage**: In-memory (default), OpenSearch (optional)
- **Cloud**: AWS SDK v2 (S3), Google Cloud Storage

### Protocols

- **HTTP/HTTPS**: Full crawling, cookies, auth, robots.txt
- **File**: Local/network file systems
- **FTP/FTPS**: With authentication
- **SMB/CIFS**: Windows shares (SMB1/SMB2+)
- **Storage**: MinIO/S3-compatible (via storage://)
- **S3**: Amazon S3 and compatible (via s3://)
- **GCS**: Google Cloud Storage (via gcs://)

### Content Formats

Office (Word, Excel, PowerPoint), PDF, Archives (ZIP, TAR, GZ, LHA), HTML, XML, JSON, Markdown, Media (audio/video metadata), Images (EXIF/IPTC/XMP), Email (EML)

---

## Architecture

### Module Structure

```
fess-crawler-parent/
├── fess-crawler/              # Core framework
├── fess-crawler-lasta/        # LastaFlute DI integration
└── fess-crawler-opensearch/   # OpenSearch backend
```

### Key Design Patterns

**Factory**: `CrawlerClientFactory`, `ExtractorFactory` - protocol/format-specific component selection
**Strategy**: `CrawlerClient`, `Extractor`, `Transformer` - pluggable implementations
**Builder**: `RequestDataBuilder`, `ExtractorBuilder` - fluent construction
**Template Method**: `AbstractCrawlerClient`, `AbstractExtractor` - common logic with overrides
**DI**: LastaFlute container with `@Resource` and XML config

### Core Principles

**Thread Safety**:
- `AtomicLong` for counters (`CrawlerContext.accessCount`)
- `volatile` for status flags
- Synchronized blocks for critical sections
- Thread-local storage via `CrawlingParameterUtil`

**Resource Management**:
- `AutoCloseable` throughout
- `DeferredFileOutputStream` for large responses (temp files for >1MB)
- Connection pooling with limits
- Background temp file deletion via `FileUtil.deleteInBackground()`

**Fault Tolerance**:
- `FaultTolerantClient` wrapper (retry, circuit breaker)
- `SwitchableHttpClient` for HTTP client fallback
- Graceful degradation (e.g., robots.txt parsing continues on errors)

---

## Key Components

### Crawler (`Crawler.java`)

Main orchestrator for crawling operations.

**Key Methods**:
```java
String execute()                // Start crawling, return session ID
void addUrl(String url)         // Add URL to queue
void cleanup(String sessionId)  // Clean up session
void stop()                     // Stop gracefully
```

**Key Fields**: `crawlerContext`, `urlFilter`, `intervalController`, `clientFactory`, `ruleManager`

### CrawlerContext (`CrawlerContext.java`)

Execution context and configuration.

**Important Fields**:
```java
String sessionId                // Format: yyyyMMddHHmmssSSS
volatile CrawlerStatus status   // DONE, RUNNING
AtomicLong accessCount          // Thread-safe counter
int numOfThread = 10            // Crawler threads
int maxDepth = -1               // Max depth (-1 = unlimited)
long maxAccessCount = 0         // Max URLs (0 = unlimited)
```

### CrawlerThread (`CrawlerThread.java`)

Worker thread for crawling.

**Flow**: Poll URL → Validate → Get client → Delay → Check last-modified → Execute → Process → Extract children → Queue children → Delay

### HTTP Client Architecture

The HTTP client system supports both Apache HttpComponents 4.x and 5.x with a switchable architecture:

```
SwitchableHttpClient (extends FaultTolerantClient)
    ├── Hc5HttpClient (default) - Apache HttpComponents 5.x
    └── Hc4HttpClient (fallback) - Apache HttpComponents 4.x

HcHttpClient (abstract base class)
    ├── Hc4HttpClient
    └── Hc5HttpClient
```

**Switching HTTP Clients**:
```bash
# Use HC5 (default)
java -Dfess.crawler.http.client=hc5 ...

# Use HC4 (legacy)
java -Dfess.crawler.http.client=hc4 ...
```

**Key HTTP Client Properties** (defined in `HcHttpClient`):
- `connectionTimeout`, `soTimeout` - Connection/socket timeouts
- `proxyHost`, `proxyPort` - Proxy settings
- `userAgent` - User agent string
- `robotsTxtEnabled` - Enable/disable robots.txt
- `ignoreSslCertificate` - Skip SSL validation
- `maxTotalConnection`, `defaultMaxConnectionPerRoute` - Connection pool limits

### CrawlerClientFactory

Pattern-based client selection using `LinkedHashMap<Pattern, CrawlerClient>`.

**Standard Patterns** (from `crawler/client.xml`):
```java
"http:.*", "https:.*" → httpClient (SwitchableHttpClient)
"file:.*"             → fsClient (FileSystemClient)
"smb:.*"              → smbClient (SmbClient - SMB2+)
"smb1:.*"             → smb1Client (SmbClient - SMB1)
"ftp:.*", "ftps:.*"   → ftpClient (FtpClient)
"storage:.*"          → storageClient (StorageClient - MinIO)
"s3:.*"               → s3Client (S3Client - AWS S3)
"gcs:.*"              → gcsClient (GcsClient - Google Cloud Storage)
```

### Cloud Storage Clients

**S3Client** (`client/s3/S3Client.java`):
- Uses AWS SDK v2 (`software.amazon.awssdk:s3`)
- URL format: `s3://bucket-name/object-path`
- Properties: `endpoint`, `accessKey`, `secretKey`, `region`

**GcsClient** (`client/gcs/GcsClient.java`):
- Uses Google Cloud Storage SDK
- URL format: `gcs://bucket-name/object-path`
- Properties: `projectId`, `credentialsFile`, `endpoint` (for testing)

**StorageClient** (`client/storage/StorageClient.java`):
- Uses MinIO SDK for S3-compatible storage
- URL format: `storage://bucket-name/object-path`

### Services

**UrlQueueService**: URL queue management (FIFO), duplicate detection via `visited()`
**DataService**: Access result persistence, iteration

**Implementations**:
- `UrlQueueServiceImpl`, `DataServiceImpl`: In-memory (default)
- `OpenSearchDataService`: OpenSearch backend (persistent)

### Processing Pipeline

```
CrawlerThread → Client → ResponseProcessor → Transformer → Extractor → ExtractData
                                                                            ↓
                         ← UrlQueueService ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ←
                         ← DataService ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ←
```

**Rule**: Pattern-based response routing (`RegexRule`, `SitemapsRule`)
**ResponseProcessor**: `DefaultResponseProcessor`, `SitemapsResponseProcessor`, `NullResponseProcessor`
**Transformer**: `HtmlTransformer`, `XmlTransformer`, `FileTransformer`, etc.
**Extractor**: Weight-based selection (tries in descending weight order)

### Key Extractors

`TikaExtractor` (1000+ formats), `PdfExtractor`, `MsWordExtractor`, `MsExcelExtractor`, `MsPowerPointExtractor`, `ZipExtractor`, `HtmlExtractor`, `MarkdownExtractor`, `EmlExtractor`, etc.

**Registration**:
```java
extractorFactory.addExtractor("text/html", htmlExtractor, 2);  // Weight 2
extractorFactory.addExtractor("text/html", tikaExtractor, 1);  // Fallback
```

### Helpers

**RobotsTxtHelper**: RFC 9309 parsing, user-agent matching, crawl-delay, sitemaps
**SitemapsHelper**: Sitemap XML parsing, index handling
**MimeTypeHelper**: MIME detection via Tika
**EncodingHelper**: Charset detection with BOM
**UrlConvertHelper**: URL normalization
**ContentLengthHelper**: Content length limits per MIME type

---

## Development Workflow

### Build Commands

```bash
mvn clean install              # Build all
mvn clean install -DskipTests  # Skip tests
mvn test                       # Run tests
mvn formatter:format           # Format code
mvn license:format             # Update license headers
```

### Code Style

- 4 spaces (no tabs)
- Opening brace on same line
- Max line length: 120
- JavaDoc required for public APIs
- License headers required (Apache 2.0, 2012-2025)

### Testing

**Structure**: `src/test/java/org/codelibs/fess/crawler/`
**Frameworks**: JUnit 4, UTFlute, Mockito 5.7.0, Testcontainers 2.0.2
**Test Resources**: `src/test/resources/`
**Test Server**: Jetty 12.0.16 for HTTP tests

**Pattern** (using PlainTestCase):
```java
public class MyTest extends PlainTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Setup
    }

    @Override
    protected void tearDown() throws Exception {
        // Cleanup
        super.tearDown();
    }

    public void test_method_scenario() throws Exception {
        // Given
        // When
        // Then
    }
}
```

**Pattern** (using Mockito):
```java
import static org.mockito.Mockito.*;

public class MyTest extends PlainTestCase {
    public void test_with_mock() {
        CrawlerClient mockClient = mock(CrawlerClient.class);
        when(mockClient.execute(any())).thenReturn(responseData);
        // Test
        verify(mockClient).execute(any());
    }
}
```

**Coverage Goal**: >80% line coverage

### Contributing

1. Fork repo
2. Create feature branch (`feature/amazing-feature`)
3. Make focused commits
4. Add tests
5. Format code (`mvn formatter:format && mvn license:format`)
6. Run tests (`mvn test`)
7. Open Pull Request

---

## Common Development Tasks

### Adding a Protocol Client

1. **Implement `CrawlerClient`**:
```java
public class MyClient extends AbstractCrawlerClient {
    @Override
    public ResponseData execute(RequestData request) { /* ... */ }

    @Override
    public void close() { /* cleanup */ }
}
```

2. **Register in DI config** (`crawler/client.xml`):
```xml
<component name="myClient" class="...MyClient" instance="prototype">
    <property name="charset">"UTF-8"</property>
</component>
```

3. **Add to factory**:
```xml
<postConstruct name="addClient">
    <arg>"myprotocol:.*"</arg>
    <arg>myClient</arg>
</postConstruct>
```

4. **Add tests**: Unit + integration with Testcontainers if needed

### Adding a Content Extractor

1. **Implement `Extractor`**:
```java
public class MyExtractor extends AbstractExtractor {
    @Override
    public ExtractData getText(InputStream in, Map<String, String> params) {
        ExtractData data = new ExtractData();
        // Extract text
        data.setContent(extractedText);
        return data;
    }
}
```

2. **Register** in `crawler/extractor.xml`:
```xml
<component name="myExtractor" class="...MyExtractor" instance="singleton"/>
```

```java
extractorFactory.addExtractor("application/myformat", myExtractor, weight);
```

3. **Add test with sample file** in `src/test/resources/`

### Configuring URL Filtering

```java
// Include patterns (must match)
crawler.urlFilter.addInclude("https://example.com/.*");

// Exclude patterns (must not match)
crawler.urlFilter.addExclude(".*\\.(css|js|png|jpg)$");
```

### Setting Crawl Limits

```java
context.setMaxAccessCount(1000);  // Max URLs (0 = unlimited)
context.setMaxDepth(3);           // Max depth (-1 = unlimited)
context.setNumOfThread(10);       // Concurrent threads
```

### Accessing Results

```java
DataService ds = container.getComponent("dataService");

// Count
int count = ds.getCount(sessionId);

// Get by URL
AccessResult result = ds.getAccessResult(sessionId, url);

// Iterate all
ds.iterate(sessionId, accessResult -> {
    System.out.println(accessResult.getUrl());
    System.out.println(accessResult.getContent());
});

// Cleanup
ds.delete(sessionId);
```

### Resource Cleanup Pattern

```java
// Always use try-with-resources for ResponseData
try (ResponseData responseData = client.execute(requestData)) {
    // Process
}  // Temp files auto-deleted
```

### Configuring Cloud Storage Clients

**S3 Client**:
```java
S3Client s3Client = container.getComponent("s3Client");
s3Client.setEndpoint("https://s3.amazonaws.com");
s3Client.setAccessKey("your-access-key");
s3Client.setSecretKey("your-secret-key");
s3Client.setRegion("us-east-1");
```

**GCS Client**:
```java
GcsClient gcsClient = container.getComponent("gcsClient");
gcsClient.setProjectId("your-project-id");
gcsClient.setCredentialsFile("/path/to/credentials.json");
```

---

## Best Practices for AI Assistants

### When Adding Features

1. Read existing code first (use symbol overview tools)
2. Follow existing patterns
3. Add tests
4. Handle resources properly (try-with-resources)
5. Consider thread safety
6. Update JavaDoc

### When Fixing Bugs

1. Write failing test first
2. Understand root cause
3. Minimal changes
4. Verify no regressions

### When Refactoring

1. Preserve behavior
2. Keep tests green
3. Small incremental steps
4. Don't mix with new features

### Code Quality Checklist

- [ ] Java conventions followed
- [ ] JavaDoc for public APIs
- [ ] Tests pass (`mvn test`)
- [ ] No compiler warnings
- [ ] Proper exception handling
- [ ] Resource cleanup (AutoCloseable)
- [ ] Thread-safe if needed
- [ ] Code formatted (`mvn formatter:format`)
- [ ] License headers (`mvn license:format`)

---

## Quick Reference

### Key File Locations

**Core**: `fess-crawler/src/main/java/org/codelibs/fess/crawler/`
- `Crawler.java`, `CrawlerContext.java`, `CrawlerThread.java`

**Clients**: `fess-crawler/src/main/java/org/codelibs/fess/crawler/client/`
- `http/` - `HcHttpClient.java` (abstract), `Hc4HttpClient.java`, `Hc5HttpClient.java`, `SwitchableHttpClient.java`
- `fs/FileSystemClient.java`
- `ftp/FtpClient.java`
- `smb/SmbClient.java`, `smb1/SmbClient.java`
- `storage/StorageClient.java`
- `s3/S3Client.java`
- `gcs/GcsClient.java`

**DI Config**: `fess-crawler-lasta/src/main/resources/`
- `crawler.xml` - Main configuration
- `crawler/client.xml` - Client definitions
- `crawler/extractor.xml` - Extractor definitions
- `crawler/rule.xml` - Rule definitions

### Exception Hierarchy

```
CrawlerSystemException (unchecked)  # Setup/config errors
CrawlingAccessException (checked)   # Runtime crawl errors
  ├─ MaxLengthExceededException
  └─ ChildUrlsException
ExtractException (checked)          # Extraction failures
  └─ UnsupportedExtractException
```

### Thread-Local Storage

```java
// Set (in CrawlerThread)
CrawlingParameterUtil.setCrawlerContext(context);
CrawlingParameterUtil.setUrlQueue(urlQueue);

// Get (anywhere in same thread)
CrawlerContext ctx = CrawlingParameterUtil.getCrawlerContext();

// Clear (ALWAYS in finally)
CrawlingParameterUtil.clearAll();
```

### Key Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Apache HttpComponents 5.x | 5.6 | HTTP client (default) |
| Apache HttpComponents 4.x | 4.5.x | HTTP client (legacy) |
| Apache Tika | 3.0+ | Content extraction |
| Apache POI | 5.3+ | Office documents |
| PDFBox | 3.0+ | PDF extraction |
| AWS SDK v2 | 2.x | S3 support |
| Google Cloud Storage | latest | GCS support |
| MinIO | 8.x | S3-compatible storage |
| Jetty | 12.0.16 | Test server |
| Mockito | 5.7.0 | Unit testing |
| Testcontainers | 2.0.2 | Integration testing |

## Log Message Guidelines

- Format parameters as `key=value` (e.g., `sessionId={}`, `url={}`)
- Prefix with `[name]` when context identification is needed
- Use full words, not abbreviations (e.g., "documents" not "docs")
- Log only identifying fields, not entire objects
