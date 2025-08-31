# Fess Crawler

[![Java CI with Maven](https://github.com/codelibs/fess-crawler/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess-crawler/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess-crawler-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess-crawler-parent)

## Overview

**Fess Crawler** is a powerful, flexible Java-based web crawling framework designed for enterprise-scale content extraction and processing. Built with a modular architecture, it supports multiple protocols (HTTP/HTTPS, File System, FTP, SMB, Cloud Storage) and provides extensive content extraction capabilities from various document formats.

### Key Features

- **Multi-Protocol Support**: HTTP/HTTPS, File System, FTP, SMB/CIFS, Cloud Storage (MinIO, S3)
- **Comprehensive Content Extraction**: Office documents, PDFs, archives, images, audio/video files
- **Multi-Threading**: Configurable thread pools for high-performance crawling
- **Fault Tolerance**: Built-in retry mechanisms and error handling
- **Flexible Configuration**: XML-based dependency injection with LastaFlute DI
- **Extensible Architecture**: Plugin system for custom extractors, transformers, and clients
- **Rate Limiting**: Politeness policies and interval controllers
- **URL Filtering**: Regex-based inclusion/exclusion patterns
- **Data Persistence**: Multiple backend options including OpenSearch integration

## Technology Stack

- **Java**: 21+ (requires Java 21 or higher)
- **Build System**: Maven 3.x
- **DI Container**: LastaFlute DI
- **HTTP Client**: Apache HttpComponents
- **Content Extraction**: Apache Tika, Apache POI, PDFBox
- **Testing**: JUnit 4, UTFlute, Testcontainers
- **Storage Backends**: OpenSearch, Memory-based

## Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.codelibs.fess</groupId>
    <artifactId>fess-crawler</artifactId>
    <version>15.2.0-SNAPSHOT</version>
</dependency>

<!-- For LastaFlute DI integration -->
<dependency>
    <groupId>org.codelibs.fess</groupId>
    <artifactId>fess-crawler-lasta</artifactId>
    <version>15.2.0-SNAPSHOT</version>
</dependency>

<!-- For OpenSearch backend -->
<dependency>
    <groupId>org.codelibs.fess</groupId>
    <artifactId>fess-crawler-opensearch</artifactId>
    <version>15.2.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import org.codelibs.fess.crawler.Crawler;
import org.codelibs.fess.crawler.client.http.HcHttpClient;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.transformer.impl.FileTransformer;

public class BasicCrawlerExample {
    public static void main(String[] args) throws Exception {
        // Create crawler container
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        
        // Configure basic components
        container.singleton("crawler", Crawler.class)
                .singleton("httpClient", HcHttpClient.class)
                .singleton("fileTransformer", FileTransformer.class);
        
        // Get crawler instance
        Crawler crawler = container.getComponent("crawler");
        
        // Configure crawling parameters
        crawler.addUrl("https://example.com");
        crawler.crawlerContext.setMaxAccessCount(100);
        crawler.crawlerContext.setNumOfThread(5);
        crawler.urlFilter.addInclude("https://example.com/.*");
        
        // Execute crawling
        String sessionId = crawler.execute();
        System.out.println("Crawling completed. Session ID: " + sessionId);
    }
}
```

### File System Crawling

```java
import org.codelibs.fess.crawler.client.fs.FileSystemClient;

// Configure for file system crawling
container.singleton("fsClient", FileSystemClient.class);

// Add file URL
crawler.addUrl("file:///path/to/directory");
crawler.urlFilter.addInclude("file:///path/to/directory/.*");
```

## Configuration

### XML Configuration

Fess Crawler uses XML-based configuration with LastaFlute DI. Place configuration files in your classpath:

```xml
<!-- crawler.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE components PUBLIC "-//DBFLUTE//DTD LastaDi 1.0//EN"
    "http://dbflute.org/meta/lastadi10.dtd">
<components namespace="fessCrawler">
    <component name="crawler" class="org.codelibs.fess.crawler.Crawler" instance="prototype"/>
    <component name="httpClient" class="org.codelibs.fess.crawler.client.http.HcHttpClient" instance="singleton"/>
    <component name="fileTransformer" class="org.codelibs.fess.crawler.transformer.impl.FileTransformer" instance="singleton"/>
</components>
```

### Crawler Context Configuration

```java
// Set maximum number of URLs to crawl
crawler.crawlerContext.setMaxAccessCount(1000);

// Set number of crawler threads
crawler.crawlerContext.setNumOfThread(10);

// Set maximum crawl depth
crawler.crawlerContext.setMaxDepth(3);

// Set request interval (politeness)
crawler.crawlerContext.setDefaultIntervalTime(1000); // 1 second
```

### URL Filtering

```java
// Include patterns
crawler.urlFilter.addInclude("https://example.com/.*");
crawler.urlFilter.addInclude(".*\\.pdf$");

// Exclude patterns  
crawler.urlFilter.addExclude(".*\\.js$");
crawler.urlFilter.addExclude(".*login.*");
```

## Supported Protocols and Formats

### Protocols
- **HTTP/HTTPS**: Full web crawling support with cookies, authentication, redirects
- **File System**: Local and network file system access
- **FTP**: FTP server crawling with authentication
- **SMB/CIFS**: Windows network shares
- **Storage**: Cloud storage systems (MinIO, S3-compatible)

### Content Formats

#### Office Documents
- Microsoft Office (Word, Excel, PowerPoint)
- OpenOffice/LibreOffice documents
- RTF, WordPerfect

#### PDFs and Images
- PDF documents (text and metadata extraction)
- Images (JPEG, PNG, GIF, TIFF, BMP)
- Image metadata (EXIF, IPTC, XMP)

#### Archives and Compressed Files
- ZIP, TAR, GZ archives
- LHA compression format
- Nested archive extraction

#### Web and Markup
- HTML, XHTML with XPath support
- XML documents
- JSON and structured data

#### Media Files
- Audio formats (MP3, WAV, FLAC)
- Video formats (MP4, AVI, MOV)
- Metadata extraction from media files

## Architecture

### Multi-Module Structure

```
fess-crawler-parent/
├── fess-crawler/              # Core crawler framework
│   ├── client/               # Protocol clients (HTTP, FTP, SMB, etc.)
│   ├── extractor/           # Content extractors
│   ├── transformer/         # Data transformers
│   └── service/             # Core services
├── fess-crawler-lasta/       # LastaFlute DI integration
└── fess-crawler-opensearch/  # OpenSearch backend
```

### Key Components

#### Core Engine
- **Crawler**: Main orchestrator managing crawl execution
- **CrawlerContext**: Execution context and configuration
- **CrawlerThread**: Individual crawler thread implementation

#### Client Architecture
- **HcHttpClient**: HTTP/HTTPS client using Apache HttpComponents
- **FileSystemClient**: File system access
- **FtpClient**: FTP protocol support
- **SmbClient**: SMB/CIFS network shares
- **StorageClient**: Cloud storage integration

#### Content Processing Pipeline
- **Extractors**: Content extraction from various formats
- **Transformers**: Data transformation and enrichment
- **Filters**: URL filtering with regex patterns
- **Rules**: Content processing rules and validation

## Building and Testing

### Build Commands

```bash
# Build all modules
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Build specific module
mvn clean install -pl fess-crawler

# Generate test coverage report
mvn jacoco:report
```

### Code Quality

```bash
# Format code
mvn formatter:format

# Update license headers
mvn license:format

# Run static analysis
mvn spotbugs:check
```

### Running Tests

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

## Examples

### Web Crawling with Custom Rules

```java
// Create crawler with custom configuration
StandardCrawlerContainer container = new StandardCrawlerContainer();

// Configure HTTP client with custom settings
container.singleton("httpClient", HcHttpClient.class, client -> {
    client.setUserAgent("MyBot/1.0");
    client.setConnectionTimeout(30000);
    client.setMaxConnections(100);
});

// Configure URL filtering
container.singleton("urlFilter", UrlFilterImpl.class, filter -> {
    filter.addInclude("https://example.com/.*");
    filter.addExclude(".*\\.(css|js|png|jpg|gif)$");
});

// Configure content extraction
container.singleton("tikaExtractor", TikaExtractor.class);
container.singleton("extractorFactory", ExtractorFactory.class, factory -> {
    factory.addExtractor("text/html", container.getComponent("tikaExtractor"));
    factory.addExtractor("application/pdf", container.getComponent("tikaExtractor"));
});

Crawler crawler = container.getComponent("crawler");
crawler.addUrl("https://example.com");
crawler.crawlerContext.setMaxAccessCount(500);
String sessionId = crawler.execute();
```

### Background Crawling

```java
// Configure for background execution
crawler.setBackground(true);
String sessionId = crawler.execute();

// Check crawling status
while (crawler.crawlerContext.getStatus() == CrawlerStatus.RUNNING) {
    Thread.sleep(1000);
    System.out.println("Crawling in progress...");
}

// Wait for completion
crawler.awaitTermination();
System.out.println("Crawling completed");
```

### Custom Content Extractor

```java
public class CustomExtractor extends AbstractExtractor {
    @Override
    public ExtractData getText(final InputStream inputStream, final Map<String, String> params) {
        // Custom extraction logic
        ExtractData extractData = new ExtractData();
        // ... implementation
        return extractData;
    }
}

// Register custom extractor
container.singleton("customExtractor", CustomExtractor.class);
container.singleton("extractorFactory", ExtractorFactory.class, factory -> {
    factory.addExtractor("application/custom", container.getComponent("customExtractor"));
});
```

## Advanced Configuration

### Multi-Instance Crawling

```java
// Create multiple crawler instances
Crawler crawler1 = container.getComponent("crawler");
crawler1.setSessionId("session1");
crawler1.addUrl("https://site1.com");

Crawler crawler2 = container.getComponent("crawler");  
crawler2.setSessionId("session2");
crawler2.addUrl("https://site2.com");

// Execute concurrently
crawler1.setBackground(true);
crawler2.setBackground(true);

String sessionId1 = crawler1.execute();
String sessionId2 = crawler2.execute();

crawler1.awaitTermination();
crawler2.awaitTermination();
```

### Custom Interval Control

```java
// Configure politeness policy
container.singleton("intervalController", DefaultIntervalController.class, controller -> {
    controller.setDelayMillisForWaitingNewUrl(5000);
    controller.setDefaultIntervalTime(1000);
});
```

### Sitemap Support

```java
// Enable sitemap processing
container.singleton("sitemapsRule", SitemapsRule.class, rule -> {
    rule.addRule("url", ".*sitemap.*");
});

// Add sitemap URL
crawler.addUrl("https://example.com/sitemap.xml");
```

## Data Access and Storage

### Accessing Crawled Data

```java
// Get data service
DataService dataService = container.getComponent("dataService");

// Iterate through crawled data
dataService.iterate(sessionId, accessResult -> {
    System.out.println("URL: " + accessResult.getUrl());
    System.out.println("Status: " + accessResult.getHttpStatusCode());
    System.out.println("Content Type: " + accessResult.getMimeType());
    System.out.println("Content: " + accessResult.getContent());
    System.out.println("---");
});

// Get specific result
AccessResult result = dataService.getAccessResult(sessionId, url);

// Delete session data
dataService.delete(sessionId);
```

### OpenSearch Integration

```java
// Add OpenSearch dependency and configure
container.singleton("opensearchDataService", OpenSearchDataService.class, service -> {
    service.setIndexName("crawler-data");
    service.setHostname("localhost");
    service.setPort(9200);
});
```

## Performance Tuning

### Thread Configuration

```java
// Optimize thread pool settings
crawler.crawlerContext.setNumOfThread(20);           // Number of crawler threads
crawler.crawlerContext.setMaxThreadCheckCount(50);   // Thread monitoring frequency
```

### Connection Pool Tuning

```java
container.singleton("httpClient", HcHttpClient.class, client -> {
    client.setMaxConnections(200);          // Total connections
    client.setMaxConnectionsPerRoute(20);   // Per-host connections
    client.setConnectionTimeout(30000);     // Connection timeout
    client.setSocketTimeout(60000);         // Read timeout
});
```

### Memory Management

```java
// Configure memory usage
crawler.crawlerContext.setMaxAccessCount(10000);     // Limit crawled URLs
crawler.crawlerContext.setMaxDepth(5);               // Limit crawl depth

// Use streaming for large files
container.singleton("fileTransformer", FileTransformer.class, transformer -> {
    transformer.setMaxContentSize(10 * 1024 * 1024); // 10MB limit
});
```

## Troubleshooting

### Common Issues

#### Connection Timeouts
```java
// Increase timeout values
client.setConnectionTimeout(60000);  // 60 seconds
client.setSocketTimeout(120000);     // 120 seconds
```

#### Memory Issues
```java
// Reduce concurrent threads and batch sizes
crawler.crawlerContext.setNumOfThread(5);
crawler.crawlerContext.setMaxAccessCount(1000);
```

#### SSL/TLS Issues
```java
// Configure SSL settings
container.singleton("httpClient", HcHttpClient.class, client -> {
    client.setTrustAllCertificates(true);  // For testing only
});
```

### Debug Logging

Enable debug logging by adding to your logging configuration:

```xml
<logger name="org.codelibs.fess.crawler" level="DEBUG"/>
<logger name="org.codelibs.fess.crawler.client" level="DEBUG"/>
<logger name="org.codelibs.fess.crawler.extractor" level="DEBUG"/>
```

### Monitoring

```java
// Monitor crawling progress
while (crawler.crawlerContext.getStatus() == CrawlerStatus.RUNNING) {
    int processed = dataService.getCount(sessionId);
    System.out.println("Processed: " + processed + " URLs");
    Thread.sleep(5000);
}
```

## Contributing

We welcome contributions to Fess Crawler! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Development Setup

```bash
# Clone the repository
git clone https://github.com/codelibs/fess-crawler.git
cd fess-crawler

# Build the project
mvn clean install

# Run tests
mvn test

# Format code before committing
mvn formatter:format
mvn license:format
```

### Code Style

- Follow Java coding conventions
- Use proper JavaDoc comments for public APIs
- Include unit tests for new functionality
- Ensure all tests pass before submitting PR

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

