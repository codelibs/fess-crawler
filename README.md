# Fess Crawler

[![Java CI with Maven](https://github.com/codelibs/fess-crawler/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess-crawler/actions/workflows/maven.yml)
[![Maven Repository](https://img.shields.io/badge/Maven-maven.codelibs.org-blue)](https://maven.codelibs.org/release/org/codelibs/fess/fess-crawler/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Javadoc](https://javadoc.io/badge2/org.codelibs.fess/fess-crawler/javadoc.svg)](https://javadoc.io/doc/org.codelibs.fess/fess-crawler)

**Fess Crawler** is a Java crawling framework for enterprise-scale content acquisition and text
extraction. It is the crawling engine behind [Fess](https://github.com/codelibs/fess), and can be
embedded in any JVM application on its own.

## Key Features

- **Multi-protocol**: HTTP/HTTPS, local/network file systems, FTP/FTPS, SMB/CIFS (SMB1 and SMB2+),
  MinIO-compatible object storage, Amazon S3, Google Cloud Storage
- **Broad text extraction**: Office documents, PDF, HTML, XML, JSON, CSV, Markdown, e-mail, archives,
  PostScript, and media/image metadata — via Apache Tika, POI, PDFBox and format-specific extractors
- **Multi-threaded** crawling with configurable thread pools, depth limits and access-count limits
- **Fault tolerant**: retry wrapper (`FaultTolerantClient`) and an HTTP client that can fall back
  between Apache HttpComponents 5.x and 4.x
- **Polite**: `robots.txt` (RFC 9309) and sitemap support, plus configurable request intervals
- **Bounded by design**: content-length caps enforced *while* downloading, archive-bomb and Zip Slip
  defenses, and bounded recursion in nested extractors
- **Pluggable**: clients, extractors, transformers, rules and filters are all replaceable via DI
- **Pluggable storage**: in-memory by default, OpenSearch for persistent/distributed crawls

## Requirements

- Java 21 or later
- Maven 3.8 or later (to build from source)

## Installation

Releases are published to the CodeLibs Maven repository rather than Maven Central, so declare it
first. Browse [the artifact directory](https://maven.codelibs.org/release/org/codelibs/fess/fess-crawler/)
for the current release.

```xml
<repositories>
    <repository>
        <id>codelibs.org</id>
        <name>CodeLibs Repository</name>
        <url>https://maven.codelibs.org/release/</url>
    </repository>
</repositories>
```

Then add the modules you need.

```xml
<properties>
    <fess.crawler.version>15.7.0</fess.crawler.version>
</properties>

<dependencies>
    <!-- Core framework -->
    <dependency>
        <groupId>org.codelibs.fess</groupId>
        <artifactId>fess-crawler</artifactId>
        <version>${fess.crawler.version}</version>
    </dependency>

    <!-- Ready-made LastaFlute DI wiring (recommended starting point) -->
    <dependency>
        <groupId>org.codelibs.fess</groupId>
        <artifactId>fess-crawler-lasta</artifactId>
        <version>${fess.crawler.version}</version>
    </dependency>

    <!-- Optional: persist the URL queue and results in OpenSearch -->
    <dependency>
        <groupId>org.codelibs.fess</groupId>
        <artifactId>fess-crawler-opensearch</artifactId>
        <version>${fess.crawler.version}</version>
    </dependency>
</dependencies>
```

> **Note**
> `fess-crawler-lasta` declares `jakarta.transaction:jakarta.transaction-api` with `provided`
> scope, so it is *not* pulled in transitively. Lasta DI needs it at runtime — add it to your own
> dependencies, or container startup fails with
> `ClassNotFoundException: jakarta.transaction.Transactional$TxType`.
>
> You also need an SLF4J/Log4j binding of your choice; none is bundled.

## Quick Start

`fess-crawler-lasta` ships a complete DI configuration (`crawler.xml`) with every client, extractor,
transformer and rule already wired, so the shortest working crawler is:

```java
import org.codelibs.fess.crawler.Crawler;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.service.DataService;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

public class QuickStart {
    public static void main(final String[] args) {
        SingletonLaContainerFactory.setConfigPath("crawler.xml");
        SingletonLaContainerFactory.init();

        final Crawler crawler = SingletonLaContainerFactory.getContainer().getComponent("crawler");
        final DataService<AccessResult<?>> dataService =
                SingletonLaContainerFactory.getContainer().getComponent("dataService");

        try (crawler) {
            crawler.addUrl("https://example.com/");
            crawler.addIncludeFilter("https://example.com/.*");
            crawler.setMaxAccessCount(100L);
            crawler.setNumOfThread(5);
            crawler.setMaxDepth(3);

            final String sessionId = crawler.execute(); // blocks until the crawl finishes

            System.out.println("crawled=" + dataService.getCount(sessionId));
            dataService.iterate(sessionId, result -> System.out.println(result.getUrl()));
            dataService.delete(sessionId);
        }
    }
}
```

`crawler` is a **prototype** component: call `getComponent("crawler")` again for each independent
crawl. `execute()` returns the session ID, which is the key for everything in `DataService` and
`UrlQueueService`.

Swap the URL for `file:///path/to/dir`, `smb://host/share/`, `ftp://host/dir/`, `s3://bucket/prefix`
… and the same code crawls that source — client selection is driven by the URL scheme.

## Configuring a Crawl

All of these are `Crawler` methods; there is no need to reach into `CrawlerContext` (its field is
`protected`, so it is not accessible from outside the `org.codelibs.fess.crawler` package).

```java
crawler.setSessionId("nightly-crawl");   // default: generated timestamp
crawler.setNumOfThread(10);              // worker threads
crawler.setMaxDepth(3);                  // -1 (default) = unlimited
crawler.setMaxAccessCount(10_000L);      // 0 (default) = unlimited
crawler.setMaxThreadCheckCount(20);      // idle polls before a thread gives up
crawler.setThreadPriority(Thread.NORM_PRIORITY);

crawler.addIncludeFilter("https://example\\.com/.*");
crawler.addExcludeFilter(".*\\.(css|js|png|jpe?g|gif)$");
```

Invalid regular expressions passed to the filters are logged and ignored rather than thrown, so a
typo silently widens the crawl — check your logs.

### Background Crawling

```java
crawler.setBackground(true);
final String sessionId = crawler.execute();   // returns immediately

// CrawlerStatus is INITIALIZING, RUNNING or DONE
crawler.awaitTermination();                   // or awaitTermination(millis)
crawler.stop();                               // request an early stop
crawler.cleanup(sessionId);                   // drop queue + filter state
```

`Crawler` implements `AutoCloseable`; `close()` performs the cleanup for the current session.

### Reading Results

```java
final DataService<AccessResult<?>> dataService = container.getComponent("dataService");

System.out.println(dataService.getCount(sessionId));

dataService.iterate(sessionId, result -> {
    System.out.println(result.getUrl());
    System.out.println(result.getHttpStatusCode());
    System.out.println(result.getMimeType());
    System.out.println(result.getContentLength());
    // Extracted text/binary lives on the child entity, not on AccessResult itself:
    System.out.println(result.getAccessResultData().getDataAsString());
});

final AccessResult<?> one = dataService.getAccessResult(sessionId, url);
dataService.delete(sessionId);
```

## Supported Sources

Clients are selected by URL pattern (see `crawler/client.xml` in `fess-crawler-lasta`):

| URL pattern | Client | Notes |
| --- | --- | --- |
| `http:.*`, `https:.*` | `SwitchableHttpClient` | wraps `Hc5HttpClient` (default) or `Hc4HttpClient` |
| `file:.*` | `FileSystemClient` | local and mounted network paths |
| `smb:.*` | `smb.SmbClient` | SMB2+ (SMBJ) |
| `smb1:.*` | `smb1.SmbClient` | legacy SMB1 (JCIFS) |
| `ftp:.*`, `ftps:.*` | `FtpClient` | shared component for both schemes |
| `storage:.*` | `StorageClient` | MinIO / S3-compatible object storage |
| `s3:.*` | `S3Client` | AWS SDK v2 |
| `gcs:.*` | `GcsClient` | Google Cloud Storage |

### Content Formats

Extractors registered by MIME type in `crawler/extractor.xml`: HTML, XML/XHTML/RDF/FreeMind, PDF,
LHA, e-mail (`message/rfc822`), JSON, CSV, Markdown, PostScript, and a large Tika-backed catalogue
covering Office (Word/Excel/PowerPoint/Publisher/Visio), OpenDocument, RTF, archives, audio, video
and images (EXIF/IPTC/XMP metadata).

Additional extractors ship in the jar but are **not** wired into the default DI, so you must register
them yourself if you want them: `ZipExtractor`, `TarExtractor`, `ApiExtractor`, `CommandExtractor`.

## Configuration

### Overriding DI Components

The recommended approach is to include `crawler.xml` from your own Lasta DI file and redefine only
the components you care about — component names are the contract:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE components PUBLIC "-//DBFLUTE//DTD LastaDi 1.0//EN"
    "http://dbflute.org/meta/lastadi10.dtd">
<components namespace="fessCrawler">
    <include path="crawler.xml"/>

    <component name="internalHttpClient"
        class="org.codelibs.fess.crawler.client.http.Hc5HttpClient" instance="prototype">
        <property name="userAgent">"MyBot/1.0 (+https://example.com/bot)"</property>
        <property name="connectionTimeout">30000</property>
        <property name="soTimeout">60000</property>
        <property name="maxTotalConnections">200</property>
        <property name="maxConnectionsPerRoute">20</property>
    </component>
</components>
```

The DI files you can include or override individually live in `fess-crawler-lasta`'s resources:
`crawler/` + `client.xml`, `container.xml`, `contentlength.xml`, `encoding.xml`, `extractor.xml`,
`filter.xml`, `interval.xml`, `log.xml`, `mimetype.xml`, `robotstxt.xml`, `rule.xml`,
`sitemaps.xml`, `transformer.xml`, `transformer_basic.xml`, `urlconverter.xml`.

### HTTP Client

`HcHttpClient` is an **abstract** base holding the init-parameter constants — register one of the
concrete classes, `Hc5HttpClient` (default) or `Hc4HttpClient`. `SwitchableHttpClient` extends
`FaultTolerantClient` and *delegates* to whichever of the two the system property selects:

```
-Dfess.crawler.http.client=hc4    # anything else, including unset, selects hc5
```

The property is read once when `SwitchableHttpClient` is constructed.

Commonly tuned setters on `Hc5HttpClient` / `Hc4HttpClient` — note the names, several differ from
what you might guess:

| Purpose | Setter | Init-parameter key |
| --- | --- | --- |
| User agent | `setUserAgent(String)` | `userAgent` |
| Connect timeout (ms) | `setConnectionTimeout(Integer)` | `connectionTimeout` |
| Read timeout (ms) | `setSoTimeout(Integer)` | `soTimeout` |
| Total pool size | `setMaxTotalConnections(Integer)` | `maxTotalConnection` |
| Per-host pool size | `setMaxConnectionsPerRoute(Integer)` | `maxConnectionsPerRoute` |
| Skip TLS verification | `setIgnoreSslCertificate(boolean)` | `ignoreSslCertificate` |
| Proxy | `setProxyHost(String)` / `setProxyPort(Integer)` | `proxyHost` / `proxyPort` |
| Follow redirects | `setRedirectsEnabled(boolean)` | `redirectsEnabled` |
| Honour robots.txt | `setUseRobotsTxtDisallows(boolean)` / `setUseRobotsTxtAllows(boolean)` | — |

Disable TLS verification only against hosts you control.

Every client also inherits generic knobs from `AbstractCrawlerClient`: `accessTimeout`,
`maxContentLength`, `maxCachedContentSize`. Any of these can be supplied as a map instead of via
setters:

```java
final Map<String, Object> params = new HashMap<>();
params.put(HcHttpClient.USER_AGENT_PROPERTY, "MyBot/1.0");
params.put(HcHttpClient.SO_TIMEOUT_PROPERTY, 60000);
params.put(AbstractCrawlerClient.MAX_CONTENT_LENGTH, 50L * 1024 * 1024);
client.setInitParameterMap(params);
```

### Object Storage Credentials

`S3Client` reads `accessKey`, `secretKey`, `endpoint`, `region` (default `us-east-1`) and
`crossRegionAccessEnabled` (default `true`). When `accessKey`/`secretKey` are blank it falls back to
the **AWS default credentials provider chain**, so IAM roles, instance profiles, IRSA, environment
variables and shared profiles all work without hard-coded keys.

`GcsClient` reads `projectId`, `credentialsFile` and `endpoint`; without `credentialsFile` it uses
Application Default Credentials.

### Politeness and Intervals

`DefaultIntervalController` exposes four independent delays, all in milliseconds:

```xml
<component name="intervalController"
    class="org.codelibs.fess.crawler.interval.impl.DefaultIntervalController">
    <property name="delayMillisBeforeProcessing">1000</property>
    <property name="delayMillisAfterProcessing">0</property>
    <property name="delayMillisAtNoUrlInQueue">500</property>
    <property name="delayMillisForWaitingNewUrl">1000</property>
</component>
```

Use `delayMillisBeforeProcessing` for a fixed per-request delay. `robots.txt` `Crawl-delay` is
handled separately by `RobotsTxtHelper`.

### Content Size Limits

Size limits are owned by `ContentLengthHelper`, not by the transformers. The default cap is 10 MB and
can be overridden per MIME type:

```xml
<component name="contentLengthHelper"
    class="org.codelibs.fess.crawler.helper.ContentLengthHelper">
    <property name="defaultMaxLength">10485760</property>
    <postConstruct name="addMaxLength">
        <arg>"application/pdf"</arg>
        <arg>52428800</arg>
    </postConstruct>
</component>
```

HTTP clients enforce this **during** the download and abort as soon as the limit is exceeded, rather
than buffering the whole body first. Responses larger than `maxCachedContentSize` spill to a
temporary file, which is deleted when the `ResponseData` is closed — always use try-with-resources
when handling `ResponseData` yourself.

### HTML Metadata and JSON-LD

`HtmlExtractor` extracts standard metadata (title, description, OpenGraph, Twitter Card) by default.
JSON-LD extraction is opt-in and bounded in nesting depth, size and item count:

```xml
<component name="htmlExtractor" class="org.codelibs.fess.crawler.extractor.impl.HtmlExtractor">
    <property name="extractDefaultMetadata">true</property>   <!-- default -->
    <property name="extractJsonLd">true</property>            <!-- default: false -->
</component>
```

### Extractor Selection

`ExtractorFactory` maps MIME types to extractors. Several extractors may claim the same MIME type; in
that case they are tried in **descending weight** order until one succeeds (`weight` defaults to `1`,
set via `AbstractExtractor.setWeight(int)`).

To extract from a stream directly, without running a crawl:

```java
final ExtractData data = extractorFactory.builder(inputStream, params)
        .mimeType("application/pdf")
        .maxContentLength(10 * 1024 * 1024)
        .extract();
System.out.println(data.getContent());
```

When `maxContentLength` is not set, the builder falls back to `ContentLengthHelper`.

## OpenSearch Backend

`fess-crawler-opensearch` replaces the in-memory queue and result store with OpenSearch indices.
`OpenSearchDataService` has no no-arg constructor and no host/port setters — it is configured
through `OpenSearchCrawlerConfig`, as in the module's own `crawler_opensearch.xml`:

```xml
<component name="crawlerConfig"
    class="org.codelibs.fess.crawler.util.OpenSearchCrawlerConfig">
    <property name="dataIndex">"fess_crawler.data"</property>
    <property name="dataShards">5</property>
    <property name="dataReplicas">1</property>
</component>

<component name="dataService"
    class="org.codelibs.fess.crawler.service.impl.OpenSearchDataService">
    <arg>crawlerConfig</arg>
</component>
```

Connectivity comes from an injected `fesenClient`
([fesen-httpclient](https://github.com/codelibs/fesen-httpclient)); index mappings are created on
first connect.

## Embedding Without Lasta DI

`StandardCrawlerContainer` is a small standalone container for applications that do not want Lasta
DI. Be aware of two behaviours before using it:

- Registration **eagerly instantiates** each component, so a component must be registered *after*
  everything it depends on, and `@Resource` injection is matched **by field name**.
- `crawler`, `crawlerThread`, `urlQueue`, `accessResult` and the services must be registered as
  `prototype`; registering `crawler` as a singleton makes every `getComponent("crawler")` return the
  same instance.

A crawl needs roughly 25 components wired together (crawler + thread + entities, the four services,
eight helpers, a client factory with at least one client, a rule manager with a rule and a response
processor, a transformer, an interval controller, and an extractor factory). Rather than reproduce
all of it here, use
[`fess-crawler/src/test/java/org/codelibs/fess/crawler/CrawlerTest.java`](fess-crawler/src/test/java/org/codelibs/fess/crawler/CrawlerTest.java)
as the reference wiring — it is exercised by the test suite on every build. For most applications the
Lasta DI route in [Quick Start](#quick-start) is considerably less work.

## Architecture

```
fess-crawler-parent/
├── fess-crawler/              # Core framework: clients, extractors, transformers, services
├── fess-crawler-lasta/        # Lasta DI wiring (crawler.xml and friends)
└── fess-crawler-opensearch/   # OpenSearch-backed queue and result store
```

### Crawl Pipeline

```
Crawler ──▶ CrawlerThread ──▶ CrawlerClient ──▶ ResponseData
                                                    │
                                            RuleManager (pattern match)
                                                    │
                                            ResponseProcessor
                                                    │
                                              Transformer ──▶ Extractor ──▶ ExtractData
                                                    │
                        ┌───────────────────────────┴───────────────────────────┐
                        ▼                                                       ▼
                UrlQueueService (child URLs)                          DataService (results)
```

`UrlFilter` gates URLs on the way into the queue; `IntervalController` paces the workers;
`CrawlingParameterUtil` carries the `CrawlerContext` and `UrlQueue` in thread-locals.

### Extending

A custom extractor implements one method:

```java
public class CustomExtractor extends AbstractExtractor {
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        final ExtractData data = new ExtractData();
        // ... populate content and metadata
        return data;
    }

    @Override
    public int getWeight() {
        return 10; // higher wins when several extractors claim the same MIME type
    }
}
```

Declare it as a component, then **append** it to the existing factory after the container starts:

```xml
<components namespace="fessCrawler">
    <include path="crawler.xml"/>
    <component name="customExtractor" class="com.example.CustomExtractor"/>
</components>
```

```java
final ExtractorFactory factory = container.getComponent("extractorFactory");
factory.addExtractor("application/x-custom", container.getComponent("customExtractor"));
```

> **Warning**
> Do *not* redeclare `extractorFactory` itself in your DI file to add a mapping. The outer
> definition replaces the included one wholesale, and every default MIME-type mapping — HTML, PDF,
> Office, everything — is silently lost, leaving `getExtractor()` returning `null`.

Custom clients (`CrawlerClient`), transformers (`Transformer`) and rules (`Rule`) follow the same
pattern — implement the interface, register the component, map it in the relevant factory.

## Building from Source

```bash
git clone https://github.com/codelibs/fess-crawler.git
cd fess-crawler

mvn clean install                 # build + test all modules
mvn clean install -DskipTests     # skip tests
mvn clean install -pl fess-crawler
```

`fess-crawler` builds against `fess-parent`; when working on an unreleased version, build and install
[codelibs/fess-parent](https://github.com/codelibs/fess-parent) (branch `main`) first — this is
exactly what CI does.

### Testing

Tests use **JUnit 5 (Jupiter)** on top of UTFlute (`PlainTestCase`, or `LastaDiTestCase` for the DI
modules), with Mockito and Testcontainers where a real backend is needed.

```bash
mvn test
mvn test -pl fess-crawler
mvn test -Dtest=CrawlerTest
mvn test -Dtest=CrawlerTest#test_execute_web
```

Surefire runs test **classes in parallel**, so tests must not share mutable static state.
`JodExtractorTest` is excluded from the default run (it needs a local LibreOffice/JODConverter).
Container-backed tests (`S3ClientTest`, `StorageClientTest`, `GcsClientTest`, `SmbClientTest`)
require a working Docker daemon.

JaCoCo produces a coverage report during `package`, but no minimum is enforced by the build.

### Code Quality

```bash
mvn formatter:format     # Eclipse formatter: 4 spaces, no tabs, 140-column lines
mvn license:format       # Apache 2.0 headers (license:check runs in the build and fails on gaps)
```

Formatter and license configuration come from `fess-parent`, which points at the shared CodeLibs
config, so do not add a local formatter file.

### Continuous Integration

`.github/workflows/maven.yml` runs on pushes and pull requests to `master` and `*.x` branches: a
single **JDK 21** (Temurin) build that installs `fess-parent` and then runs `mvn -B package`.

`package` is more than tests — it also runs `license:check`, `formatter:format`, `jacoco:report`,
`source:jar` and **`javadoc:jar`**. Javadoc errors therefore fail CI even though `mvn test` passes
locally. If you touch imports or Javadoc, verify with:

```bash
mvn javadoc:jar
```

## Troubleshooting

**Container fails to start with `ClassNotFoundException: jakarta.transaction.Transactional$TxType`**
Add `jakarta.transaction:jakarta.transaction-api` to your dependencies — `fess-crawler-lasta`
declares it as `provided`.

**Nothing is crawled beyond the seed URL**
Include filters are required for child URLs. `crawler.addUrl(url)` alone does not authorize the
crawler to follow links; add `crawler.addIncludeFilter(url + ".*")`. Also check `setMaxDepth`.

**Crawl stops early**
Check `setMaxAccessCount` and `setMaxDepth`, and whether `robots.txt` disallows the paths — set
`useRobotsTxtDisallows` to `false` on the HTTP client only when you are authorized to ignore it.

**Timeouts on slow servers**
Raise `connectionTimeout` and `soTimeout` on the HTTP client, and `accessTimeout` on the client base.

**Large files are truncated or skipped**
Raise `ContentLengthHelper.defaultMaxLength` (10 MB default) or add a per-MIME-type override, and
raise the client's `maxContentLength`.

**High memory use**
Lower `setNumOfThread`, lower `maxCachedContentSize` so bodies spill to disk sooner, and keep
`ContentLengthHelper` limits tight.

### Debug Logging

```xml
<logger name="org.codelibs.fess.crawler" level="DEBUG"/>
<logger name="org.codelibs.fess.crawler.client" level="DEBUG"/>
<logger name="org.codelibs.fess.crawler.extractor" level="DEBUG"/>
```

## Contributing

1. Fork the repository and create a feature branch
2. Add tests for your change (JUnit 5)
3. Run `mvn formatter:format && mvn license:format`
4. Run `mvn package` — this catches Javadoc and license failures that `mvn test` does not
5. Open a pull request against `master`

Bug reports and feature requests: https://github.com/codelibs/fess-crawler/issues

## License

Apache License 2.0 — see [LICENSE](LICENSE).
