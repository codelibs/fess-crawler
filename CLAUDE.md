# CLAUDE.md - AI Assistant Development Guide for Fess Crawler

This document provides comprehensive information about the Fess Crawler project to help AI assistants (like Claude) understand the codebase architecture, design patterns, and best practices for contributing to the project.

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Key Components](#key-components)
4. [Crawler Flow and Lifecycle](#crawler-flow-and-lifecycle)
5. [Important Implementation Details](#important-implementation-details)
6. [Development Guidelines](#development-guidelines)
7. [Testing Strategy](#testing-strategy)
8. [Common Tasks and Patterns](#common-tasks-and-patterns)

---

## Project Overview

**Fess Crawler** is a powerful, flexible Java-based web crawling framework designed for enterprise-scale content extraction and processing. It supports multiple protocols and provides extensive content extraction capabilities from various document formats.

### Key Information

- **Language**: Java 21+
- **Build System**: Maven 3.x
- **Version**: 15.2.0-SNAPSHOT
- **License**: Apache License 2.0
- **DI Container**: LastaFlute DI (based on DBFlute)
- **Repository**: https://github.com/codelibs/fess-crawler

### Technology Stack

- **HTTP Client**: Apache HttpComponents (4.5+)
- **Content Extraction**: Apache Tika (3.0+), Apache POI (5.3+), PDFBox (3.0+)
- **Testing**: JUnit 4, UTFlute, Testcontainers, Mockito 5.7.0
- **Storage**: In-memory (default), OpenSearch (optional)
- **Utilities**: Apache Commons (IO, Net, Lang3, Text, Pool2, Codec), Google Guava, Jackson

### Supported Protocols

1. **HTTP/HTTPS** (`http://`, `https://`) - Full web crawling with cookies, auth, redirects, robots.txt
2. **File System** (`file://`) - Local and network file systems
3. **FTP** (`ftp://`) - FTP servers with authentication
4. **SMB/CIFS** (`smb://`, `smb1://`) - Windows network shares (SMB1 and SMB2+)
5. **Cloud Storage** (`storage://`) - MinIO/S3-compatible object storage

### Supported Content Formats

- **Office**: Word (.doc, .docx), Excel (.xls, .xlsx), PowerPoint (.ppt, .pptx), OpenOffice/LibreOffice
- **PDF**: Text and metadata extraction via PDFBox
- **Archives**: ZIP, TAR, GZ, LHA with nested extraction
- **Web**: HTML (with XPath), XML, JSON
- **Media**: Audio (MP3, WAV, FLAC), Video (MP4, AVI, MOV) with metadata
- **Images**: JPEG, PNG, GIF, TIFF, BMP with EXIF/IPTC/XMP metadata

---

## Architecture

### Multi-Module Structure

```
fess-crawler-parent/
├── fess-crawler/              # Core crawler framework
│   ├── src/main/java/
│   │   └── org/codelibs/fess/crawler/
│   │       ├── Crawler.java               # Main orchestrator
│   │       ├── CrawlerContext.java        # Execution context
│   │       ├── CrawlerThread.java         # Worker thread
│   │       ├── client/                    # Protocol clients
│   │       ├── extractor/                 # Content extractors
│   │       ├── transformer/               # Data transformers
│   │       ├── processor/                 # Response processors
│   │       ├── service/                   # Core services
│   │       ├── helper/                    # Helper utilities
│   │       ├── filter/                    # URL filters
│   │       ├── rule/                      # Processing rules
│   │       └── util/                      # Utilities
│   └── src/main/java/org/codelibs/fess/net/protocol/storage/
│       └── Handler.java                   # Storage protocol handler
├── fess-crawler-lasta/        # LastaFlute DI integration
│   └── src/main/resources/
│       └── crawler.xml                    # DI configuration
└── fess-crawler-opensearch/   # OpenSearch backend
```

### Design Patterns

1. **Factory Pattern**
   - `CrawlerClientFactory`: Creates protocol-specific clients based on URL patterns
   - `ExtractorFactory`: Creates content extractors based on MIME types (with weight-based prioritization)
   - `CrawlerPooledObjectFactory`: Object pooling for expensive resources

2. **Strategy Pattern**
   - `CrawlerClient`: Protocol-specific implementations (HTTP, FTP, SMB, File, Storage)
   - `Extractor`: Format-specific content extractors (50+ implementations)
   - `Transformer`: Content-type transformers (HTML, XML, File, Binary, Text)
   - `ResponseProcessor`: Rule-based response handlers

3. **Builder Pattern**
   - `RequestDataBuilder`: Fluent API for constructing request data
   - `ExtractorBuilder`: Builds extraction pipelines

4. **Template Method Pattern**
   - `AbstractCrawlerClient`: Common client logic with protocol-specific overrides
   - `AbstractExtractor`: Common extraction logic
   - `AbstractTransformer`: Common transformation logic
   - `AbstractRule`: Common rule logic
   - `AbstractIntervalController`: Common interval control logic

5. **Observer/Callback Pattern**
   - `AccessResultCallback`: For iterating over crawl results
   - `LogHelper`: Extensible logging system

6. **Dependency Injection**
   - LastaFlute DI container with `@Resource` annotations
   - XML-based component configuration (`crawler.xml`)
   - Supports `prototype` and `singleton` scopes

7. **Object Pool Pattern**
   - Apache Commons Pool2 integration
   - `CrawlerPooledObjectFactory`: Manages pooled crawler instances

### Architectural Principles

#### 1. Thread Safety
- `AtomicLong` for access counting (`CrawlerContext.accessCount`)
- `volatile` for status flags (`CrawlerContext.status`)
- Synchronized blocks for active thread count management
- `LruHashSet` (10,000 entries) for robots.txt URL tracking
- Thread-local storage for per-thread data (`CrawlingParameterUtil`)

#### 2. Resource Management
- AutoCloseable interfaces throughout (Crawler, clients, response data)
- Explicit cleanup in finally blocks
- Temporary file management with background deletion (`FileUtil.deleteInBackground()`)
- Connection pool management with configurable limits
- Timeout management via `TimeoutManager`

#### 3. Fault Tolerance
- `FaultTolerantClient`: Wrapper providing retry and circuit breaker patterns
- Exception hierarchy for different failure scenarios
- Graceful degradation (e.g., robots.txt parsing continues on malformed lines)

#### 4. Extensibility
- Interface-based design throughout
- Plugin architecture for clients, extractors, transformers, processors
- Rule-based processing pipeline with pattern matching
- DI container for flexible component wiring

#### 5. Separation of Concerns
```
CrawlerThread → CrawlerClient → ResponseProcessor → Transformer → Extractor
                                                                      ↓
                     ← UrlQueueService ←                    ExtractData
                     ← DataService ←
```

---

## Key Components

### Core Components

#### Crawler (`Crawler.java`)
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/Crawler.java`

**Responsibilities**:
- Main entry point and orchestrator for crawling operations
- Session management (generates unique session IDs in `yyyyMMddHHmmssSSS` format)
- Thread group creation and lifecycle management
- URL queue initialization
- Background/daemon mode execution support
- Resource cleanup via AutoCloseable

**Key Methods**:
```java
String execute()                    // Starts crawling, returns session ID
void addUrl(String url)             // Adds URL to crawl queue
void cleanup(String sessionId)      // Cleans up session data
void stop()                         // Stops crawling gracefully
void awaitTermination()             // Waits for completion
void setBackground(boolean bg)      // Enable background execution
```

**Key Fields**:
- `crawlerContext`: Execution context and configuration
- `urlFilter`: URL filtering with include/exclude patterns
- `intervalController`: Politeness delay controller
- `clientFactory`: Factory for protocol-specific clients
- `ruleManager`: Rule-based response routing

#### CrawlerContext (`CrawlerContext.java`)
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/CrawlerContext.java`

**Responsibilities**:
- Holds execution context and configuration
- Thread-safe access count tracking
- Active thread count management
- Configuration parameters storage
- Thread-local sitemap storage
- Robots.txt URL tracking with LRU cache (10,000 entries)

**Important Fields**:
```java
String sessionId                    // Unique session identifier
volatile CrawlerStatus status       // DONE, RUNNING (thread-safe)
AtomicLong accessCount              // URLs accessed (thread-safe)
int activeThreadCount               // Currently active threads (synchronized)
int numOfThread = 10                // Number of crawler threads
int maxDepth = -1                   // Maximum crawl depth (-1 = unlimited)
long maxAccessCount = 0             // Maximum URLs to crawl (0 = unlimited)
int maxThreadCheckCount = 20        // Thread monitoring frequency
```

**Thread Safety Notes**:
- `status` is volatile for visibility across threads
- `accessCount` uses AtomicLong for lock-free incrementing
- `activeThreadCount` uses synchronized methods
- `robotTxtUrls` uses thread-safe LruHashSet

#### CrawlerThread (`CrawlerThread.java`)
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/CrawlerThread.java`

**Responsibilities**:
- Individual worker thread for crawling
- URL polling from queue
- Client selection based on URL protocol
- Response processing orchestration
- Child URL extraction and queuing
- Last-modified checking (conditional requests)
- Robots.txt sitemap discovery

**Crawling Loop**:
1. Poll URL from queue (`UrlQueueService.poll()`)
2. Validate URL (depth, filters)
3. Get appropriate client (`CrawlerClientFactory.getClient()`)
4. Pre-processing interval delay
5. Check last-modified (HEAD request if available)
6. Execute request (GET/POST)
7. Process response via rule-matched processor
8. Extract and queue child URLs
9. Post-processing interval delay
10. Cleanup and release resources

### Client Architecture

#### CrawlerClientFactory (`CrawlerClientFactory.java`)
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/client/CrawlerClientFactory.java`

**Pattern Matching**:
- Uses `LinkedHashMap<Pattern, CrawlerClient>` for ordered matching
- First-match-wins strategy
- Supports position-based insertion for priority control

**Standard Client Registration**:
```java
addClient("^https?://.*", httpClient)      // HTTP/HTTPS
addClient("^file:.*", fileSystemClient)    // File system
addClient("^ftp://.*", ftpClient)          // FTP
addClient("^smb://.*", smbClient)          // SMB/CIFS
addClient("^storage://.*", storageClient)  // Cloud storage
```

#### CrawlerClient Interface (`CrawlerClient.java`)

**Key Methods**:
```java
ResponseData execute(RequestData requestData)          // Execute request
void setInitParameterMap(Map<String, Object> params)   // Initialize client
void close()                                            // Resource cleanup
```

#### Protocol-Specific Clients

**1. HcHttpClient (HTTP/HTTPS)**
- Apache HttpComponents-based implementation
- Connection pooling via `PoolingHttpClientConnectionManager`
- SSL/TLS support with configurable verification
- Cookie management with persistent storage
- Authentication: Basic, Digest, NTLM, Form-based
- Proxy support
- **Robots.txt parsing** (RFC 9309 compliant via `RobotsTxtHelper`)
- User-agent customization
- Request header customization
- Redirect handling (configurable max redirects)
- Timeout configuration (connection, socket, request)

**2. FileSystemClient (file://)**
- Local and network file system access
- File attribute extraction (owner, groups, permissions)
- Directory traversal with child URL generation
- POSIX and ACL attribute support
- Symbolic link handling

**3. StorageClient (storage://)**
- MinIO/S3-compatible object storage
- Bucket and object operations
- Metadata and tag retrieval
- Directory listing via prefix-based queries
- Streaming for large objects
- **Custom Protocol Handler**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/net/protocol/storage/Handler.java`
- **Environment Variables**:
  - `STORAGE_ENDPOINT`: MinIO endpoint URL
  - `STORAGE_ACCESS_KEY`: Access key
  - `STORAGE_SECRET_KEY`: Secret key
  - `STORAGE_REGION`: Region (optional)

**4. FtpClient (ftp://)**
- Apache Commons Net-based
- Authentication support (username, password)
- Directory listing
- Binary/ASCII mode selection
- Passive/active mode

**5. SmbClient (smb://, smb1://)**
- Windows network share access
- Authentication (domain, username, password)
- Both SMBv1 (`Smb1Client`) and SMBv2+ (`SmbClient`) support
- Directory traversal
- File attribute extraction

### Service Layer

#### UrlQueueService (`UrlQueueService.java`)
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/service/UrlQueueService.java`

**Responsibilities**:
- URL queue management (FIFO)
- Session-based URL tracking
- Duplicate URL detection via `visited()` method
- Batch URL insertion
- Session persistence

**Key Methods**:
```java
void add(String sessionId, String url)               // Add single URL
void insert(QUEUE urlQueue)                          // Insert queue entry
void offerAll(String sessionId, List<QUEUE> urls)    // Batch add
QUEUE poll(String sessionId)                         // Get next URL (blocking)
boolean visited(QUEUE urlQueue)                      // Check if URL visited
void delete(String sessionId)                        // Clean up session data
void saveSession(String sessionId)                   // Persist session
```

**Implementation**:
- `UrlQueueServiceImpl`: In-memory implementation with concurrent maps

#### DataService (`DataService.java`)
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/service/DataService.java`

**Responsibilities**:
- Access result persistence
- Result retrieval and iteration
- Session-based data management
- Count tracking

**Key Methods**:
```java
void store(RESULT accessResult)                                      // Store result
void update(RESULT accessResult)                                     // Update result
void update(List<RESULT> results)                                    // Batch update
RESULT getAccessResult(String sessionId, String url)                 // Get by URL
void iterate(String sessionId, AccessResultCallback<RESULT> callback) // Iterate all
int getCount(String sessionId)                                       // Count results
void delete(String sessionId)                                        // Delete session
```

**Implementations**:
- `DataServiceImpl`: In-memory storage (default)
- `OpenSearchDataService`: OpenSearch backend (persistent, scalable)

### Processing Pipeline

#### Rule & RuleManager
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/rule/`

**Responsibilities**:
- Pattern-based response routing
- Response processor selection
- URL/MIME type matching

**Implementations**:
- `RegexRule`: Regular expression-based matching on URL or MIME type
- `SitemapsRule`: Sitemap-specific handling

**Typical Configuration**:
```java
ruleManager.addRule(regexRule);  // First-match-wins ordering
```

#### ResponseProcessor
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/processor/ResponseProcessor.java`

**Implementations**:

**1. DefaultResponseProcessor**
- Standard HTTP response handling
- Status code validation (200, 304)
- Transformer invocation
- Child URL storage
- Access count management
- Depth limit enforcement

**2. SitemapsResponseProcessor**
- Sitemap XML processing
- URL extraction from sitemaps
- Sitemap index handling (`<sitemapindex>`)
- News, video, image sitemap support

**3. NullResponseProcessor**
- No-op processor for ignored content (e.g., CSS, JS, images)

#### Transformer
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/transformer/Transformer.java`

**Responsibilities**:
- Response data transformation
- Content extraction coordination
- Child URL discovery (link extraction)
- Metadata extraction

**Key Implementations**:
- `HtmlTransformer`: HTML parsing with link extraction (anchors, scripts, stylesheets)
- `XmlTransformer`: XML document processing
- `FileTransformer`: Generic file handling with extractor delegation
- `TextTransformer`: Plain text processing
- `BinaryTransformer`: Binary file metadata extraction
- `XpathTransformer`: XPath-based content extraction

**Typical Flow**:
```java
ResponseData → Transformer.transform()
    ↓
Read response body
    ↓
Detect encoding (EncodingHelper)
    ↓
Extract content via Extractor
    ↓
Parse for child URLs
    ↓
Return ResultData (with text + child URLs + metadata)
```

#### Extractor & ExtractorFactory
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/extractor/`

**Responsibilities**:
- Content text extraction from various formats
- MIME type-based extractor selection
- Weight-based prioritization (for multiple extractors per MIME type)
- Fallback handling via `UnsupportedExtractException`

**Weight System**:
Extractors can be assigned weights (default: 1). When multiple extractors are registered for the same MIME type, they're tried in descending weight order until one succeeds.

**Key Extractors** (in `impl/`):
- `TikaExtractor`: Apache Tika-based (supports 1000+ formats) - typically highest weight
- `HtmlExtractor`: HTML text extraction
- `HtmlXpathExtractor`: XPath-based HTML extraction
- `PdfExtractor`: PDF text extraction (PDFBox)
- `MsWordExtractor`: Microsoft Word (.doc, .docx)
- `MsExcelExtractor`: Microsoft Excel (.xls, .xlsx)
- `MsPowerPointExtractor`: Microsoft PowerPoint (.ppt, .pptx)
- `ZipExtractor`: ZIP archive processing (extracts nested files)
- `TarExtractor`: TAR archive processing
- `LhaExtractor`: LHA compression format
- `EmlExtractor`: Email message extraction
- `TextExtractor`: Plain text
- `XmlExtractor`: XML documents
- `CommandExtractor`: External command execution for extraction
- `JodExtractor`: JODConverter-based (requires LibreOffice)
- `FilenameExtractor`: Filename-based content (uses filename as content)

**Registration Example**:
```java
extractorFactory.addExtractor("text/html", htmlExtractor, 2);     // Weight 2
extractorFactory.addExtractor("text/html", tikaExtractor, 1);     // Weight 1 (fallback)
```

### Helper Utilities

#### RobotsTxtHelper
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/helper/RobotsTxtHelper.java`

**RFC 9309 Compliant Features**:
- User-agent directive parsing (case-insensitive, normalized to lowercase)
- Disallow/Allow pattern matching
- Wildcard (`*`) support in paths
- End-of-path (`$`) matching
- Crawl-delay extraction
- Sitemap directive discovery
- Priority-based matching (longest match wins, Allow beats Disallow at equal length)
- Comment stripping (`#`)
- Resilient parsing (continues on malformed lines)
- BOM handling

**Usage**:
```java
RobotsTxtHelper.parse(robotsTxtContent, userAgent)
    → Returns RobotsTxt object with allow/disallow rules, crawl-delay, sitemaps
```

#### SitemapsHelper
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/helper/SitemapsHelper.java`

**Features**:
- Sitemap XML parsing
- Sitemap index handling (`<sitemapindex>`)
- News sitemaps (`sitemap-news.xsd`)
- Video sitemaps (`sitemap-video.xsd`)
- Image sitemaps (`sitemap-image.xsd`)
- Alternate language links (`hreflang`)
- Change frequency and priority extraction

#### Other Key Helpers

**MimeTypeHelper**: MIME type detection using Apache Tika with fallback to file extension

**EncodingHelper**: Character encoding detection with BOM detection and charset normalization

**ContentLengthHelper**: Content size validation to prevent memory exhaustion

**UrlConvertHelper**: URL normalization and relative-to-absolute URL resolution

**MemoryDataHelper**: In-memory data storage with session management and concurrent access support

**LogHelper**: Structured logging interface with extensible LogType-based logging

#### Utility Classes
**Location**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/util/`

**CrawlingParameterUtil**: Thread-local parameter storage (CrawlerContext, UrlQueue, services)
**TextUtil**: Text normalization, whitespace handling, language detection
**XmlUtil**: XML parsing, namespace handling, XPath evaluation
**XPathAPI**: XPath query execution and DOM traversal
**ResponseDataUtil**: ResponseData helper methods
**TemporaryFileInputStream**: Auto-deleting temp file stream
**IgnoreCloseInputStream**: Prevents premature stream closure for multiple readers

### Entity Classes

**ResponseData** (`entity/ResponseData.java`):
- HTTP response encapsulation
- Body storage (byte array or temp file via `DeferredFileOutputStream`)
- Metadata map
- Child URL set
- Redirect handling
- **Closeable for temp file cleanup** (important!)

**AccessResult** (`entity/AccessResult.java`):
- Crawl result interface
- Stores URL, content, metadata
- Session and rule tracking
- Timing information (create time, modify time, last modified)
- HTTP status code

**RequestData** (`entity/RequestData.java`):
- HTTP request details (method, URL, headers)
- Weight for prioritization

**UrlQueue** (`entity/UrlQueue.java`):
- Queue entry abstraction
- Depth tracking
- Parent URL reference
- Last-modified timestamp
- Create time

**ExtractData** (`entity/ExtractData.java`):
- Extracted text content
- Child URLs
- Metadata map

---

## Crawler Flow and Lifecycle

### High-Level Flow

```
1. Initialization
   ├── Crawler instance created (via DI or programmatic)
   ├── Session ID generated (yyyyMMddHHmmssSSS format)
   ├── CrawlerContext initialized
   └── Components injected (clients, services, helpers)

2. URL Addition
   ├── User calls addUrl(url)
   ├── URL added to UrlQueueService
   └── URL processed by UrlFilter (include/exclude patterns)

3. Execution Start (execute())
   ├── Parent thread created
   ├── UrlFilter initialized with session
   ├── Thread group created
   ├── CrawlerThread instances created (numOfThread count)
   ├── Threads started
   └── Status set to RUNNING

4. Per-Thread Crawling Loop (CrawlerThread.run())
   ├── Poll URL from queue
   ├── Validate URL (depth, filters)
   ├── Get CrawlerClient for URL protocol (pattern matching)
   ├── Increment active thread count
   ├── Set thread-local parameters (CrawlingParameterUtil)
   ├── Pre-processing delay (IntervalController.delay(PRE_PROCESSING))
   ├── Check last-modified (HEAD request if available)
   ├── Execute request (GET/HEAD)
   ├── Match response to Rule (RuleManager)
   ├── Get ResponseProcessor from Rule
   ├── Process response
   │   ├── Check HTTP status (200, 304)
   │   ├── Transform data (Transformer)
   │   ├── Extract content (Extractor)
   │   ├── Store AccessResult (DataService)
   │   └── Extract and queue child URLs (UrlQueueService)
   ├── Add sitemaps from robots.txt (if discovered)
   ├── Post-processing delay (IntervalController.delay(POST_PROCESSING))
   ├── Cleanup (close response, clear thread-local)
   ├── Decrement active thread count
   └── Wait for new URL delay (if queue empty)

5. Thread Termination Conditions
   ├── Status == DONE (stop() called)
   ├── Max access count reached (maxAccessCount)
   ├── Thread check count exceeded with no active threads (maxThreadCheckCount)
   └── Container unavailable (shutdown)

6. Completion
   ├── All threads join (awaitTermination())
   ├── Status set to DONE
   ├── Session saved (UrlQueueService.saveSession())
   └── Resources released (AutoCloseable.close())

7. Cleanup (optional)
   ├── UrlQueueService.delete(sessionId)
   ├── DataService.delete(sessionId)
   └── UrlFilter.clear()
```

### Detailed Request Processing Flow

```
CrawlerThread.run()
  │
  ├─→ urlQueueService.poll(sessionId)
  │     └─→ Returns UrlQueue or null (blocking with timeout)
  │
  ├─→ isValid(urlQueue)
  │     ├─→ Check null/blank URL
  │     ├─→ Check max depth (if maxDepth >= 0)
  │     └─→ Check URL filter (include/exclude patterns)
  │
  ├─→ clientFactory.getClient(url)
  │     └─→ Pattern matching to find appropriate client
  │
  ├─→ startCrawling()
  │     └─→ Increment activeThreadCount (synchronized)
  │
  ├─→ intervalController.delay(PRE_PROCESSING)
  │
  ├─→ isContentUpdated(client, urlQueue)
  │     ├─→ If lastModified exists in urlQueue
  │     │     ├─→ Execute HEAD request
  │     │     ├─→ Compare timestamps
  │     │     └─→ If not modified: process 304 response, return false
  │     └─→ Return true if updated or no lastModified
  │
  ├─→ client.execute(requestData)
  │     └─→ Returns ResponseData (with status, headers, body)
  │
  ├─→ Check for redirect
  │     └─→ If redirectLocation: queue redirect URL, skip further processing
  │
  ├─→ processResponse(urlQueue, responseData)
  │     │
  │     ├─→ ruleManager.getRule(responseData)
  │     │     └─→ Pattern matching on URL and/or MIME type
  │     │
  │     ├─→ rule.getResponseProcessor()
  │     │
  │     └─→ responseProcessor.process(responseData)
  │           │
  │           ├─→ Check HTTP status (200, 304)
  │           │
  │           ├─→ transformer.transform(responseData)
  │           │     ├─→ Read response body (stream or byte array)
  │           │     ├─→ Detect encoding (EncodingHelper)
  │           │     ├─→ extractorFactory.getExtractor(mimeType)
  │           │     ├─→ extractor.getText(inputStream, params)
  │           │     ├─→ Parse for child URLs (HTML links, etc.)
  │           │     └─→ Return ResultData (text + childUrls + metadata)
  │           │
  │           ├─→ createAccessResult(responseData, resultData)
  │           │     └─→ Combine response and result data into AccessResult
  │           │
  │           ├─→ dataService.store(accessResult)
  │           │     └─→ Persist crawl result to storage backend
  │           │
  │           └─→ storeChildUrls(childUrlSet, url, depth)
  │                 ├─→ Filter by depth limit (maxDepth)
  │                 ├─→ Apply URL filter (include/exclude)
  │                 ├─→ Deduplicate (urlQueueService.visited())
  │                 ├─→ Create UrlQueue entries (depth + 1)
  │                 └─→ urlQueueService.offerAll(childUrls)
  │
  ├─→ addSitemapsFromRobotsTxt(urlQueue)
  │     └─→ Extract and queue sitemap URLs from context.robotTxtUrls
  │
  ├─→ intervalController.delay(POST_PROCESSING)
  │
  └─→ finishCrawling()
        └─→ Decrement activeThreadCount (synchronized)
```

### Interval Control Points

**IntervalController** has four control points:
1. `PRE_PROCESSING`: Before accessing URL (politeness delay)
2. `POST_PROCESSING`: After processing response
3. `NO_URL_IN_QUEUE`: When queue is empty (wait before rechecking)
4. `WAIT_NEW_URL`: Between iterations (general delay)

**Implementations**:
- `DefaultIntervalController`: Simple time-based delays (configurable per control point)
- `HostIntervalController`: Per-host politeness delays (prevents hammering a single host)

---

## Important Implementation Details

### Thread Safety Considerations

**CrawlerContext Thread Safety**:
```java
// AtomicLong for lock-free access counting
private AtomicLong accessCount = new AtomicLong(0);
accessCount.incrementAndGet();  // Thread-safe

// volatile for cross-thread visibility
private volatile CrawlerStatus status;

// Synchronized for active thread count
private int activeThreadCount;
public synchronized void incrementActiveThreadCount() { ... }
public synchronized void decrementActiveThreadCount() { ... }

// Thread-safe LRU cache for robots.txt URLs (10,000 entries)
private Set<String> robotTxtUrls = new LruHashSet<>(10000);

// Thread-local storage for sitemaps
private ThreadLocal<Set<String>> sitemaps = new ThreadLocal<>();
```

**Thread-Local Parameter Storage**:
```java
// CrawlingParameterUtil provides thread-local storage
CrawlingParameterUtil.setCrawlerContext(crawlerContext);
CrawlingParameterUtil.setUrlQueue(urlQueue);
CrawlingParameterUtil.setUrlQueueService(urlQueueService);
CrawlingParameterUtil.setDataService(dataService);

// Access from any method without passing parameters
CrawlerContext ctx = CrawlingParameterUtil.getCrawlerContext();

// IMPORTANT: Always clear after use to prevent memory leaks
CrawlingParameterUtil.clearAll();
```

**UrlQueueService Concurrency**:
- Uses `ConcurrentHashMap` for session-based queues
- `visited()` method uses synchronized maps for duplicate detection
- `poll()` is blocking with timeout

### Memory Management

**Large Response Body Handling**:
```java
// ResponseData uses DeferredFileOutputStream
// Small responses (< threshold): in-memory byte array
// Large responses (> threshold): temporary file
DeferredFileOutputStream dfos = new DeferredFileOutputStream(
    threshold,      // e.g., 1MB
    prefix,         // temp file prefix
    suffix,         // temp file suffix
    tempDir         // temp directory
);

// IMPORTANT: Always close ResponseData to delete temp files
try (ResponseData responseData = client.execute(requestData)) {
    // process response
}  // temp file deleted here if exists
```

**Temporary File Management**:
```java
// Background deletion to avoid blocking
FileUtil.deleteInBackground(tempFile);

// TemporaryFileInputStream auto-deletes on close
try (TemporaryFileInputStream in = new TemporaryFileInputStream(tempFile)) {
    // read file
}  // file deleted here
```

**Content Length Validation**:
```java
// ContentLengthHelper prevents OOM by checking size before reading
ContentLengthHelper.validateMaxLength(contentLength, maxContentLength);
// Throws MaxLengthExceededException if too large
```

**Connection Pooling**:
```java
// HcHttpClient uses PoolingHttpClientConnectionManager
connectionManager.setMaxTotal(200);               // Total connections
connectionManager.setDefaultMaxPerRoute(20);      // Per-host connections
connectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
```

### Error Handling

**Exception Hierarchy**:
```java
CrawlerSystemException           // Configuration/setup errors (unchecked)
  └─ (subclasses for specific setup issues)

CrawlingAccessException          // Runtime crawl errors (checked)
  ├─ MaxLengthExceededException  // Content too large
  ├─ ChildUrlsException          // Deferred child URL processing
  └─ (other runtime issues)

ExtractException                 // Content extraction failures (checked)
  └─ UnsupportedExtractException // No suitable extractor
```

**Error Handling Patterns**:

1. **Client Errors**: Logged and skipped (crawling continues)
2. **Extraction Errors**: Try next extractor (weight-based fallback)
3. **Robots.txt Parsing**: Continue on malformed lines (resilient parsing)
4. **Resource Cleanup**: Always in finally blocks or try-with-resources

**Fault Tolerant Client**:
```java
// FaultTolerantClient wraps any CrawlerClient
// Provides retry with exponential backoff
// Implements circuit breaker pattern
FaultTolerantClient ftClient = new FaultTolerantClient(httpClient);
ftClient.setMaxRetryCount(3);
ftClient.setRetryInterval(1000);  // Initial delay in ms
```

### Performance Optimizations

**Connection Pooling**: Reuses HTTP connections to reduce overhead

**Object Pooling**: Apache Commons Pool2 for expensive resources (Crawler instances)

**LRU Caching**:
- Robots.txt URLs: 10,000 entries
- URL visited tracking (UrlQueueService)

**Batch Operations**:
```java
// Batch URL insertion reduces overhead
urlQueueService.offerAll(sessionId, childUrlList);

// Batch data update
dataService.update(accessResultList);
```

**Deferred File Output**: Large content written to temp files (not memory)

**Lazy Initialization**: Expensive resources initialized on first use

### Logging

**Logging Stack**:
- API: SLF4J
- Implementation: Log4j2
- Structured logging via LogType enum

**LogHelper**:
```java
public interface LogHelper {
    void log(LogType logType, Object... objs);
}

// LogType values:
// - CHECK_LAST_MODIFIED
// - GET_CONTENT
// - START_THREAD
// - FINISHED_THREAD
// - PROCESS_CHILD_URLS
// etc.
```

**Logging Best Practices**:
- Use SLF4J for all logging
- Include session ID in log messages for traceability
- Use appropriate log levels (TRACE, DEBUG, INFO, WARN, ERROR)
- Log important state transitions (thread start/stop, status changes)

---

## Development Guidelines

### Code Style

1. **Follow Java Coding Conventions**
   - Use 4 spaces for indentation (not tabs)
   - Opening brace on same line
   - Maximum line length: 120 characters
   - Use meaningful variable names

2. **JavaDoc Comments**
   - **Required** for all public APIs (classes, interfaces, public methods)
   - Include `@param`, `@return`, `@throws` as appropriate
   - Include usage examples for complex APIs

3. **License Headers**
   - All source files must include Apache License 2.0 header
   - Run `mvn license:format` before committing

4. **Code Formatting**
   - Run `mvn formatter:format` before committing
   - Formatter configuration in parent POM

### Building and Testing

**Build Commands**:
```bash
# Build all modules
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests

# Build specific module
mvn clean install -pl fess-crawler

# Format code
mvn formatter:format

# Update license headers
mvn license:format
```

**Running Tests**:
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CrawlerTest

# Run specific test method
mvn test -Dtest=CrawlerTest#test_execute_web

# Run tests for specific module
mvn test -pl fess-crawler
```

**Test Coverage**:
```bash
# Generate coverage report
mvn jacoco:report

# View report at:
# target/site/jacoco/index.html
```

### Contributing Workflow

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Make changes** with clear, focused commits
4. **Add tests** for new functionality
5. **Format code** (`mvn formatter:format` && `mvn license:format`)
6. **Run tests** (`mvn test`)
7. **Commit** changes (`git commit -m 'Add amazing feature'`)
8. **Push** to branch (`git push origin feature/amazing-feature`)
9. **Open** a Pull Request

### Best Practices for AI Assistants

#### When Adding New Features

1. **Read Existing Code First**: Always read related code before making changes
2. **Follow Existing Patterns**: Use the same design patterns and code style
3. **Add Tests**: Include unit tests and integration tests
4. **Update Documentation**: Update JavaDoc and README if needed
5. **Check Thread Safety**: Consider multi-threading implications
6. **Handle Resources**: Use try-with-resources or finally blocks for cleanup
7. **Validate Input**: Check for null, validate ranges, handle edge cases

#### When Fixing Bugs

1. **Write a Failing Test First**: Reproduces the bug
2. **Understand Root Cause**: Don't just fix symptoms
3. **Minimal Changes**: Fix only what's necessary
4. **Verify Fix**: Ensure test passes and no regressions
5. **Add Logging**: If appropriate for debugging

#### When Refactoring

1. **Preserve Behavior**: Don't change functionality
2. **Keep Tests Green**: All tests should pass before and after
3. **Small Steps**: Make incremental changes
4. **Run Tests Frequently**: After each small change
5. **Don't Mix**: Keep refactoring and new features separate

#### Code Quality Checklist

- [ ] Follows Java coding conventions
- [ ] Includes JavaDoc for public APIs
- [ ] Includes unit tests with good coverage
- [ ] All tests pass
- [ ] No compiler warnings
- [ ] Proper exception handling
- [ ] Resource cleanup (AutoCloseable, try-with-resources)
- [ ] Thread-safe if accessed by multiple threads
- [ ] License headers present
- [ ] Code formatted (`mvn formatter:format`)
- [ ] No obvious performance issues
- [ ] No security vulnerabilities (SQL injection, XSS, etc.)

---

## Testing Strategy

### Test Structure

**Test Directory**: `/home/user/fess-crawler/fess-crawler/src/test/java/org/codelibs/fess/crawler/`

```
src/test/java/
├── CrawlerTest.java              # Integration tests (end-to-end)
├── CrawlerContextTest.java       # Context unit tests
├── CrawlerThreadTest.java        # Thread logic tests
├── CrawlerStatusTest.java        # Status enum tests
├── builder/                      # Builder tests
├── client/                       # Client tests
│   ├── CrawlerClientFactoryTest.java
│   ├── http/                     # HTTP client tests
│   ├── fs/                       # File system tests
│   ├── ftp/                      # FTP tests
│   ├── smb/                      # SMB tests
│   └── storage/                  # Storage tests
├── entity/                       # Entity tests
├── exception/                    # Exception tests
├── extractor/                    # Extractor tests
│   ├── ExtractorFactoryTest.java
│   └── impl/                     # Per-format extractor tests
├── filter/                       # Filter tests
├── helper/                       # Helper tests (robots.txt, sitemaps, etc.)
├── interval/                     # Interval controller tests
├── pool/                         # Pool tests
├── processor/                    # Processor tests
├── rule/                         # Rule tests
├── service/                      # Service tests
├── transformer/                  # Transformer tests
└── util/                         # Utility tests
```

### Testing Technologies

- **JUnit 4**: Primary test framework
- **UTFlute**: DBFlute testing utility for DI testing
- **Testcontainers**: Integration testing with containers (Docker-based)
- **Mockito 5.7.0**: Mocking framework
- **Embedded Servers**:
  - Jetty 6.1.26: HTTP tests
  - Apache FTPServer 1.1.1: FTP tests
- **CrawlerWebServer**: Custom test server utility

### Test Categories

#### 1. Unit Tests
**Purpose**: Test individual classes in isolation

**Pattern**:
```java
public class MyExtractorTest extends UTFlute {
    private MyExtractor extractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        extractor = new MyExtractor();
    }

    public void test_getText_normalCase() throws Exception {
        // Given
        InputStream in = getClass().getResourceAsStream("sample.txt");

        // When
        ExtractData result = extractor.getText(in, Collections.emptyMap());

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().contains("expected text"));
    }
}
```

**Best Practices**:
- Use descriptive test names: `test_methodName_scenario`
- Test one thing per test method
- Use arrange-act-assert pattern (given-when-then)
- Mock dependencies with Mockito
- Use test resources in `src/test/resources/`

#### 2. Integration Tests
**Purpose**: Test end-to-end crawling scenarios

**Pattern** (from `CrawlerTest.java`):
```java
public void test_execute_web() throws Exception {
    // Given: Start embedded web server
    CrawlerWebServer server = new CrawlerWebServer(7070);
    server.start();

    // When: Execute crawler
    Crawler crawler = container.getComponent("crawler");
    crawler.addUrl("http://localhost:7070/");
    String sessionId = crawler.execute();

    // Then: Verify results
    DataService dataService = container.getComponent("dataService");
    assertEquals(10, dataService.getCount(sessionId));

    // Cleanup
    server.stop();
}
```

**Best Practices**:
- Use embedded servers (Jetty, FTPServer) for integration tests
- Test realistic scenarios
- Verify complete flow (URL queue → client → processor → storage)
- Clean up resources in finally or @After

#### 3. Error Handling Tests
**Purpose**: Test exception scenarios and error recovery

**Pattern**:
```java
public void test_extractor_unsupportedFormat() throws Exception {
    // Given
    ExtractorFactory factory = new ExtractorFactory();

    // When/Then
    try {
        factory.getExtractor("application/unknown").getText(null, null);
        fail("Should throw UnsupportedExtractException");
    } catch (UnsupportedExtractException e) {
        // Expected
    }
}
```

**Best Practices**:
- Test all exception paths
- Verify error messages
- Test resource cleanup on errors
- Use `@Test(expected = ExceptionType.class)` for simple cases

#### 4. Resource Management Tests
**Purpose**: Verify proper cleanup and no memory leaks

**Pattern** (from `ExtractorResourceManagementTest.java`):
```java
public void test_tempFileCleanup() throws Exception {
    // Given
    File tempFile = File.createTempFile("test", ".tmp");

    // When
    try (TemporaryFileInputStream in = new TemporaryFileInputStream(tempFile)) {
        // Read file
    }

    // Then: File should be deleted
    assertFalse(tempFile.exists());
}
```

**Best Practices**:
- Test try-with-resources cleanup
- Verify temp files are deleted
- Check connection pool release
- Monitor thread lifecycle

#### 5. Thread Safety Tests
**Purpose**: Verify concurrent access safety

**Pattern**:
```java
public void test_concurrentAccess() throws Exception {
    // Given
    CrawlerContext context = new CrawlerContext();
    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    // When: Multiple threads increment access count
    for (int i = 0; i < threadCount; i++) {
        executor.submit(() -> {
            for (int j = 0; j < 100; j++) {
                context.incrementAndGetAccessCount();
            }
        });
    }

    // Then: Verify correct total
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
    assertEquals(1000, context.getAccessCount());
}
```

### Test Data

**Test Resources**: `/home/user/fess-crawler/fess-crawler/src/test/resources/`

**Common Test Files**:
- HTML samples: `test.html`, `test_utf8.html`, `test_sjis.html`
- Office documents: `test.doc`, `test.docx`, `test.xls`, `test.xlsx`, `test.ppt`, `test.pptx`
- PDFs: `test.pdf`, `test_encrypted.pdf`
- Archives: `test.zip`, `test.tar`, `test.tar.gz`
- Images: `test.jpg`, `test.png`, `test.gif`

**Creating Test Files**:
1. Place in `src/test/resources/`
2. Load in tests: `getClass().getResourceAsStream("test.txt")`
3. Keep files small (< 100KB) to avoid bloating repository

### Mock Usage

**Mockito Patterns**:
```java
// Mock creation
CrawlerClient mockClient = mock(CrawlerClient.class);

// Stub method
when(mockClient.execute(any(RequestData.class)))
    .thenReturn(responseData);

// Verify interaction
verify(mockClient, times(1)).execute(any(RequestData.class));

// Argument capture
ArgumentCaptor<RequestData> captor = ArgumentCaptor.forClass(RequestData.class);
verify(mockClient).execute(captor.capture());
assertEquals("http://example.com", captor.getValue().getUrl());
```

### Test Coverage Goals

- **Unit Tests**: > 80% line coverage
- **Integration Tests**: Cover all major protocols and formats
- **Error Handling**: All exception types tested
- **Resource Management**: All AutoCloseable tested

### Running Tests in CI

**GitHub Actions Configuration** (`.github/workflows/maven.yml`):
```yaml
- name: Build with Maven
  run: mvn clean install -B
- name: Run tests
  run: mvn test -B
```

**Excluded Tests**:
```xml
<!-- In pom.xml -->
<excludes>
    <exclude>**/JodExtractorTest.java</exclude>  <!-- Requires LibreOffice -->
</excludes>
```

---

## Common Tasks and Patterns

### Adding a New Protocol Client

1. **Implement CrawlerClient interface**:
```java
public class MyProtocolClient extends AbstractCrawlerClient {
    @Override
    public ResponseData execute(RequestData request) {
        // Implementation
    }

    @Override
    public void close() {
        // Cleanup
    }
}
```

2. **Register with CrawlerClientFactory**:
```java
// In XML config (crawler/client.xml)
<component name="myProtocolClient" class="...MyProtocolClient" instance="singleton"/>

// In CrawlerClientFactory configuration
clientFactory.addClient("^myprotocol://.*", myProtocolClient);
```

3. **Add tests**: Unit tests and integration tests

### Adding a New Content Extractor

1. **Implement Extractor interface**:
```java
public class MyFormatExtractor extends AbstractExtractor {
    @Override
    public ExtractData getText(InputStream in, Map<String, String> params) {
        ExtractData data = new ExtractData();
        // Extract text from input stream
        data.setContent(extractedText);
        return data;
    }
}
```

2. **Register with ExtractorFactory**:
```java
// In XML config (crawler/extractor.xml)
<component name="myFormatExtractor" class="...MyFormatExtractor" instance="singleton"/>

// In ExtractorFactory configuration
extractorFactory.addExtractor("application/myformat", myFormatExtractor, weight);
```

3. **Add test with sample file**:
```java
public class MyFormatExtractorTest extends UTFlute {
    public void test_getText() throws Exception {
        MyFormatExtractor extractor = new MyFormatExtractor();
        InputStream in = getClass().getResourceAsStream("sample.myformat");
        ExtractData result = extractor.getText(in, Collections.emptyMap());
        assertTrue(result.getContent().contains("expected"));
    }
}
```

### Adding a New Transformer

1. **Extend AbstractTransformer**:
```java
public class MyTransformer extends AbstractTransformer {
    @Override
    public ResultData transform(ResponseData responseData) {
        ResultData resultData = new ResultData();
        // Transform response
        resultData.setData(...);
        return resultData;
    }
}
```

2. **Register in DI config**:
```java
<component name="myTransformer" class="...MyTransformer" instance="singleton"/>
```

3. **Configure in Rule**:
```java
RegexRule rule = new RegexRule();
rule.addRule("url", ".*\\.myext$");
rule.setResponseProcessor(responseProcessor);  // Uses myTransformer
```

### Configuring URL Filtering

```java
// Include patterns (whitelist)
crawler.urlFilter.addInclude("https://example.com/.*");
crawler.urlFilter.addInclude("https://example.com/docs/.*\\.html$");

// Exclude patterns (blacklist)
crawler.urlFilter.addExclude(".*\\.(css|js|png|jpg|gif)$");
crawler.urlFilter.addExclude(".*login.*");
crawler.urlFilter.addExclude(".*/admin/.*");

// Processing order:
// 1. Check include patterns (must match at least one)
// 2. Check exclude patterns (must not match any)
// 3. If both pass, URL is accepted
```

### Setting Up Crawl Limits

```java
// Maximum number of URLs to crawl (0 = unlimited)
crawler.crawlerContext.setMaxAccessCount(1000);

// Maximum depth (-1 = unlimited, 0 = only seed URLs, 1 = seed + direct links, etc.)
crawler.crawlerContext.setMaxDepth(3);

// Number of concurrent crawler threads
crawler.crawlerContext.setNumOfThread(10);

// Thread monitoring frequency (check for termination every N URLs)
crawler.crawlerContext.setMaxThreadCheckCount(20);
```

### Implementing Politeness Delays

```java
// Configure DefaultIntervalController
DefaultIntervalController intervalController = new DefaultIntervalController();

// Delay before each URL access (milliseconds)
intervalController.setDefaultIntervalTime(1000);  // 1 second

// Delay when no URLs in queue (wait before rechecking)
intervalController.setDelayMillisForWaitingNewUrl(5000);  // 5 seconds

// Delay for new URL wait
intervalController.setDelayMillisForWaitingNewUrl(500);

crawler.setIntervalController(intervalController);
```

### Accessing Crawl Results

```java
DataService dataService = container.getComponent("dataService");

// Get count of crawled URLs
int count = dataService.getCount(sessionId);

// Get specific result by URL
AccessResult result = dataService.getAccessResult(sessionId, "http://example.com");
if (result != null) {
    System.out.println("Content: " + result.getContent());
    System.out.println("MIME Type: " + result.getMimeType());
    System.out.println("Status: " + result.getHttpStatusCode());
}

// Iterate all results
dataService.iterate(sessionId, accessResult -> {
    System.out.println("URL: " + accessResult.getUrl());
    System.out.println("Content: " + accessResult.getContent());
    // Process result
});

// Clean up session data
dataService.delete(sessionId);
```

### Handling Authentication

**HTTP Basic/Digest Authentication**:
```java
HcHttpClient httpClient = container.getComponent("httpClient");

// Basic auth
AuthenticationImpl auth = new AuthenticationImpl(
    "example.com",  // host
    80,             // port
    "username",
    "password"
);
httpClient.addAuthentication(auth);

// Form-based auth
FormAuthentication formAuth = new FormAuthentication();
formAuth.setLoginUrl("http://example.com/login");
formAuth.setLoginParameterMap(Map.of(
    "username", "user",
    "password", "pass"
));
httpClient.addAuthentication(formAuth);
```

### Custom Response Processing

```java
// Create custom ResponseProcessor
public class MyResponseProcessor extends DefaultResponseProcessor {
    @Override
    public void process(ResponseData responseData) {
        // Custom processing
        super.process(responseData);  // Call default processing
        // Additional logic
    }
}

// Register and use
RegexRule rule = new RegexRule();
rule.addRule("url", ".*\\.custom$");
rule.setResponseProcessor(new MyResponseProcessor());
ruleManager.addRule(rule);
```

### Working with Robots.txt

```java
// Robots.txt is automatically fetched and parsed by HcHttpClient
// Configure in HcHttpClient
httpClient.setRobotsTxtEnabled(true);

// Custom user agent for robots.txt matching
httpClient.setUserAgent("MyBot/1.0");

// Access robots.txt helper directly
RobotsTxtHelper helper = container.getComponent("robotsTxtHelper");
RobotsTxt robotsTxt = helper.parse(robotsTxtContent, "MyBot");

// Check if URL is allowed
boolean allowed = robotsTxt.allows("/path/to/page");

// Get crawl delay
Long delay = robotsTxt.getCrawlDelay();  // null if not specified

// Get sitemaps
List<String> sitemaps = robotsTxt.getSitemaps();
```

### Processing Sitemaps

```java
// Sitemaps are automatically discovered from robots.txt
// Configure SitemapsResponseProcessor
SitemapsResponseProcessor sitemapsProcessor = new SitemapsResponseProcessor();

// Configure SitemapsRule
SitemapsRule sitemapsRule = new SitemapsRule();
sitemapsRule.addRule("url", ".*sitemap.*\\.xml$");
sitemapsRule.setResponseProcessor(sitemapsProcessor);

ruleManager.addRule(sitemapsRule);

// Add sitemap URL directly
crawler.addUrl("https://example.com/sitemap.xml");
```

### Background Crawling

```java
// Set background mode
crawler.setBackground(true);

// Execute (returns immediately)
String sessionId = crawler.execute();

// Monitor progress
while (crawler.crawlerContext.getStatus() == CrawlerStatus.RUNNING) {
    int count = dataService.getCount(sessionId);
    System.out.println("Crawled: " + count + " URLs");
    Thread.sleep(1000);
}

// Wait for completion
crawler.awaitTermination();
System.out.println("Crawling completed");
```

### Error Recovery

```java
// Use FaultTolerantClient for automatic retries
FaultTolerantClient ftClient = new FaultTolerantClient(httpClient);
ftClient.setMaxRetryCount(3);
ftClient.setRetryInterval(1000);  // Initial delay in ms

// Register fault-tolerant client
clientFactory.addClient("^https?://.*", ftClient);

// Errors are logged and crawling continues
// Check logs for failed URLs
```

---

## Key File Locations Reference

### Core Classes
- **Crawler**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/Crawler.java`
- **CrawlerContext**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/CrawlerContext.java`
- **CrawlerThread**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/CrawlerThread.java`

### Clients
- **HcHttpClient**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/client/http/HcHttpClient.java`
- **FileSystemClient**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/client/fs/FileSystemClient.java`
- **StorageClient**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/client/storage/StorageClient.java`
- **Storage Protocol Handler**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/net/protocol/storage/Handler.java`

### Services
- **UrlQueueService**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/service/UrlQueueService.java`
- **DataService**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/service/DataService.java`

### Helpers
- **RobotsTxtHelper**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/helper/RobotsTxtHelper.java`
- **SitemapsHelper**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/helper/SitemapsHelper.java`
- **MimeTypeHelper**: `/home/user/fess-crawler/fess-crawler/src/main/java/org/codelibs/fess/crawler/helper/MimeTypeHelper.java`

### Configuration
- **Main DI Config**: `/home/user/fess-crawler/fess-crawler-lasta/src/main/resources/crawler.xml`
- **Parent POM**: `/home/user/fess-crawler/pom.xml`
- **Core POM**: `/home/user/fess-crawler/fess-crawler/pom.xml`

---

## Additional Resources

- **README**: `/home/user/fess-crawler/README.md`
- **GitHub**: https://github.com/codelibs/fess-crawler
- **License**: Apache License 2.0
- **Issues**: https://github.com/codelibs/fess-crawler/issues

---

**Last Updated**: This document was generated based on fess-crawler version 15.2.0-SNAPSHOT.

**For AI Assistants**: When working on this project, always:
1. Read the relevant code before making changes
2. Follow existing patterns and conventions
3. Add comprehensive tests
4. Handle resources properly (AutoCloseable, try-with-resources)
5. Consider thread safety
6. Format code before committing (`mvn formatter:format`)
7. Update this document if adding major new features or patterns
