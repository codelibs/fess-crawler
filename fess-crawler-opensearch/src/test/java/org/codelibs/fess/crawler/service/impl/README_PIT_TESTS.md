# PIT API Integration Tests

## Overview

This directory contains integration tests for the Point in Time (PIT) API implementation in OpenSearch.

## Test File

- `PitApiIntegrationTest.java` - Comprehensive integration tests for PIT API implementation

## Prerequisites

1. **Docker**: TestContainers requires Docker to be installed and running
2. **Java 17+**: Required for running the tests
3. **Maven 3.6+**: For building and running tests

## Running the Tests

### Run all PIT API tests

```bash
mvn test -Dtest=PitApiIntegrationTest
```

### Run specific test

```bash
mvn test -Dtest=PitApiIntegrationTest#testDataServiceIterateWithPitApi
```

### Run from specific module

```bash
mvn test -Dtest=PitApiIntegrationTest -pl fess-crawler-opensearch
```

## Test Coverage

### 1. **testDataServiceIterateWithPitApi**
Tests the `OpenSearchDataService.iterate()` method which uses PIT API for paginating through large datasets.

- **What it tests**: PIT-based iteration over 250 records
- **Expected behavior**: All records are iterated correctly using PIT pagination
- **Key verification**: Counter matches total inserted records

### 2. **testUrlQueueUpdateSessionIdWithPitApi**
Tests the `OpenSearchUrlQueueService.updateSessionId()` method which uses PIT API to update session IDs across all records.

- **What it tests**: Session ID update on 150 URL queue entries
- **Expected behavior**: All records are updated from old to new session ID
- **Key verification**:
  - New session ID has all records
  - Old session ID has zero records

### 3. **testBulkDeleteWithPitApi**
Tests the `AbstractCrawlerService.delete()` method which uses PIT API for bulk deletion.

- **What it tests**: Bulk deletion of 300 records using PIT pagination
- **Expected behavior**: All matching records are deleted
- **Key verification**: No records remain after deletion

### 4. **testPitDataConsistency**
Tests PIT's ability to provide a consistent snapshot of data.

- **What it tests**: Data consistency during PIT iteration
- **Expected behavior**: PIT returns exact count from snapshot point
- **Key verification**: Iteration count matches initial record count

### 5. **testDeleteByQueryWithPitApi**
Tests the `FesenClient.deleteByQuery()` method which uses PIT API for query-based deletion.

- **What it tests**: Query-based deletion of 200 records
- **Expected behavior**: All records matching query are deleted
- **Key verification**:
  - Deleted count matches total records
  - No records remain in index

## Test Container Configuration

The tests use OpenSearch 3.3.2 running in a Docker container:

```java
opensearchContainer = new GenericContainer<>(DockerImageName.parse("opensearchproject/opensearch:3.3.2"))
    .withExposedPorts(9200)
    .withEnv("discovery.type", "single-node")
    .withEnv("OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m")
    .withEnv("DISABLE_SECURITY_PLUGIN", "true")
    .waitingFor(Wait.forHttp("/_cluster/health").forStatusCode(200));
```

## Key Features Tested

### PIT API Benefits Verified

1. ✅ **Consistent Pagination**: Tests verify that PIT provides consistent results across multiple pagination requests
2. ✅ **Large Dataset Handling**: Tests with 150-300 records verify PIT works correctly beyond scroll size limits
3. ✅ **search_after Integration**: All tests use search_after with sort on `_id` for efficient pagination
4. ✅ **Proper Resource Cleanup**: Tests verify PIT resources are properly cleaned up after use
5. ✅ **Bulk Operations**: Tests verify PIT works correctly with bulk update and delete operations

### Implementation Details Tested

- **CreatePitRequest**: Proper PIT creation with timeout configuration
- **PointInTimeBuilder**: Correct PIT ID assignment in search requests
- **search_after**: Pagination using sort values from previous page
- **DeletePitRequest**: Proper PIT cleanup in finally blocks

## Troubleshooting

### Docker not running

```
Error: Could not find a valid Docker environment
```

**Solution**: Start Docker daemon before running tests

### Port already in use

```
Error: Port 9200 is already allocated
```

**Solution**: Stop any existing OpenSearch/Elasticsearch instances or let TestContainers assign a random port

### Test timeout

```
Error: Test timeout after 60 seconds
```

**Solution**: Increase wait times in test setup or check Docker resources

## Performance Considerations

- **Container startup**: ~10-15 seconds
- **Index creation**: ~2 seconds
- **Per test execution**: ~5-10 seconds
- **Total test suite**: ~2-3 minutes

## CI/CD Integration

For CI/CD pipelines, ensure:

1. Docker is available in the build environment
2. Sufficient memory allocated (at least 1GB for OpenSearch)
3. Network access for pulling Docker images
4. Appropriate timeouts configured

Example GitHub Actions:

```yaml
- name: Run PIT API Integration Tests
  run: mvn test -Dtest=PitApiIntegrationTest -pl fess-crawler-opensearch
  env:
    TESTCONTAINERS_RYUK_DISABLED: false
```

## Related Documentation

- [OpenSearch PIT API Documentation](https://opensearch.org/docs/latest/search-plugins/point-in-time/)
- [TestContainers Java Documentation](https://www.testcontainers.org/)
- [PIT API Implementation Details](../../../../main/java/org/codelibs/fess/crawler/service/impl/)
