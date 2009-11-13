/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.robot.dbflute.helper.mapstring.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.helper.mapstring.MapListString;

/**
 * The implementation of MapList-String.
 * @author jflute
 */
public class MapListStringImpl implements MapListString {

    /** Line separator. */
    public static final String NEW_LINE = System.getProperty("line.separator");

    /** Map-mark. */
    protected String _mapMark;

    /** List-mark. */
    protected String _listMark;

    /** Start-brace. */
    protected String _startBrace;

    /** End-brace. */
    protected String _endBrace;

    /** Delimiter. */
    protected String _delimiter;

    /** Equal. */
    protected String _equal;

    /** Top string. */
    protected String _topString;

    /** Remainder string. */
    protected String _remainderString;

    /**
     * Constructor.
     */
    public MapListStringImpl() {
        _mapMark = DEFAULT_MAP_MARK;
        _listMark = DEFAULT_LIST_MARK;
        _startBrace = DEFAULT_START_BRACE;
        _endBrace = DEFAULT_END_BRACE;
        _delimiter = DEFAULT_DELIMITER;
        _equal = DEFAULT_EQUAL;
    }

    // ==========================================================================================
    //                                                                                     Setter
    //                                                                                     ======
    /**
     * Set map-mark.
     * 
     * @param mapMark Map mark. (NotNull)
     */
    public void setMapMark(String mapMark) {
        _mapMark = mapMark;
    }

    /**
     * Set list-mark.
     * 
     * @param listMark List mark. (NotNull)
     */
    public void setListMark(String listMark) {
        _listMark = listMark;
    }

    /**
     * Set start-brace.
     * 
     * @param startBrace Start-brace. (NotNull)
     */
    public synchronized void setStartBrace(String startBrace) {
        _startBrace = startBrace;
    }

    /**
     * Set end-brace.
     * 
     * @param endBrace End-brace. (NotNull)
     */
    public synchronized void setEndBrace(String endBrace) {
        _endBrace = endBrace;
    }

    /**
     * Set delimiter.
     * 
     * @param delimiter Delimiter. (NotNull)
     */
    public synchronized void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    /**
     * Set equal.
     * 
     * @param equal Equal. (NotNull)
     */
    public void setEqual(String equal) {
        _equal = equal;
    }

    // ****************************************************************************************************
    //                                                                                          Main Method
    //                                                                                          ***********

    // ==========================================================================================
    //                                                                                   Generate
    //                                                                                   ========
    /**
     * Generate map from map-string. {Implement}
     * 
     * @param mapString Map-string (NotNull)
     * @return Generated map. (NotNull)
     */
    public synchronized Map<String, Object> generateMap(String mapString) {
        assertMapString(mapString);

        _topString = mapString;
        _remainderString = mapString;

        removeBothSideSpaceAndTabAndNewLine();
        removePrefixMapMarkAndStartBrace();

        final Map<String, Object> generatedMap = newStringObjectMap();
        parseRemainderMapString(generatedMap);
        if (!"".equals(_remainderString)) {
            String msg = "Final remainderString must be empty string:";
            msg = msg + getNewLineAndIndent() + " # remainderString --> " + _remainderString;
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString;
            msg = msg + getNewLineAndIndent() + " # generatedMap --> " + generatedMap;
            throw new IllegalStateException(msg);
        }
        return generatedMap;
    }

    /**
     * Generate map from list-string. {Implement}
     * 
     * @param listString List-string (NotNull)
     * @return Generated list. (NotNull)
     */
    public synchronized List<Object> generateList(String listString) {
        assertListString(listString);

        _topString = listString;
        _remainderString = listString;

        removeBothSideSpaceAndTabAndNewLine();
        removePrefixListMarkAndStartBrace();

        final List<Object> generatedList = newObjectList();
        parseRemainderListString(generatedList);
        if (!"".equals(_remainderString)) {
            String msg = "Final remainderString must be empty string:";
            msg = msg + getNewLineAndIndent() + " # remainderString --> " + _remainderString;
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString;
            msg = msg + getNewLineAndIndent() + " # generatedList --> " + generatedList;
            throw new IllegalStateException(msg);
        }
        return generatedList;
    }

    // ==========================================================================================
    //                                                                                      Parse
    //                                                                                      =====
    /**
     * Parse remainder map string.
     * 
     * @param currentMap current map.
     */
    protected void parseRemainderMapString(final Map<String, Object> currentMap) {
        while (true) {
            if (initializeAtLoopBeginning()) {
                return;
            }

            // *** Now, _remainderString should starts with the key of the map. ***

            final int equalIndex = _remainderString.indexOf(_equal);
            assertEqualIndex(_remainderString, equalIndex, _topString, currentMap);
            final String mapKey = _remainderString.substring(0, equalIndex).trim();
            removePrefixTargetIndexPlus(equalIndex, _equal.length());
            removeBothSideSpaceAndTabAndNewLine();

            // *** Now, _remainderString should starts with the value of the map. ***

            if (isStartsWithMapPrefix(_remainderString)) {
                removePrefixMapMarkAndStartBrace();
                parseRemainderMapString(setupNestMap(currentMap, mapKey));
                if (closingAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            if (isStartsWithListPrefix(_remainderString)) {
                removePrefixListMarkAndStartBrace();
                parseRemainderListString(setupNestList(currentMap, mapKey));
                if (closingAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            final int delimiterIndex = _remainderString.indexOf(_delimiter);
            final int endBraceIndex = _remainderString.indexOf(_endBrace);
            assertEndBracekIndex(_remainderString, endBraceIndex, _topString, currentMap);

            // If delimiter exists and delimiter is closer than end brace, 
            // Everything from the head of the present remainder string to the delimiter becomes map value.
            //   ex) value1,key2=value2}
            if (delimiterIndex >= 0 && delimiterIndex < endBraceIndex) {
                final String mapValue = _remainderString.substring(0, delimiterIndex);
                currentMap.put(mapKey, filterMapListValue(mapValue));

                // Because the map element continues since the delimiter, skip the delimiter and continue the loop.
                removePrefixTargetIndexPlus(delimiterIndex, _delimiter.length());
                continue;
            }

            // Everything from the head of the present remainder string to the delimiter becomes map value.
            //   ex) value1}, key2=value2}
            final String mapValue = _remainderString.substring(0, endBraceIndex);
            currentMap.put(mapKey, filterMapListValue(mapValue));

            // Analyzing map is over. So closing and return.
            closingByEndBraceIndex(endBraceIndex);
            return;
        }
    }

    /**
     * Parse remainder list string.
     * 
     * @param currentList current list.
     */
    protected void parseRemainderListString(final List<Object> currentList) {
        while (true) {
            if (initializeAtLoopBeginning()) {
                return;
            }

            // *** Now, _remainderString should starts with the value of the list. ***

            if (isStartsWithMapPrefix(_remainderString)) {
                removePrefixMapMarkAndStartBrace();
                parseRemainderMapString(setupNestMap(currentList));
                if (closingAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            if (isStartsWithListPrefix(_remainderString)) {
                removePrefixListMarkAndStartBrace();
                parseRemainderListString(setupNestList(currentList));
                if (closingAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            final int delimiterIndex = _remainderString.indexOf(_delimiter);
            final int endBraceIndex = _remainderString.indexOf(_endBrace);
            assertEndBraceIndex(_remainderString, endBraceIndex, _topString, currentList);

            // If delimiter exists and delimiter is closer than end brace, 
            // Everything from the head of the present remainder string to the delimiter becomes list value.
            //   ex) value1,value2,value3}
            if (delimiterIndex >= 0 && delimiterIndex < endBraceIndex) {
                final String listValue = _remainderString.substring(0, delimiterIndex);
                currentList.add(filterMapListValue(listValue));

                // Because the list element continues since the delimiter, skip the delimiter and continue the loop.
                removePrefixTargetIndexPlus(delimiterIndex, _delimiter.length());
                continue;
            }

            // Everything from the head of the present remainder string to the delimiter becomes list value.
            //   ex) value1}, value2, }
            final String listValue = _remainderString.substring(0, endBraceIndex);
            currentList.add(filterMapListValue(listValue));

            // Analyzing list is over. So closing and return.
            closingByEndBraceIndex(endBraceIndex);
            return;
        }
    }

    /**
     * Initialize at loop beginning.
     * 
     * @return Is return?
     */
    protected boolean initializeAtLoopBeginning() {
        // Remove prefix delimiter. (Result string is always trimmed.)
        removePrefixAllDelimiter();

        // If the remainder string is empty-string, Analyzing is over!
        if (_remainderString.equals("")) {
            return true;
        }

        // If the remainder string starts with end-brace, Analyzing current map is over!
        // And then remove the end-brace.
        if (isStartsWithEndBrace(_remainderString)) {
            removePrefixEndBrace();
            return true;
        }
        return false;
    }

    /**
     * Close after parse nest map list.
     * 
     * @return Is return?
     */
    protected boolean closingAfterParseNestMapList() {
        // If the remainder string starts with end-brace, remove it and return true.
        if (isStartsWithEndBrace(_remainderString)) {
            removePrefixEndBrace();
            return true;
        }
        return false;
    }

    /**
     * Close by end-brace index.
     * 
     * @param endBraceIndex End-brace index.
     */
    protected void closingByEndBraceIndex(int endBraceIndex) {
        // Remove the value that was finished analyzing and end-brace.
        _remainderString = _remainderString.substring(endBraceIndex);
        removePrefixEndBrace();
    }

    // ****************************************************************************************************
    //                                                                                      StateFul Method
    //                                                                                      ***************

    // ==========================================================================================
    //                                                                                     Remove
    //                                                                                     ======
    /**
     * Remove prefix map-mark and start-brace.
     */
    protected void removePrefixMapMarkAndStartBrace() {
        removePrefix(_mapMark + _startBrace);
    }

    /**
     * Remove prefix list-mark and start-brace.
     */
    protected void removePrefixListMarkAndStartBrace() {
        removePrefix(_listMark + _startBrace);
    }

    /**
     * Remove prefix delimiter.
     */
    protected void removePrefixDelimiter() {
        removePrefix(_delimiter);
    }

    /**
     * Remove prefix end-brace.
     */
    protected void removePrefixEndBrace() {
        removePrefix(_endBrace);
    }

    /**
     * Remove prefix.
     * 
     * @param prefixString Prefix string. (NotNull)
     */
    protected void removePrefix(String prefixString) {
        if (_remainderString == null) {
            String msg = "Argument[remainderString] must not be null: " + _remainderString;
            throw new IllegalArgumentException(msg);
        }
        if (prefixString == null) {
            String msg = "Argument[prefixString] must not be null!";
            throw new IllegalArgumentException(msg);
        }

        removeBothSideSpaceAndTabAndNewLine();

        if (_remainderString.length() < prefixString.length()) {
            String msg = "Argument[remainderString] length must be larger than Argument[prefixString] length:";
            msg = msg + getNewLineAndIndent() + " # remainderString --> " + _remainderString;
            msg = msg + getNewLineAndIndent() + " # prefixString=" + prefixString;
            throw new IllegalArgumentException(msg);
        }
        if (!_remainderString.startsWith(prefixString)) {
            String msg = "Argument[remainderString] must start with Argument[prefixString:]";
            msg = msg + getNewLineAndIndent() + " # remainderString --> " + _remainderString;
            msg = msg + getNewLineAndIndent() + " # prefixString --> " + prefixString;
            throw new IllegalArgumentException(msg);
        }

        _remainderString = _remainderString.substring(prefixString.length());
        removeBothSideSpaceAndTabAndNewLine();
    }

    /**
     * Remove prefix and delimiter.
     */
    protected void removePrefixAllDelimiter() {
        removeBothSideSpaceAndTabAndNewLine();

        while (true) {
            if (!isStartsWithDelimiter(_remainderString)) {
                break;
            }

            if (isStartsWithDelimiter(_remainderString)) {
                removePrefixDelimiter();
                removeBothSideSpaceAndTabAndNewLine();
            }
        }
    }

    /**
     * Remove both side space and tab and new-line.
     */
    protected void removeBothSideSpaceAndTabAndNewLine() {
        _remainderString = _remainderString.trim();
    }

    /**
     * Remove prefix (target index plus one).
     * 
     * @param index Index.
     * @param plusCount Plus count.
     */
    protected void removePrefixTargetIndexPlus(int index, int plusCount) {
        _remainderString = _remainderString.substring(index + plusCount);
    }

    // ****************************************************************************************************
    //                                                                                     StateLess Method
    //                                                                                     ****************

    // ==========================================================================================
    //                                                                                     Assert
    //                                                                                     ======
    /**
     * Assert map-string.
     * 
     * @param mapString Map-string. (NotNull)
     */
    protected void assertMapString(String mapString) {
        if (mapString == null) {
            String msg = "Argument[mapString] must not be null: ";
            throw new IllegalArgumentException(msg + "mapString=null");
        }
        mapString = mapString.trim();
        if (!isStartsWithMapPrefix(mapString)) {
            String msg = "Argument[mapString] must start with '" + _mapMark + _startBrace + "': ";
            throw new IllegalArgumentException(msg + "mapString=" + mapString);
        }
        if (!isEndsWithEndBrace(mapString)) {
            String msg = "Argument[mapString] must end with '" + _endBrace + "': ";
            throw new IllegalArgumentException(msg + "mapString=" + mapString);
        }

        final int startBraceCount = getDelimiterCount(mapString, _startBrace);
        final int endBraceCount = getDelimiterCount(mapString, _endBrace);
        if (startBraceCount != endBraceCount) {
            String msg = "It is necessary to have braces of the same number on start and end:";
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString;
            msg = msg + getNewLineAndIndent() + " # startBraceCount --> " + startBraceCount;
            msg = msg + getNewLineAndIndent() + " # endBraceCount --> " + endBraceCount;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert list-string.
     * 
     * @param listString List-string. (NotNull)
     */
    protected void assertListString(String listString) {
        if (listString == null) {
            String msg = "Argument[listString] must not be null: ";
            throw new IllegalArgumentException(msg + "listString=null");
        }
        listString = listString.trim();
        if (!isStartsWithListPrefix(listString)) {
            String msg = "Argument[listString] must start with '" + _mapMark + "': ";
            throw new IllegalArgumentException(msg + "listString=" + listString);
        }
        if (!isEndsWithEndBrace(listString)) {
            String msg = "Argument[listString] must end with '" + _endBrace + "': ";
            throw new IllegalArgumentException(msg + "listString=" + listString);
        }

        final int startBraceCount = getDelimiterCount(listString, _startBrace);
        final int endBraceCount = getDelimiterCount(listString, _endBrace);
        if (startBraceCount != endBraceCount) {
            String msg = "It is necessary to have braces of the same number on start and end:";
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString;
            msg = msg + getNewLineAndIndent() + " # startBraceCount --> " + startBraceCount;
            msg = msg + getNewLineAndIndent() + " # endBraceCount --> " + endBraceCount;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert equal-index.
     * 
     * @param remainderMapString Remainder map-string. (NotNull)
     * @param equalIndex Equal-index.
     * @param mapString4Log Map-string for log. (NotNull)
     * @param currentMap4Log Current-map for log. (NotNull)
     */
    protected void assertEqualIndex(String remainderMapString, int equalIndex, String mapString4Log,
            Map<String, Object> currentMap4Log) {
        if (remainderMapString == null) {
            String msg = "Argument[remainderMapString] must not be null:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> null";
            msg = msg + getNewLineAndIndent() + " # equalIndex --> " + equalIndex;
            msg = msg + getNewLineAndIndent() + " # mapString4Log --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap4Log --> " + currentMap4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (equalIndex < 0) {
            String msg = "Argument[equalIndex] must be plus or zero:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # equalIndex --> " + equalIndex;
            msg = msg + getNewLineAndIndent() + " # mapString4Log --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap4Log --> " + currentMap4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (remainderMapString.length() < equalIndex) {
            String msg = "Argument[remainderMapString] length must be larger than equalIndex value:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # equalIndex --> " + equalIndex;
            msg = msg + getNewLineAndIndent() + " # mapString4Log --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap4Log --> " + currentMap4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndMark = remainderMapString.substring(equalIndex, equalIndex + _equal.length());
        if (!expectedAsEndMark.equals(_equal)) {
            String msg = "Argument[remainderMapString] must have '" + _equal + "' at Argument[equalIndex]:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # equalIndex --> " + equalIndex;
            msg = msg + getNewLineAndIndent() + " # expectedAsEndMark --> " + expectedAsEndMark;
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap --> " + currentMap4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert end-brace-index.
     * 
     * @param remainderMapString Remainder map-string. (NotNull)
     * @param endBraceIndex End-brace-index.
     * @param mapString4Log Map-string for log. (NotNull)
     * @param currentMap4Log Current-map for log. (NotNull)
     */
    protected void assertEndBracekIndex(String remainderMapString, int endBraceIndex, String mapString4Log,
            Map<String, Object> currentMap4Log) {
        if (remainderMapString == null) {
            String msg = "Argument[remainderMapString] must not be null:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> null";
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap --> " + currentMap4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (endBraceIndex < 0) {
            String msg = "Argument[endMarkIndex] must be plus or zero:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # mapString --> =" + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap --> " + currentMap4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (remainderMapString.length() < endBraceIndex) {
            String msg = "Argument[remainderMapString] length must be larger than endMarkIndex value:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap --> " + currentMap4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndMark = remainderMapString
                .substring(endBraceIndex, endBraceIndex + _endBrace.length());
        if (!expectedAsEndMark.equals(_endBrace)) {
            String msg = "Argument[remainderMapString] must have '" + _endBrace + "' at Argument[endBraceIndex]:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # expectedAsEndMark --> " + expectedAsEndMark;
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap --> " + currentMap4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert end-brace-index.
     * @param remainderListString Remainder list-string. (NotNull)
     * @param endBraceIndex End-brace-index.
     * @param listString4Log List-string for log. (NotNull)
     * @param currentList4Log Current-list for log. (NotNull)
     */
    protected void assertEndBraceIndex(String remainderListString, int endBraceIndex, String listString4Log,
            List<?> currentList4Log) {
        if (remainderListString == null) {
            String msg = "Argument[remainderListString] must not be null:";
            msg = msg + getNewLineAndIndent() + " # remainderListString --> null";
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString4Log;
            msg = msg + getNewLineAndIndent() + " # currentList --> " + currentList4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (endBraceIndex < 0) {
            String msg = "Argument[endMarkIndex] must be plus or zero:";
            msg = msg + getNewLineAndIndent() + " # remainderListString --> " + remainderListString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString4Log;
            msg = msg + getNewLineAndIndent() + " # currentList --> " + currentList4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (remainderListString.length() < endBraceIndex) {
            String msg = "Argument[remainderListString] length must be larger than endMarkIndex value:";
            msg = msg + getNewLineAndIndent() + " # remainderListString --> " + remainderListString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString4Log;
            msg = msg + getNewLineAndIndent() + " # currentList --> " + currentList4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndBrace = remainderListString.substring(endBraceIndex, endBraceIndex
                + _endBrace.length());
        if (!expectedAsEndBrace.equals(_endBrace)) {
            String msg = "Argument[remainderListString] must have '" + _endBrace + "' at Argument[endBraceIndex]:";
            msg = msg + getNewLineAndIndent() + " # remainderListString --> " + remainderListString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # expectedAsEndBrace --> " + expectedAsEndBrace;
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString4Log;
            msg = msg + getNewLineAndIndent() + " # currentList --> " + currentList4Log;
            msg = msg + getNewLineAndIndent() + " # _startBrace --> " + _startBrace;
            msg = msg + getNewLineAndIndent() + " # _endBrace --> " + _endBrace;
            msg = msg + getNewLineAndIndent() + " # _delimiter --> " + _delimiter;
            msg = msg + getNewLineAndIndent() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }
    }

    // ==========================================================================================
    //                                                                                     Filter
    //                                                                                     ======
    /**
     * Filter map or list value.
     * <p>
     * <pre>
     * # The value is trimmed.
     * # If the value is null, this returns null.
     * # If the value is 'null', this returns null.
     * # If the trimmed value is empty string, this returns null.
     * </pre>
     * @param value value. (Nullable)
     * @return Filtered value. (Nullable)
     */
    protected String filterMapListValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return (("".equals(value) || "null".equals(value)) ? null : value);
    }

    // ==========================================================================================
    //                                                                                  Judgement
    //                                                                                  =========
    /**
     * Does it start with map-prefix?
     * 
     * @param targetString Target-string. (NotNull)
     * @return Determination.
     */
    protected boolean isStartsWithMapPrefix(String targetString) {
        if (targetString == null) {
            String msg = "Argument[targetString] must not be null!";
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        if (targetString.startsWith(_mapMark + _startBrace)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Does it start with list-prefix?
     * 
     * @param targetString Target-string. (NotNull)
     * @return Determination.
     */
    protected boolean isStartsWithListPrefix(String targetString) {
        if (targetString == null) {
            String msg = "Argument[targetString] must not be null!";
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        if (targetString.startsWith(_listMark + _startBrace)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Does it start with delimiter?
     * 
     * @param targetString Target-string. (NotNull)
     * @return Determination.
     */
    protected boolean isStartsWithDelimiter(String targetString) {
        if (targetString == null) {
            String msg = "Argument[targetString] must not be null!";
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        if (targetString.startsWith(_delimiter)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Does it start with end-brace?
     * 
     * @param targetString Target-string. (NotNull)
     * @return Determination.
     */
    protected boolean isStartsWithEndBrace(String targetString) {
        if (targetString == null) {
            String msg = "Argument[targetString] must not be null!";
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        if (targetString.startsWith(_endBrace)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Does it end with end-brace?
     * 
     * @param targetString Target-string. (NotNull)
     * @return Determination.
     */
    protected boolean isEndsWithEndBrace(String targetString) {
        if (targetString == null) {
            String msg = "Argument[targetString] must not be null!";
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        if (targetString.endsWith(_endBrace)) {
            return true;
        } else {
            return false;
        }
    }

    // ==========================================================================================
    //                                                                                      Other
    //                                                                                      =====
    /**
     * Setup nest map.
     * 
     * @param currentMap Current-map. (NotNull)
     * @param mapKey Map-key. (NotNull)
     * @return Nest map. (NotNull)
     */
    protected Map<String, Object> setupNestMap(Map<String, Object> currentMap, String mapKey) {
        final Map<String, Object> nestMap = newStringObjectMap();
        currentMap.put(mapKey, nestMap);
        return nestMap;
    }

    /**
     * Setup nest map.
     * 
     * @param currentList Current-list. (NotNull)
     * @return Nest map. (NotNull)
     */
    protected Map<String, Object> setupNestMap(List<Object> currentList) {
        final Map<String, Object> nestMap = newStringObjectMap();
        currentList.add(nestMap);
        return nestMap;
    }

    /**
     * Setup nest list.
     * 
     * @param currentMap Current-map. (NotNull)
     * @param mapKey Map-key. (NotNull)
     * @return Nest list. (NotNull)
     */
    protected List<Object> setupNestList(Map<String, Object> currentMap, String mapKey) {
        final List<Object> nestList = newObjectList();
        currentMap.put(mapKey, nestList);
        return nestList;
    }

    /**
     * Setup nest list.
     * 
     * @param currentList Current-list. (NotNull)
     * @return Nest list. (NotNull)
     */
    protected List<Object> setupNestList(List<Object> currentList) {
        final List<Object> nestList = newObjectList();
        currentList.add(nestList);
        return nestList;
    }

    /**
     * New string-object-map.
     * 
     * @return String-object-map. (NotNull)
     */
    protected Map<String, Object> newStringObjectMap() {
        return new LinkedHashMap<String, Object>();
    }

    /**
     * New object-list.
     * 
     * @return String-object-list. (NotNull)
     */
    protected List<Object> newObjectList() {
        return new ArrayList<Object>();
    }

    /**
     * Get new-line and indent.
     * 
     * @return New-line and indent. (NotNull)
     */
    protected String getNewLineAndIndent() {
        return NEW_LINE + "    ";
    }

    /**
     * Get count that target string exist in the base string.
     * 
     * @param targetString Target string.
     * @param delimiter Delimiter
     * @return Delimiter count that _remainderString contains.
     */
    protected int getDelimiterCount(String targetString, String delimiter) {
        int result = 0;
        for (int i = 0;;) {
            if (targetString.indexOf(delimiter, i) != -1) {
                result++;
                i = targetString.indexOf(delimiter, i) + 1;
            } else {
                break;
            }
        }
        if (result == 0) {
            result = -1;
        }
        return result;
    }
}