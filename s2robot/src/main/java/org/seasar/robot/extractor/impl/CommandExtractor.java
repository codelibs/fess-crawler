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
package org.seasar.robot.extractor.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.extractor.ExecutionTimeoutException;
import org.seasar.robot.extractor.ExtractException;
import org.seasar.robot.extractor.Extractor;
import org.seasar.robot.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exctract a text by running a command.
 * 
 * @author shinsuke
 * 
 */
public class CommandExtractor implements Extractor {
    private static final Logger logger = LoggerFactory
        .getLogger(CommandExtractor.class);

    public String outputEncoding = Constants.UTF_8;

    public File tempDir = null;

    public String command;

    public long executionTimeout = 30L * 1000L; // 30sec

    public File workingDirectory = null;

    public String commandOutputEncoding = System.getProperty("file.encoding");

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.extractor.Extractor#getText(java.io.InputStream,
     * java.util.Map)
     */
    public ExtractData getText(InputStream in, Map<String, String> params) {
        String resourceName =
            params != null ? params.get(ExtractData.RESOURCE_NAME_KEY) : null;

        String extention;
        String filePrefix;
        if (StringUtil.isNotBlank(resourceName)) {
            String[] strings = resourceName.split("\\.");
            StringBuilder buf = new StringBuilder();
            if (strings.length > 1) {
                for (int i = 0; i < strings.length - 1; i++) {
                    if (buf.length() != 0) {
                        buf.append('.');
                    }
                    buf.append(strings[i]);
                }
                filePrefix = buf.toString();
                extention = strings[strings.length - 1];
            } else {
                filePrefix = resourceName;
                extention = "";
            }
        } else {
            filePrefix = "none";
            extention = "";
        }
        File inputFile = null;
        File outputFile = null;
        try {
            inputFile =
                File.createTempFile(
                    "cmdextin_" + filePrefix + "_",
                    extention,
                    tempDir);
            outputFile =
                File.createTempFile(
                    "cmdextout_" + filePrefix + "_",
                    extention,
                    tempDir);

            // store to a file
            StreamUtil.drain(in, inputFile);

            executeCommand(inputFile, outputFile);

            ExtractData extractData =
                new ExtractData(new String(
                    FileUtil.getBytes(outputFile),
                    outputEncoding));
            if (StringUtil.isNotBlank(resourceName)) {
                extractData.putValues(
                    "resourceName",
                    new String[] { resourceName });
            }

            return extractData;
        } catch (IOException e) {
            throw new ExtractException("Could not extract a content.", e);
        } finally {
            if (inputFile != null && !inputFile.delete()) {
                logger.info("Failed to delete " + inputFile.getAbsolutePath());
            }
            if (outputFile != null && !outputFile.delete()) {
                logger.info("Failed to delete " + outputFile.getAbsolutePath());
            }
        }
    }

    private void executeCommand(File inputFile, File outputFile) {

        if (StringUtil.isBlank(command)) {
            throw new RobotSystemException("command is empty.");
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("$INPUT_FILE", inputFile.getAbsolutePath());
        params.put("$OUTPUT_FILE", outputFile.getAbsolutePath());

        List<String> cmdList = parseCommand(command, params);
        if (logger.isInfoEnabled()) {
            logger.info("Command: " + cmdList);
        }

        ProcessBuilder pb = new ProcessBuilder(cmdList);
        if (workingDirectory != null) {
            pb.directory(workingDirectory);
        }
        pb.redirectErrorStream(true);

        Process currentProcess = null;
        MonitorThread mt = null;
        try {
            currentProcess = pb.start();

            // monitoring
            mt = new MonitorThread(currentProcess, executionTimeout);
            mt.start();

            InputStreamThread it =
                new InputStreamThread(
                    currentProcess.getInputStream(),
                    commandOutputEncoding);
            it.start();

            currentProcess.waitFor();
            it.join(5000);

            if (mt.isTeminated()) {
                throw new ExecutionTimeoutException(
                    "The command execution is timeout: " + cmdList);
            }

            int exitValue = currentProcess.exitValue();

            if (logger.isInfoEnabled()) {
                logger.info("Exit Code: " + exitValue + " - Process Output:\n"
                    + it.getOutput());
            }
        } catch (RobotSystemException e) {
            throw e;
        } catch (InterruptedException e) {
            if (mt != null && mt.isTeminated()) {
                throw new ExecutionTimeoutException(
                    "The command execution is timeout: " + cmdList,
                    e);
            } else {
                throw new RobotSystemException("Process terminated.", e);
            }
        } catch (Exception e) {
            throw new RobotSystemException("Process terminated.", e);
        } finally {
            if (mt != null) {
                mt.setFinished(true);
                try {
                    mt.interrupt();
                } catch (Exception e) {
                }
            }
            if (currentProcess != null) {
                try {
                    currentProcess.destroy();
                } catch (Exception e) {
                }
            }
            currentProcess = null;

        }
    }

    /**
     * @param command2
     * @param params
     * @return
     */
    List<String> parseCommand(String command, Map<String, String> params) {
        String cmd = command.trim();
        List<String> cmdList = new ArrayList<String>();
        StringBuilder buf = new StringBuilder();
        boolean singleQuote = false;
        boolean doubleQuote = false;
        char prevChar = ' ';
        for (int i = 0; i < cmd.length(); i++) {
            char c = cmd.charAt(i);
            if (c == ' ' && !singleQuote && !doubleQuote && buf.length() > 0) {
                cmdList.add(getCommandValue(buf.toString(), params));
                buf.delete(0, buf.length());
            } else if (c == '\"' && !singleQuote && prevChar != '\\') {
                if (doubleQuote) {
                    if (buf.length() > 0) {
                        cmdList.add(getCommandValue(buf.toString(), params));
                        buf.delete(0, buf.length());
                    }

                    doubleQuote = false;
                } else {
                    doubleQuote = true;
                }
            } else if (c == '\'' && !doubleQuote && prevChar != '\\') {
                if (singleQuote) {
                    if (buf.length() > 0) {
                        cmdList.add(getCommandValue(buf.toString(), params));
                        buf.delete(0, buf.length());
                    }

                    singleQuote = false;
                } else {
                    singleQuote = true;
                }
            } else {
                buf.append(c);
            }
            prevChar = c;
        }
        if (buf.length() > 0) {
            cmdList.add(getCommandValue(buf.toString(), params));
        }
        return cmdList;
    }

    private String getCommandValue(String key, Map<String, String> params) {
        String value = params.get(key);
        if (value == null) {
            return key;
        }
        return value;
    }

    protected static class MonitorThread extends Thread {
        private Process process;

        private long timeout;

        private boolean finished = false;

        private boolean teminated = false;

        public MonitorThread(Process process, long timeout) {
            this.process = process;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
            }

            if (!finished) {
                try {
                    process.destroy();
                    teminated = true;
                } catch (Exception e) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Could not kill the subprocess.", e);
                    }
                }
            }
        }

        /**
         * @param finished
         *            The finished to set.
         */
        public void setFinished(boolean finished) {
            this.finished = finished;
        }

        /**
         * @return Returns the teminated.
         */
        public boolean isTeminated() {
            return teminated;
        }
    }

    protected static class InputStreamThread extends Thread {

        private BufferedReader br;

        private List<String> list = new LinkedList<String>();

        private int maxLineBuffer = 1000;

        public InputStreamThread(InputStream is, String charset) {
            try {
                br = new BufferedReader(new InputStreamReader(is, charset));
            } catch (UnsupportedEncodingException e) {
                throw new RobotSystemException(e);
            }
        }

        @Override
        public void run() {
            for (;;) {
                try {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug(line);
                    }
                    list.add(line);
                    if (list.size() > maxLineBuffer) {
                        list.remove(0);
                    }
                } catch (IOException e) {
                    throw new RobotSystemException(e);
                }
            }
        }

        public String getOutput() {
            StringBuilder buf = new StringBuilder();
            for (String value : list) {
                buf.append(value).append("\n");
            }
            return buf.toString();
        }
    }
}
