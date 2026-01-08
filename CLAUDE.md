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

### Tech Stack

- **HTTP**: Apache HttpComponents 4.5+ and 5.x (switchable)
- **Extraction**: Apache Tika, POI, PDFBox
- **Testing**: JUnit 4, UTFlute, Mockito, Testcontainers
- **Storage**: In-memory (default), OpenSearch (optional)
- **Cloud**: AWS SDK v2 (S3), Google Cloud Storage

### Protocols

HTTP/HTTPS, File, FTP/FTPS, SMB/CIFS (SMB1/SMB2+), Storage (MinIO via `storage://`), S3 (`s3://`), GCS (`gcs://`)

### Content Formats

Office (Word, Excel, PowerPoint), PDF, Archives (ZIP, TAR, GZ, LHA), HTML, XML, JSON, Markdown, Media metadata, Images (EXIF/IPTC/XMP), Email (EML)

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

- **Factory**: `CrawlerClientFactory`, `ExtractorFactory` - protocol/format-specific component selection
- **Strategy**: `CrawlerClient`, `Extractor`, `Transformer` - pluggable implementations
- **Builder**: `RequestDataBuilder`, `ExtractorBuilder` - fluent construction
- **Template Method**: `AbstractCrawlerClient`, `AbstractExtractor` - common logic with overrides
- **DI**: LastaFlute container with `@Resource` and XML config

### Core Principles

**Thread Safety**: `AtomicLong` for counters, `volatile` for status flags, synchronized blocks, thread-local storage via `CrawlingParameterUtil`

**Resource Management**: `AutoCloseable` throughout, `DeferredFileOutputStream` for large responses, connection pooling, background temp file deletion via `FileUtil.deleteInBackground()`

**Fault Tolerance**: `FaultTolerantClient` wrapper (retry, circuit breaker), `SwitchableHttpClient` for HTTP client fallback

---

## Key Components

### Core Classes

- **Crawler** (`Crawler.java`): Main orchestrator - `execute()`, `addUrl()`, `cleanup()`, `stop()`
- **CrawlerContext** (`CrawlerContext.java`): Execution context - `sessionId`, `status`, `accessCount`, `numOfThread`, `maxDepth`, `maxAccessCount`
- **CrawlerThread** (`CrawlerThread.java`): Worker thread - Poll URL → Validate → Execute → Process → Queue children

### HTTP Client Architecture

```
SwitchableHttpClient (extends FaultTolerantClient)
    ├── Hc5HttpClient (default) - Apache HttpComponents 5.x
    └── Hc4HttpClient (fallback) - Apache HttpComponents 4.x

HcHttpClient (abstract base class)
    ├── Hc4HttpClient
    └── Hc5HttpClient
```

Switch via system property: `-Dfess.crawler.http.client=hc4` or `hc5` (default)

**Key Properties**: `connectionTimeout`, `soTimeout`, `proxyHost`, `proxyPort`, `userAgent`, `robotsTxtEnabled`, `ignoreSslCertificate`, `maxTotalConnection`, `defaultMaxConnectionPerRoute`

### CrawlerClientFactory

Pattern-based client selection (from `crawler/client.xml`):
- `http:.*`, `https:.*` → SwitchableHttpClient
- `file:.*` → FileSystemClient
- `smb:.*` → SmbClient (SMB2+), `smb1:.*` → SmbClient (SMB1)
- `ftp:.*`, `ftps:.*` → FtpClient
- `storage:.*` → StorageClient, `s3:.*` → S3Client, `gcs:.*` → GcsClient

### Cloud Storage Clients

- **S3Client**: AWS SDK v2, `s3://bucket/path`, properties: `endpoint`, `accessKey`, `secretKey`, `region`
- **GcsClient**: Google Cloud SDK, `gcs://bucket/path`, properties: `projectId`, `credentialsFile`, `endpoint`
- **StorageClient**: MinIO SDK, `storage://bucket/path`

### Services

- **UrlQueueService**: URL queue management (FIFO), duplicate detection
- **DataService**: Access result persistence, iteration
- Implementations: `UrlQueueServiceImpl`, `DataServiceImpl` (in-memory), `OpenSearchDataService` (persistent)

### Processing Pipeline

```
CrawlerThread → Client → ResponseProcessor → Transformer → Extractor → ExtractData
                                                                            ↓
                         ← UrlQueueService ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ←
                         ← DataService ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ←
```

- **Rule**: Pattern-based response routing (`RegexRule`, `SitemapsRule`)
- **ResponseProcessor**: `DefaultResponseProcessor`, `SitemapsResponseProcessor`, `NullResponseProcessor`
- **Transformer**: `HtmlTransformer`, `XmlTransformer`, `FileTransformer`, etc.
- **Extractor**: Weight-based selection (tries in descending weight order)

### Key Extractors

`TikaExtractor`, `PdfExtractor`, `MsWordExtractor`, `MsExcelExtractor`, `MsPowerPointExtractor`, `ZipExtractor`, `HtmlExtractor`, `MarkdownExtractor`, `EmlExtractor`

### Helpers

- **RobotsTxtHelper**: RFC 9309 parsing, user-agent matching, crawl-delay, sitemaps
- **SitemapsHelper**: Sitemap XML parsing, index handling
- **MimeTypeHelper**: MIME detection via Tika
- **EncodingHelper**: Charset detection with BOM
- **UrlConvertHelper**: URL normalization
- **ContentLengthHelper**: Content length limits per MIME type

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

- 4 spaces (no tabs), opening brace on same line, max line length 120
- JavaDoc required for public APIs
- License headers required (Apache 2.0)

### Testing

- **Structure**: `src/test/java/org/codelibs/fess/crawler/`
- **Base class**: Extend `PlainTestCase` from UTFlute
- **Test Resources**: `src/test/resources/`
- **Coverage Goal**: >80% line coverage

### Contributing

1. Fork repo, create feature branch
2. Make focused commits with tests
3. Format code (`mvn formatter:format && mvn license:format`)
4. Run tests (`mvn test`)
5. Open Pull Request

---

## Best Practices for AI Assistants

### When Adding Features

1. Read existing code first
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

### Code Quality Checklist

- [ ] Java conventions followed
- [ ] JavaDoc for public APIs
- [ ] Tests pass (`mvn test`)
- [ ] Proper exception handling
- [ ] Resource cleanup (AutoCloseable)
- [ ] Thread-safe if needed
- [ ] Code formatted and license headers added

---

## Quick Reference

### Key File Locations

**Core**: `fess-crawler/src/main/java/org/codelibs/fess/crawler/`
- `Crawler.java`, `CrawlerContext.java`, `CrawlerThread.java`

**Clients**: `fess-crawler/src/main/java/org/codelibs/fess/crawler/client/`
- `http/` - `HcHttpClient.java`, `Hc4HttpClient.java`, `Hc5HttpClient.java`, `SwitchableHttpClient.java`
- `fs/FileSystemClient.java`, `ftp/FtpClient.java`
- `smb/SmbClient.java`, `smb1/SmbClient.java`
- `storage/StorageClient.java`, `s3/S3Client.java`, `gcs/GcsClient.java`

**DI Config**: `fess-crawler-lasta/src/main/resources/`
- `crawler.xml`, `crawler/client.xml`, `crawler/extractor.xml`, `crawler/rule.xml`

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

Use `CrawlingParameterUtil` to set/get `CrawlerContext` and `UrlQueue` in worker threads. Always clear in finally block with `CrawlingParameterUtil.clearAll()`.

### Resource Cleanup Pattern

Always use try-with-resources for `ResponseData` - temp files are auto-deleted on close.

## Log Message Guidelines

- Format parameters as `key=value` (e.g., `sessionId={}`, `url={}`)
- Prefix with `[name]` when context identification is needed
- Use full words, not abbreviations
- Log only identifying fields, not entire objects
