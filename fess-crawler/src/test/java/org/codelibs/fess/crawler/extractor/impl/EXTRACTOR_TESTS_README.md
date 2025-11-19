# Extractor Implementation Tests

This directory contains comprehensive tests for the Extractor implementations, focusing on the improvements made to resource management, error handling, and input validation.

## Test Files Overview

### 1. ExtractorResourceManagementTest.java
**Purpose**: Verify proper resource management in Extractor implementations.

**Key Test Areas**:
- Resource closure on successful extraction (MS Office extractors)
- Resource closure on failed extraction
- Improved error messages with context
- Input validation using `validateInputStream()`

**Covered Extractors**:
- MsWordExtractor
- MsExcelExtractor
- MsPowerPointExtractor
- TextExtractor

**Test Count**: 8 tests

**Key Scenarios**:
- ✅ Successful extraction closes resources properly
- ✅ Failed extraction includes descriptive error messages
- ✅ Null input stream validation
- ✅ Error messages include file type context

---

### 2. FilenameExtractorEnhancedTest.java
**Purpose**: Test FilenameExtractor edge cases and new documentation.

**Key Test Areas**:
- Parameter handling (null, empty, missing)
- Special character handling
- Input stream validation (not consumption)
- Edge cases (long filenames, paths, whitespace)

**Test Count**: 10 tests

**Key Scenarios**:
- ✅ Valid filename extraction
- ✅ Null parameters handling
- ✅ Empty/missing resource name
- ✅ Special characters in filename (Japanese, paths)
- ✅ Input stream not consumed (only validated)
- ✅ Whitespace and empty string handling

---

### 3. ArchiveExtractorErrorHandlingTest.java
**Purpose**: Test improved error handling in archive extractors.

**Key Test Areas**:
- Enhanced error messages
- Partial extraction (continues on failure)
- Invalid archive handling
- Empty archive handling
- Mixed valid/invalid entries

**Covered Extractors**:
- ZipExtractor
- TarExtractor

**Test Count**: 12 tests

**Key Scenarios**:
- ✅ Descriptive error messages for invalid archives
- ✅ Continues processing when some entries fail
- ✅ Handles empty archives gracefully
- ✅ Mixed valid/invalid entries processed correctly
- ✅ Null input stream validation

---

### 4. AbstractExtractorTest.java
**Purpose**: Test the base AbstractExtractor functionality.

**Key Test Areas**:
- `validateInputStream()` method behavior
- Exception types and messages
- Stream consumption (should not consume)
- Consistency across multiple calls

**Test Count**: 11 tests

**Key Scenarios**:
- ✅ Validates non-null streams
- ✅ Throws CrawlerSystemException for null
- ✅ Called during getText execution
- ✅ Does not consume or modify stream
- ✅ Consistent behavior across multiple calls
- ✅ Works with various InputStream types

---

### 5. TextExtractorEnhancedTest.java
**Purpose**: Test TextExtractor improvements and encoding handling.

**Key Test Areas**:
- Improved error messages with encoding
- Various encoding support
- Edge cases (empty, large, Unicode)
- Special characters handling

**Test Count**: 12 tests

**Key Scenarios**:
- ✅ Default UTF-8 encoding extraction
- ✅ Custom encoding support
- ✅ Error messages include encoding information
- ✅ Large content handling
- ✅ Unicode and special characters
- ✅ Empty and whitespace-only content

---

## Test Coverage Summary

### Total Tests: 53 tests

### Coverage by Component:
1. **Resource Management**: 8 tests
2. **Input Validation**: 11 tests
3. **Error Handling**: 12 tests
4. **Edge Cases**: 10 tests
5. **Encoding Support**: 12 tests

### Coverage by Extractor:
- AbstractExtractor: 11 tests
- MsWordExtractor: 3 tests
- MsExcelExtractor: 3 tests
- MsPowerPointExtractor: 3 tests
- TextExtractor: 13 tests
- FilenameExtractor: 10 tests
- ZipExtractor: 6 tests
- TarExtractor: 6 tests

## Running the Tests

### Run all extractor tests:
```bash
mvn test -Dtest=Extractor*Test
```

### Run specific test classes:
```bash
mvn test -Dtest=ExtractorResourceManagementTest
mvn test -Dtest=AbstractExtractorTest
mvn test -Dtest=ArchiveExtractorErrorHandlingTest
```

### Run tests for specific extractor:
```bash
mvn test -Dtest=MsWordExtractorTest,ExtractorResourceManagementTest
```

## Test Requirements

### Dependencies:
- JUnit 4.x
- DBFlute UTFlute (PlainTestCase)
- Apache POI (for MS Office tests)
- Apache Commons Compress (for archive tests)

### Test Resources:
- `extractor/msoffice/test.doc`
- `extractor/msoffice/test.xls`
- `extractor/msoffice/test.ppt`
- `extractor/test.txt`
- `extractor/zip/test.zip`

## Key Improvements Tested

### 1. Resource Management
- ✅ Try-with-resources usage in MS Office extractors
- ✅ Proper closure of POI objects
- ✅ No resource leaks on exception

### 2. Input Validation
- ✅ Consistent `validateInputStream()` usage
- ✅ Reduced code duplication
- ✅ Clear exception messages

### 3. Error Handling
- ✅ Archive extractors continue on partial failures
- ✅ Processing statistics in error messages
- ✅ Specific file names in error logs

### 4. Error Messages
- ✅ Encoding information in TextExtractor errors
- ✅ File type context in MS Office errors
- ✅ Archive type in archive extractor errors

## Testing Best Practices

1. **Isolation**: Each test is independent and doesn't affect others
2. **Clarity**: Test names clearly describe what is being tested
3. **Coverage**: Both success and failure paths are tested
4. **Edge Cases**: Null, empty, and boundary conditions are covered
5. **Resources**: Test resources are properly managed and cleaned up

## Contributing

When adding new tests:
1. Follow the existing naming convention: `test_<method>_<scenario>_<expectedResult>`
2. Add clear JavaDoc comments explaining the test purpose
3. Ensure tests are independent and can run in any order
4. Clean up resources in finally blocks or use try-with-resources
5. Update this README with new test information
