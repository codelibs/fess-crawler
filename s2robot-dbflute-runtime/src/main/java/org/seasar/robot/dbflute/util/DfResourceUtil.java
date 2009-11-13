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
package org.seasar.robot.dbflute.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class DfResourceUtil {

    // ===================================================================================
    //                                                                       Resource Path
    //                                                                       =============
    public static String getResourcePath(String path, String extension) {
        if (extension == null) {
            return path;
        }
        extension = "." + extension;
        if (path.endsWith(extension)) {
            return path;
        }
        return path.replace('.', '/') + extension;
    }

    public static String getResourcePath(Class<?> clazz) {
        return clazz.getName().replace('.', '/') + ".class";
    }

    public static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // ===================================================================================
    //                                                                        Resource URL
    //                                                                        ============
    public static URL getResourceUrl(String path) {
        return getResourceUrl(path, null);
    }

    public static URL getResourceUrl(String path, String extension) {
        return getResourceUrl(path, extension, Thread.currentThread().getContextClassLoader());
    }

    public static URL getResourceUrl(String path, String extension, ClassLoader loader) {
        if (path == null || loader == null) {
            return null;
        }
        path = getResourcePath(path, extension);
        return loader.getResource(path);
    }

    public static String getFileName(URL url) {
        String s = url.getFile();
        return decodeURL(s, "UTF8");
    }

    public static InputStream openStream(URL url) {
        try {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            String msg = "Failed to open the stream: url=" + url;
            throw new IllegalStateException(msg, e);
        }
    }

    public static URLConnection openConnection(URL url) {
        try {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static URL createURL(String spec) {
        try {
            return new URL(spec);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static URL create(URL context, String spec) {
        try {
            return new URL(context, spec);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String decodeURL(String s, String enc) {
        try {
            return URLDecoder.decode(s, enc);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void makeFileAndClose(URL url, String outputFilename) {
        InputStream in;
        try {
            in = url.openStream();
        } catch (IOException e) {
            String msg = url.getClass().getSimpleName() + "#openStream() threw the IO exception!";
            throw new IllegalStateException(msg, e);
        }
        DfResourceUtil.makeFileAndClose(in, outputFilename);
    }

    // ===================================================================================
    //                                                                     Resource Stream
    //                                                                     ===============
    public static InputStream getResourceStream(String path) {
        return getResourceStream(path, null);
    }

    public static InputStream getResourceStream(String path, String extension) {
        final URL url = getResourceUrl(path, extension);
        return url != null ? openStream(url) : null;
    }

    // ===================================================================================
    //                                                                  Resource Existence
    //                                                                  ==================
    public static boolean isExist(String path) {
        return getResourceUrl(path) != null;
    }

    // ===================================================================================
    //                                                                     Reader Handling
    //                                                                     ===============
    public static String readText(Reader reader) {
        BufferedReader in = new BufferedReader(reader);
        StringBuilder out = new StringBuilder(100);
        try {
            try {
                char[] buf = new char[8192];
                int n;
                while ((n = in.read(buf)) >= 0) {
                    out.append(buf, 0, n);
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            String msg = "The IOException occurred: reader=" + reader;
            throw new IllegalStateException(msg, e);
        }
        return out.toString();
    }

    // ===================================================================================
    //                                                            Build Directory Handling
    //                                                            ========================
    public static File getBuildDir(Class<?> clazz) {
        return getBuildDir(getResourcePath(clazz));
    }

    public static File getBuildDir(String path) {
        File dir = null;
        URL url = getResourceUrl(path);
        if ("file".equals(url.getProtocol())) {
            int num = path.split("/").length;
            dir = new File(getFileName(url));
            for (int i = 0; i < num;) {
                i++;
                dir = dir.getParentFile();
            }
        } else {
            dir = new File(toJarFilePath(url));
        }
        return dir;
    }

    // ===================================================================================
    //                                                                InputStream Handling
    //                                                                ====================
    public static void makeFileAndClose(InputStream in, String outputFilename) {
        final byte[] bytes = toBytesAndClose(in);
        final File outputFile = new File(outputFilename);
        final FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(outputFile, false);
        } catch (FileNotFoundException e) {
            String msg = "new FileOutputStream(outputFile, false) threw the " + e.getClass().getSimpleName();
            msg = msg + ": outputFilename=" + outputFilename;
            throw new IllegalStateException(msg, e);
        }
        try {
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            String msg = "fileOutputStream.write(toBytes) threw the " + e.getClass().getSimpleName();
            msg = msg + ": outputFilename=" + outputFilename;
            throw new IllegalStateException(msg, e);
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static final byte[] toBytesAndClose(InputStream is) {
        byte[] bytes = null;
        byte[] buf = new byte[8192];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int n = 0;
            while ((n = is.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, n);
            }
            bytes = baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (is != null) {
                close(is);
            }
        }
        return bytes;
    }

    public static void close(InputStream is) {
        if (is == null) {
            return;
        }
        try {
            is.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static final void copy(InputStream is, OutputStream os) {
        byte[] buf = new byte[8192];
        try {
            int n = 0;
            while ((n = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, n);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static int available(InputStream is) {
        try {
            return is.available();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // ===================================================================================
    //                                                                   JAR File Handling
    //                                                                   =================
    public static JarFile create(String file) {
        try {
            return new JarFile(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static JarFile create(File file) {
        try {
            return new JarFile(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static InputStream getInputStream(JarFile file, ZipEntry entry) {
        try {
            return file.getInputStream(entry);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static JarFile toJarFile(URL jarUrl) {
        java.net.URLConnection con = openConnection(jarUrl);
        if (con instanceof JarURLConnection) {
            return getJarFile((JarURLConnection) con);
        } else {
            return create(new File(toJarFilePath(jarUrl)));
        }
    }

    public static String toJarFilePath(URL jarUrl) {
        URL nestedUrl = createURL(jarUrl.getPath());
        String nestedUrlPath = nestedUrl.getPath();
        int pos = nestedUrlPath.lastIndexOf('!');
        String jarFilePath = nestedUrlPath.substring(0, pos);
        File jarFile = new File(decodeURL(jarFilePath, "UTF8"));
        return getCanonicalPath(jarFile);
    }

    public static JarFile getJarFile(JarURLConnection conn) {
        try {
            return conn.getJarFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void close(JarFile jarFile) {
        try {
            jarFile.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected static void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}
