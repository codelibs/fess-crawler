# CLAUDE.md - Fess Crawler Development Guide

Quick reference for AI assistants working on the Fess Crawler project.

## Project Overview

**Fess Crawler** is a Java-based web crawling framework for enterprise content extraction. It is the
crawling engine behind [Fess](https://github.com/codelibs/fess) and is also usable standalone.

### Essential Info

- **Language**: Java 21 (`<release>21</release>`)
- **Build**: Maven, parent POM is `org.codelibs.fess:fess-parent` (branch `main`)
- **License**: Apache 2.0
- **DI**: Lasta DI (`fess-crawler-lasta`) or the standalone `StandardCrawlerContainer`
- **Repo**: https://github.com/codelibs/fess-crawler
- **Default branch**: `master`

### Tech Stack

- **HTTP**: Apache HttpComponents 5.x (default) and 4.5.x (fallback), switchable at runtime
- **Extraction**: Apache Tika, POI, PDFBox, commonmark, commons-csv, jlha, jhighlight, JODConverter
- **Testing**: **JUnit 5 (Jupiter)**, UTFlute, Mockito, Testcontainers
- **Storage**: In-memory (default), OpenSearch (optional, via fesen-httpclient)
- **Cloud**: AWS SDK v2 (S3), Google Cloud Storage, MinIO

### Protocols

HTTP/HTTPS, File, FTP/FTPS, SMB/CIFS (SMB2+ via SMBJ, SMB1 via JCIFS), Storage (MinIO,
`storage://`), S3 (`s3://`), GCS (`gcs://`)

### Content Formats

Office (Word, Excel, PowerPoint, Publisher, Visio), PDF, PostScript, Archives (ZIP, TAR, LHA), HTML,
XML, JSON, CSV, Markdown, Email (EML), Media metadata, Images (EXIF/IPTC/XMP)

---

## Architecture

### Module Structure

```
fess-crawler-parent/
├── fess-crawler/              # Core framework
├── fess-crawler-lasta/        # Lasta DI integration + shipped crawler.xml wiring
└── fess-crawler-opensearch/   # OpenSearch backend
```

### Key Design Patterns

- **Factory**: `CrawlerClientFactory`, `ExtractorFactory` - protocol/format-specific component selection
- **Strategy**: `CrawlerClient`, `Extractor`, `Transformer` - pluggable implementations
- **Builder**: `RequestDataBuilder`, `ExtractorBuilder` - fluent construction
- **Template Method**: `AbstractCrawlerClient`, `AbstractExtractor` - common logic with overrides
- **DI**: Lasta DI container with `@Resource` and XML config

### Core Principles

**Thread Safety**: `AtomicLong` for counters, `volatile` for status flags, synchronized blocks, thread-local storage via `CrawlingParameterUtil`

**Resource Management**: `AutoCloseable` throughout, `DeferredFileOutputStream` for large responses, connection pooling, background temp file deletion via `FileUtil.deleteInBackground()`

**Fault Tolerance**: `FaultTolerantClient` wrapper (retry, circuit breaker), `SwitchableHttpClient` for HTTP client fallback

**Bounded Processing**: content-length caps enforced *during* download (not after), archive-bomb / Zip
Slip / recursion-depth defenses in the archive and EML extractors, output caps on `CommandExtractor`

---

## Key Components

### Core Classes

- **Crawler** (`Crawler.java`): Main orchestrator - `execute()`, `addUrl()`, `cleanup()`, `stop()`, `close()`. Implements `Runnable, AutoCloseable`
- **CrawlerContext** (`CrawlerContext.java`): Execution context - `sessionId`, `status`, `accessCount`, `numOfThread`, `maxDepth`, `maxAccessCount`
- **CrawlerThread** (`CrawlerThread.java`): Worker thread - Poll URL → Validate → Execute → Process → Queue children
- **CrawlerStatus** (`CrawlerStatus.java`): `INITIALIZING`, `RUNNING`, `DONE`

> **Public-API trap**: `Crawler.crawlerContext` and `Crawler.urlFilter` are **`protected`**, not
> public. In-repo tests reach them only because they sit in the same package. External code must use
> `getCrawlerContext()` / `getUrlFilter()`, or the delegating setters `setNumOfThread(int)`,
> `setMaxDepth(int)`, `setMaxAccessCount(long)`, `setMaxThreadCheckCount(int)`,
> `addIncludeFilter(String)`, `addExcludeFilter(String)`. Do not write docs or examples that touch
> the fields directly.

### HTTP Client Architecture

```
SwitchableHttpClient extends FaultTolerantClient implements CrawlerClient
    └── delegates (not inherits) to one of:
            ├── Hc5HttpClient (default) - Apache HttpComponents 5.x
            └── Hc4HttpClient (fallback) - Apache HttpComponents 4.x

AbstractCrawlerClient
    └── HcHttpClient (abstract; constants only, no setters, no instances)
            ├── Hc4HttpClient
            └── Hc5HttpClient
```

Switch via system property `fess.crawler.http.client` (`SwitchableHttpClient.HTTP_CLIENT_PROPERTY`):
`-Dfess.crawler.http.client=hc4` selects HC4; **any other value, including unset, selects HC5**. It
is read once in the constructor, so it cannot change per request.

**Key setters** (on the concrete classes — the names differ from the obvious guesses):
`setConnectionTimeout`, `setSoTimeout` (*not* `setSocketTimeout`), `setMaxTotalConnections` (*not*
`setMaxConnections`; init-param key is the singular `maxTotalConnection`), `setMaxConnectionsPerRoute`,
`setUserAgent`, `setProxyHost`/`setProxyPort`, `setIgnoreSslCertificate` (*not*
`setTrustAllCertificates`), `setRedirectsEnabled`, `setUseRobotsTxtAllows`/`setUseRobotsTxtDisallows`.

Generic knobs inherited from `AbstractCrawlerClient`: `accessTimeout`, `maxContentLength`,
`maxCachedContentSize`. All settable via `setInitParameterMap(Map<String, Object>)`.

### CrawlerClientFactory

Pattern-based client selection (from `crawler/client.xml`):
- `http:.*`, `https:.*` → `SwitchableHttpClient`
- `file:.*` → `FileSystemClient`
- `smb:.*` → `smb.SmbClient` (SMB2+), `smb1:.*` → `smb1.SmbClient` (SMB1)
- `ftp:.*`, `ftps:.*` → `FtpClient` (one shared component)
- `storage:.*` → `StorageClient`, `s3:.*` → `S3Client`, `gcs:.*` → `GcsClient`

### Cloud Storage Clients

- **S3Client**: AWS SDK v2, `s3://bucket/path`, init params: `accessKey`, `secretKey`, `endpoint`,
  `region` (default `us-east-1`), `crossRegionAccessEnabled` (default `true`). Blank
  accessKey/secretKey → falls back to the AWS **default credentials provider chain** (IAM role,
  instance profile, IRSA, env vars, shared profile)
- **GcsClient**: Google Cloud SDK, `gcs://bucket/path`, init params: `projectId`, `credentialsFile`,
  `endpoint`. No `credentialsFile` → Application Default Credentials
- **StorageClient**: MinIO SDK, `storage://bucket/path`

### Services

- **UrlQueueService**: URL queue management (FIFO), duplicate detection
- **DataService**: Access result persistence, iteration. Generic: `DataService<RESULT extends AccessResult<?>>`
- **UrlFilterService**: include/exclude pattern persistence
- Implementations: `UrlQueueServiceImpl`, `DataServiceImpl`, `UrlFilterServiceImpl` (in-memory,
  all backed by `MemoryDataHelper`); `OpenSearchDataService`, `OpenSearchUrlQueueService`,
  `OpenSearchUrlFilterService` (persistent)

`AccessResult` carries `getUrl()`, `getHttpStatusCode()`, `getMimeType()`, `getContentLength()`.
Extracted content is **not** on `AccessResult` — it lives on
`getAccessResultData().getDataAsString()`.

### Processing Pipeline

```
CrawlerThread → Client → ResponseProcessor → Transformer → Extractor → ExtractData
                                                                            ↓
                         ← UrlQueueService ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ←
                         ← DataService ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ←
```

- **Rule**: Pattern-based response routing (`RegexRule`, `SitemapsRule`)
- **ResponseProcessor**: `DefaultResponseProcessor`, `SitemapsResponseProcessor`, `NullResponseProcessor`
- **Transformer**: `HtmlTransformer`, `XmlTransformer`, `FileTransformer`, `BinaryTransformer`, `TextTransformer`
- **Extractor**: Weight-based selection — on a MIME-type collision, `ExtractorFactory` returns a
  composite that tries each extractor in descending `getWeight()` order (default weight `1`)

### Extractors

Present in `extractor/impl/`: `ApiExtractor`, `CommandExtractor`, `CsvExtractor`, `EmlExtractor`,
`FilenameExtractor`, `HtmlExtractor`, `HtmlXpathExtractor`, `JodExtractor`, `JsonExtractor`,
`LhaExtractor`, `MarkdownExtractor`, `MsExcelExtractor`, `MsPowerPointExtractor`,
`MsPublisherExtractor`, `MsVisioExtractor`, `MsWordExtractor`, `PdfExtractor`, `PsExtractor`,
`TarExtractor`, `TextExtractor`, `TikaExtractor`, `XmlExtractor`, `ZipExtractor`.
Bases: `AbstractExtractor`, `AbstractXmlExtractor`, `PasswordBasedExtractor`.

**Not registered in the default DI** — must be wired manually: `ZipExtractor`, `TarExtractor`,
`ApiExtractor`, `CommandExtractor`.

`crawler/extractor.xml` declares 19 components but `extractorFactory` maps only 10 by MIME type
(xml, html, pdf, lha, eml, tika catch-all, json, csv, markdown, ps); the rest are name-addressable
only and are wired by Fess itself.

**`HtmlExtractor`**: `extractDefaultMetadata` defaults to **true** (title, description, OpenGraph,
Twitter Card); `extractJsonLd` defaults to **false** and is bounded in nesting depth, size and count.

**DI trap**: redeclaring `extractorFactory` in an outer DI file that `<include>`s `crawler.xml`
**replaces** it and silently drops every default MIME mapping. Append instead:
`container.getComponent("extractorFactory").addExtractor(mimeType, extractor)` after init.

### Helpers

- **RobotsTxtHelper**: RFC 9309 parsing, user-agent matching, crawl-delay, sitemaps
- **SitemapsHelper**: Sitemap XML parsing, index handling
- **MimeTypeHelper** / `MimeTypeHelperImpl`: MIME detection via Tika, extension-based overrides
- **EncodingHelper**: Charset detection with BOM
- **UrlConvertHelper**: URL normalization
- **ContentLengthHelper**: `defaultMaxLength` (10 MB) + per-MIME-type `addMaxLength()`. Owns *all*
  content-size limits — transformers have no `setMaxContentSize`
- **LogHelper** / `LogHelperImpl`: crawl event logging
- **MemoryDataHelper**: in-memory store backing the three `*ServiceImpl` classes

### Interval Control

`DefaultIntervalController` has exactly four `long` millisecond knobs — there is **no**
`setDefaultIntervalTime`: `delayMillisBeforeProcessing`, `delayMillisAfterProcessing`,
`delayMillisAtNoUrlInQueue`, `delayMillisForWaitingNewUrl`.

---

## Development Workflow

### Build Commands

```bash
mvn clean install              # Build all
mvn clean install -DskipTests  # Skip tests
mvn test                       # Run tests
mvn package                    # What CI runs - includes the javadoc + license gates
mvn formatter:format           # Format code
mvn license:format             # Update license headers
mvn javadoc:jar                # Verify the javadoc gate on its own
```

`fess-parent` (branch `main`) must be installed locally when building an unreleased version.

### CI

`.github/workflows/maven.yml`, on push/PR to `master` and `*.x`:
single **JDK 21** (Temurin), installs `fess-parent`, then `mvn -B package`.

**`mvn test` is not enough.** `package` additionally runs `license:check`, `formatter:format`,
`jacoco:report`, `source:jar-no-fork` and **`javadoc:jar`**. Javadoc errors — e.g. dropping an import
while a `{@link Type}` still references it — fail CI while `mvn test` stays green. Always run
`mvn package` (or at least `mvn javadoc:jar`) before pushing.

### Code Style

- 4 spaces (no tabs), opening brace on same line, **max line length 140**
- Config is remote, from `fess-parent`: `https://www.codelibs.org/assets/formatter/eclipse-formatter-1.1.xml`
  (`lineSplit=140`, `comment.line_length=80`). Do not add a local formatter file
- JavaDoc required for public APIs (enforced by the CI javadoc gate)
- Apache 2.0 license headers required (`license:check` fails the build without them)

### Testing

- **Framework**: JUnit 5 (Jupiter). There is no `junit:junit` dependency — never add `org.junit.Test`
- **Base class**: `PlainTestCase` (UTFlute) for core; `LastaDiTestCase` for the DI/OpenSearch modules
- **Setup hook**: `protected void setUp(final TestInfo testInfo)`, not JUnit 4's `setUp()`
- **Structure**: `src/test/java/org/codelibs/fess/crawler/`, resources in `src/test/resources/`
- **Parallel**: surefire runs test **classes** in parallel (`threadCount=4`, `forkCount=1C`) — tests
  must not share mutable static state
- `JodExtractorTest` is excluded from the default run (needs local LibreOffice)
- Testcontainers tests need Docker: `S3ClientTest`, `StorageClientTest`, `GcsClientTest`,
  `smb/SmbClientTest`, `smb1/SmbClientTest`
- **Coverage**: JaCoCo reports during `package`, but **no threshold is enforced** — >80% is a goal,
  not a gate

### Contributing

1. Fork repo, create feature branch off `master`
2. Make focused commits with tests
3. Format code (`mvn formatter:format && mvn license:format`)
4. Run `mvn package` (not just `mvn test`)
5. Open Pull Request

---

## Quick Reference

### Key File Locations

**Core**: `fess-crawler/src/main/java/org/codelibs/fess/crawler/`
- `Crawler.java`, `CrawlerContext.java`, `CrawlerThread.java`, `CrawlerStatus.java`

**Clients**: `fess-crawler/src/main/java/org/codelibs/fess/crawler/client/`
- root: `CrawlerClient.java`, `AbstractCrawlerClient.java`, `CrawlerClientFactory.java`,
  `CrawlerClientFactoryWrapper.java`, `CrawlerClientCreator.java`, `FaultTolerantClient.java`,
  `AccessTimeoutTarget.java`
- `http/` - `HcHttpClient.java`, `Hc4HttpClient.java`, `Hc5HttpClient.java`, `SwitchableHttpClient.java`,
  `Hc4Authentication.java`, `Hc5Authentication.java`, `RequestHeader.java`, `*ConnectionMonitorTarget.java`
- `http/config/` - `CookieConfig`, `CredentialsConfig`, `WebAuthenticationConfig`
- `http/conn/` - `IdnDnsResolver`, `Hc5IdnDnsResolver`
- `http/form/` - `Hc4FormScheme`, `Hc5FormScheme`
- `http/ntlm/` - `JcifsEngine`, `Hc5JcifsEngine`, `SmbjEngine`, `NTLMSchemeProvider`, `Hc5NTLMSchemeFactory`
- `fs/FileSystemClient.java`, `ftp/FtpClient.java`
- `smb/SmbClient.java`, `smb1/SmbClient.java`
- `storage/StorageClient.java`, `s3/S3Client.java`, `gcs/GcsClient.java`

**DI Config**: `fess-crawler-lasta/src/main/resources/`
- `crawler.xml` (root), and under `crawler/`: `client.xml`, `container.xml`, `contentlength.xml`,
  `encoding.xml`, `extractor.xml`, `filter.xml`, `interval.xml`, `log.xml`, `mimetype.xml`,
  `robotstxt.xml`, `rule.xml`, `sitemaps.xml`, `transformer.xml`, `transformer_basic.xml`,
  `urlconverter.xml`

**OpenSearch**: `fess-crawler-opensearch/src/main/resources/crawler_opensearch.xml`, `mapping/`

### Reference Wiring

- **Lasta DI**: `fess-crawler-lasta/src/test/java/org/codelibs/fess/crawler/CrawlerTest.java` — the
  short path; `SingletonLaContainerFactory.setConfigPath("crawler.xml")` + `init()` gives a fully
  wired crawler
- **Standalone**: `fess-crawler/src/test/java/org/codelibs/fess/crawler/CrawlerTest.java:84-153` —
  the ~25-component `StandardCrawlerContainer` setup. Registration is **eager**, so order matters,
  and `@Resource` injection matches **by field name**. `crawler` must be a `prototype`

`fess-crawler-lasta` declares `jakarta.transaction:jakarta.transaction-api` as **`provided`**, so
downstream consumers must add it themselves or Lasta DI startup throws
`ClassNotFoundException: jakarta.transaction.Transactional$TxType`.

### Exception Hierarchy

All public exceptions are unchecked (extend `RuntimeException` via `CrawlerSystemException`).

```
CrawlerSystemException (RuntimeException)
  ├─ CrawlingAccessException
  │     ├─ MaxLengthExceededException
  │     └─ MultipleCrawlingAccessException
  ├─ ChildUrlsException
  ├─ ExtractException
  │     ├─ UnsupportedExtractException
  │     └─ ExecutionTimeoutException
  ├─ CrawlerLoginFailureException
  ├─ MimeTypeException
  ├─ RobotsTxtException
  ├─ SitemapsException
  └─ OpenSearchAccessException          (fess-crawler-opensearch module)
```

Two internal checked exceptions sit outside this tree and are not part of the public API:
`ApiExtractor.RetryableStatusException` and `CommandExtractor.OutputSizeExceededException`, both
`extends IOException`.

### Thread-Local Storage

Use `CrawlingParameterUtil` to set/get `CrawlerContext` and `UrlQueue` in worker threads. Always clear in finally block with `CrawlingParameterUtil.clearAll()`.

### Resource Cleanup Pattern

Always use try-with-resources for `ResponseData` - temp files are auto-deleted on close.

## Log Message Guidelines

- Format parameters as `key=value` (e.g., `sessionId={}`, `url={}`)
- Prefix with `[name]` when context identification is needed
- Use full words, not abbreviations
- Log only identifying fields, not entire objects
