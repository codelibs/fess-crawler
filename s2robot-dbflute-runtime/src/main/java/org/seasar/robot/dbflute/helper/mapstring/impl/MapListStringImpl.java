/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.helper.mapstring.MapListString;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The basic implementation of map-list-string.
 * @author jflute
 */
public class MapListStringImpl implements MapListString {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The mark of map. */
    protected String _mapMark;

    /** The mark of list. */
    protected String _listMark;

    /** The string of start brace. */
    protected String _startBrace;

    /** The string of end brace. */
    protected String _endBrace;

    /** The string of delimiter. */
    protected String _delimiter;

    /** The string of equal for map-string. */
    protected String _equal;

    /** The string of top as temporary variable for generation. */
    protected String _topString;

    /** The string of remainder as temporary variable for generation. */
    protected String _remainderString;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
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

    // ===================================================================================
    //                                                                               Build
    //                                                                               =====
    /**
     * {@inheritDoc}
     */
    public String buildMapString(Map<String, ? extends Object> map) {
        final StringBuilder sb = new StringBuilder();
        @SuppressWarnings("unchecked")
        final Map<String, Object> casted = (Map<String, Object>) map;
        doBuildMapString(sb, casted, "", "    ");
        return sb.toString();
    }

    protected void doBuildMapString(StringBuilder sb, Map<String, Object> map, String preIndent, String curIndent) {
        sb.append(_mapMark).append(_startBrace);
        final Set<Entry<String, Object>> entrySet = map.entrySet();
        for (Entry<String, ? extends Object> entry : entrySet) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            sb.append(ln()).append(curIndent).append(_delimiter);
            sb.append(" ").append(key).append(" ").append(_equal).append(" ");
            if (value instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> valueMap = (Map<String, Object>) value;
                doBuildMapString(sb, valueMap, curIndent, calculateNextIndent(preIndent, curIndent));
            } else if (value instanceof List<?>) {
                @SuppressWarnings("unchecked")
                final List<Object> valueList = (List<Object>) value;
                doBuildListString(sb, valueList, curIndent, calculateNextIndent(preIndent, curIndent));
            } else {
                sb.append(value);
            }
        }
        sb.append(ln()).append(preIndent).append(_endBrace);
    }

    /**
     * {@inheritDoc}
     */
    public String buildListString(List<? extends Object> list) {
        final StringBuilder sb = new StringBuilder();
        @SuppressWarnings("unchecked")
        final List<Object> casted = (List<Object>) list;
        doBuildListString(sb, casted, "", "    ");
        return sb.toString();
    }

    protected void doBuildListString(StringBuilder sb, List<? extends Object> list, String preIndent, String curIndent) {
        sb.append(_listMark).append(_startBrace);
        for (Object value : list) {
            sb.append(ln()).append(curIndent).append(_delimiter);
            sb.append(" ");
            if (value instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> valueMap = (Map<String, Object>) value;
                doBuildMapString(sb, valueMap, curIndent, calculateNextIndent(preIndent, curIndent));
            } else if (value instanceof List<?>) {
                @SuppressWarnings("unchecked")
                final List<Object> valueList = (List<Object>) value;
                doBuildListString(sb, valueList, curIndent, calculateNextIndent(preIndent, curIndent));
            } else {
                sb.append(value);
            }
        }
        sb.append(ln()).append(preIndent).append(_endBrace);
    }

    protected String calculateNextIndent(String preIndent, String curIndent) {
        final StringBuilder sb = new StringBuilder();
        final int indentLength = curIndent.length() - preIndent.length();
        for (int i = 0; i < indentLength; i++) {
            sb.append(" ");
        }
        return curIndent + sb.toString();
    }

    // ===================================================================================
    //                                                                            Generate
    //                                                                            ========
    /**
     * {@inheritDoc}
     */
    public Map<String, Object> generateMap(String mapString) {
        assertMapString(mapString);

        _topString = mapString;
        _remainderString = mapString;

        removeBothSideSpaceAndTabAndNewLine();
        removePrefixMapMarkAndStartBrace();

        final Map<String, Object> generatedMap = newStringObjectMap();
        parseRemainderMapString(generatedMap);
        if (!"".equals(_remainderString)) {
            String msg = "Final remainderString must be empty string:";
            msg = msg + lnd() + " # remainderString --> " + _remainderString;
            msg = msg + lnd() + " # mapString --> " + mapString;
            msg = msg + lnd() + " # generatedMap --> " + generatedMap;
            throw new IllegalStateException(msg);
        }
        return generatedMap;
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> generateList(String listString) {
        assertListString(listString);

        _topString = listString;
        _remainderString = listString;

        removeBothSideSpaceAndTabAndNewLine();
        removePrefixListMarkAndStartBrace();

        final List<Object> generatedList = newObjectList();
        parseRemainderListString(generatedList);
        if (!"".equals(_remainderString)) {
            String msg = "Final remainderString must be empty string:";
            msg = msg + lnd() + " # remainderString --> " + _remainderString;
            msg = msg + lnd() + " # listString --> " + listString;
            msg = msg + lnd() + " # generatedList --> " + generatedList;
            throw new IllegalStateException(msg);
        }
        return generatedList;
    }

    // ===================================================================================
    //                                                                               Parse
    //                                                                               =====
    /**
     * Parse remainder map string.
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
     * @param endBraceIndex End-brace index.
     */
    protected void closingByEndBraceIndex(int endBraceIndex) {
        // Remove the value that was finished analyzing and end-brace.
        _remainderString = _remainderString.substring(endBraceIndex);
        removePrefixEndBrace();
    }

    // ===================================================================================
    //                                                                              Remove
    //                                                                              ======
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
            msg = msg + lnd() + " # remainderString --> " + _remainderString;
            msg = msg + lnd() + " # prefixString=" + prefixString;
            throw new IllegalArgumentException(msg);
        }
        if (!_remainderString.startsWith(prefixString)) {
            String msg = "Argument[remainderString] must start with Argument[prefixString:]";
            msg = msg + lnd() + " # remainderString --> " + _remainderString;
            msg = msg + lnd() + " # prefixString --> " + prefixString;
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
     * @param index Index.
     * @param plusCount Plus count.
     */
    protected void removePrefixTargetIndexPlus(int index, int plusCount) {
        _remainderString = _remainderString.substring(index + plusCount);
    }

    // ===================================================================================
    //                                                                              Filter
    //                                                                              ======
    /**
     * Filter map or list value.
     * <p>
     * <pre>
     * # The value is trimmed.
     * # If the value is null, this returns null.
     * # If the value is 'null', this returns null.
     * # If the trimmed value is empty string, this returns null.
     * </pre>
     * @param value value. (NullAllowed)
     * @return Filtered value. (NullAllowed)
     */
    protected String filterMapListValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return (("".equals(value) || "null".equals(value)) ? null : value);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * Does it start with map-prefix?
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

    // ===================================================================================
    //                                                                             Various
    //                                                                             =======
    /**
     * Setup nest map.
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
     * @return String-object-map. (NotNull)
     */
    protected Map<String, Object> newStringObjectMap() {
        return new LinkedHashMap<String, Object>();
    }

    /**
     * New object-list.
     * @return String-object-list. (NotNull)
     */
    protected List<Object> newObjectList() {
        return new ArrayList<Object>();
    }

    /**
     * Get count that target string exist in the base string.
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

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    /**
     * Assert map-string.
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
            msg = msg + lnd() + " # mapString --> " + mapString;
            msg = msg + lnd() + " # startBraceCount --> " + startBraceCount;
            msg = msg + lnd() + " # endBraceCount --> " + endBraceCount;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert list-string.
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
            msg = msg + lnd() + " # listString --> " + listString;
            msg = msg + lnd() + " # startBraceCount --> " + startBraceCount;
            msg = msg + lnd() + " # endBraceCount --> " + endBraceCount;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert equal-index.
     * @param remainderMapString Remainder map-string. (NotNull)
     * @param equalIndex Equal-index.
     * @param mapString4Log Map-string for log. (NotNull)
     * @param currentMap4Log Current-map for log. (NotNull)
     */
    protected void assertEqualIndex(String remainderMapString, int equalIndex, String mapString4Log,
            Map<String, Object> currentMap4Log) {
        if (remainderMapString == null) {
            String msg = "Argument[remainderMapString] must not be null:";
            msg = msg + lnd() + " # remainderMapString --> null";
            msg = msg + lnd() + " # equalIndex --> " + equalIndex;
            msg = msg + lnd() + " # mapString4Log --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap4Log --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (equalIndex < 0) {
            String msg = "Argument[equalIndex] must be plus or zero:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # equalIndex --> " + equalIndex;
            msg = msg + lnd() + " # mapString4Log --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap4Log --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (remainderMapString.length() < equalIndex) {
            String msg = "Argument[remainderMapString] length must be larger than equalIndex value:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # equalIndex --> " + equalIndex;
            msg = msg + lnd() + " # mapString4Log --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap4Log --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndMark = remainderMapString.substring(equalIndex, equalIndex + _equal.length());
        if (!expectedAsEndMark.equals(_equal)) {
            String msg = "Argument[remainderMapString] must have '" + _equal + "' at Argument[equalIndex]:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # equalIndex --> " + equalIndex;
            msg = msg + lnd() + " # expectedAsEndMark --> " + expectedAsEndMark;
            msg = msg + lnd() + " # mapString --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert end-brace-index.
     * @param remainderMapString Remainder map-string. (NotNull)
     * @param endBraceIndex End-brace-index.
     * @param mapString4Log Map-string for log. (NotNull)
     * @param currentMap4Log Current-map for log. (NotNull)
     */
    protected void assertEndBracekIndex(String remainderMapString, int endBraceIndex, String mapString4Log,
            Map<String, Object> currentMap4Log) {
        if (remainderMapString == null) {
            String msg = "Argument[remainderMapString] must not be null:";
            msg = msg + lnd() + " # remainderMapString --> null";
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # mapString --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (endBraceIndex < 0) {
            String msg = "Argument[endMarkIndex] must be plus or zero:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # mapString --> =" + mapString4Log;
            msg = msg + lnd() + " # currentMap --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (remainderMapString.length() < endBraceIndex) {
            String msg = "Argument[remainderMapString] length must be larger than endMarkIndex value:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # mapString --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndMark = remainderMapString
                .substring(endBraceIndex, endBraceIndex + _endBrace.length());
        if (!expectedAsEndMark.equals(_endBrace)) {
            String msg = "Argument[remainderMapString] must have '" + _endBrace + "' at Argument[endBraceIndex]:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # expectedAsEndMark --> " + expectedAsEndMark;
            msg = msg + lnd() + " # mapString --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
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
            msg = msg + lnd() + " # remainderListString --> null";
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # listString --> " + listString4Log;
            msg = msg + lnd() + " # currentList --> " + currentList4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (endBraceIndex < 0) {
            String msg = "Argument[endMarkIndex] must be plus or zero:";
            msg = msg + lnd() + " # remainderListString --> " + remainderListString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # listString --> " + listString4Log;
            msg = msg + lnd() + " # currentList --> " + currentList4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (remainderListString.length() < endBraceIndex) {
            String msg = "Argument[remainderListString] length must be larger than endMarkIndex value:";
            msg = msg + lnd() + " # remainderListString --> " + remainderListString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # listString --> " + listString4Log;
            msg = msg + lnd() + " # currentList --> " + currentList4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndBrace = remainderListString.substring(endBraceIndex, endBraceIndex
                + _endBrace.length());
        if (!expectedAsEndBrace.equals(_endBrace)) {
            String msg = "Argument[remainderListString] must have '" + _endBrace + "' at Argument[endBraceIndex]:";
            msg = msg + lnd() + " # remainderListString --> " + remainderListString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # expectedAsEndBrace --> " + expectedAsEndBrace;
            msg = msg + lnd() + " # listString --> " + listString4Log;
            msg = msg + lnd() + " # currentList --> " + currentList4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * Get new-line and indent.
     * @return The string of new-line and indent. (NotNull)
     */
    protected String lnd() {
        return ln() + "    ";
    }

    protected final String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setMapMark(String mapMark) {
        _mapMark = mapMark;
    }

    public void setListMark(String listMark) {
        _listMark = listMark;
    }

    public synchronized void setStartBrace(String startBrace) {
        _startBrace = startBrace;
    }

    public synchronized void setEndBrace(String endBrace) {
        _endBrace = endBrace;
    }

    public synchronized void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    public void setEqual(String equal) {
        _equal = equal;
    }
}