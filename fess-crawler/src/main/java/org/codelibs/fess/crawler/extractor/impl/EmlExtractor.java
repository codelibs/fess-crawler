/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets a text from .eml file.
 *
 * @author shinsuke
 *
 */
public class EmlExtractor implements Extractor {
    private static final String[] DAY_OF_WEEK = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };

    private static final Logger logger = LoggerFactory
        .getLogger(EmlExtractor.class);

    protected Properties mailProperties = new Properties();

    /* (non-Javadoc)
     * @see org.codelibs.robot.extractor.Extractor#getText(java.io.InputStream, java.util.Map)
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        final Properties props = new Properties(mailProperties);
        if (params != null) {
            for (final Map.Entry<String, String> entry : params.entrySet()) {
                props.put(entry.getKey(), entry.getValue());
            }
        }
        try {
            final Session mailSession = Session.getDefaultInstance(props, null);
            final MimeMessage message = new MimeMessage(mailSession, in);
            final String content = getBodyText(message);
            final ExtractData data = new ExtractData(content != null ? content : StringUtil.EMPTY);
            final Enumeration<Header> headers = message.getAllHeaders();
            while (headers.hasMoreElements()) {
                final Header header = headers.nextElement();
                data.putValue(header.getName(), header.getValue());
            }
            putValue(data, "Content-ID", message.getContentID());
            putValue(data, "Content-Language", message.getContentLanguage());
            putValue(data, "Content-MD5", message.getContentMD5());
            putValue(data, "Description", message.getDescription());
            putValue(data, "Disposition", message.getDisposition());
            putValue(data, "Encoding", message.getEncoding());
            putValue(data, "File-Name", message.getFileName());
            putValue(data, "From", message.getFrom());
            putValue(data, "Line-Count", message.getLineCount());
            putValue(data, "Message-ID", message.getMessageID());
            putValue(data, "Message-Number", message.getMessageNumber());
            putValue(data, "Received-Date", getReceivedDate(message));
            putValue(data, "Reply-To", message.getReplyTo());
            putValue(data, "Sender", message.getSender());
            putValue(data, "Sent-Date", message.getSentDate());
            putValue(data, "Size", message.getSize());
            putValue(data, "Subject", message.getSubject());
            putValue(data, "Receipients", message.getAllRecipients());
            putValue(data, "To", message.getRecipients(Message.RecipientType.TO));
            putValue(data, "Cc", message.getRecipients(Message.RecipientType.CC));
            putValue(data, "Bcc", message.getRecipients(Message.RecipientType.BCC));
            return data;
        } catch (final MessagingException e) {
            throw new ExtractException(e);
        }
    }

    /**
     * @param data
     * @param string
     * @param contentID
     */
    private void putValue(final ExtractData data, final String key, final Object value) {
        try {
            if (value instanceof String) {
                if ("Subject".equals(key)) {
                    data.putValue(key, getDecodeText(value.toString()));
                } else {
                    data.putValue(key, value.toString());
                }
            } else if (value instanceof String[]) {
                data.putValues(key, (String[]) value);
            } else if (value instanceof Integer) {
                data.putValue(key, ((Integer) value).toString());
            } else if (value instanceof Address[]) {
                final int size = ((Address[]) value).length;
                final String[] values = new String[size];
                for (int i = 0; i < size; i++) {
                    final Address address = ((Address[]) value)[i];
                    values[i] = getDecodeText(address.toString());
                }
                data.putValues(key, values);
            } else if (value instanceof Date) {
                data.putValue(key, new SimpleDateFormat(Constants.ISO_DATETIME_FORMAT).format(value));
            } else if (value != null) {
                data.putValue(key, value.toString());
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to put " + key + ":" + value, e);
            }
        }
    }

    String getDecodeText(final String value) {
        if (value == null) {
            return StringUtil.EMPTY;
        }
        try {
            return MimeUtility.decodeText(value);
        } catch (final UnsupportedEncodingException e) {
            logger.warn("Invalid encoding.", e);
            return StringUtil.EMPTY;
        }
    }

    public Properties getMailProperties() {
        return mailProperties;
    }

    public void setMailProperties(final Properties mailProperties) {
        this.mailProperties = mailProperties;
    }

    private String getBodyText(final MimeMessage message) {
        String result = null;
        try {
            final Object content = message.getContent();
            if (content instanceof Multipart) {
                BodyPart textPlain = null;
                BodyPart textHtml = null;
                final Multipart multipart = (Multipart) content;
                final int count = multipart.getCount();
                for (int i = 0; i < count; i++) {
                    final BodyPart bodyPart = multipart.getBodyPart(i);
                    final String disposition = bodyPart.getDisposition();
                    if (disposition != null
                        && (disposition.equalsIgnoreCase("ATTACHMENT"))) {
                        // TODO: ignore attachments
                    } else {
                        if (bodyPart.isMimeType("text/plain")) {
                            textPlain = bodyPart;
                            break;
                        } else if (bodyPart.isMimeType("text/html")) {
                            textHtml = bodyPart;
                        }
                    }
                }
                if (textPlain != null) {
                    result = (String) textPlain.getContent();
                } else if (textHtml != null) {
                    result = (String) textHtml.getContent();
                }
            } else if (content instanceof String) {
                result = (String) content;
            }
        } catch (MessagingException | IOException e) {
            throw new ExtractException(e);
        }
        return result;
    }

    private static Date getReceivedDate(final javax.mail.Message message)
            throws MessagingException {
        final Date today = new Date();
        final String[] received = message.getHeader("received");
        if (received != null) {
            for (final String v : received) {
                String dateStr = null;
                try {
                    dateStr = getDateString(v);
                    final Date receivedDate =
                        new MailDateFormat().parse(dateStr);
                    if (!receivedDate.after(today)) {
                        return receivedDate;
                    }
                } catch (final ParseException e) {
                    // ignore
                }
            }
        }
        return null;
    }

    private static String getDateString(final String text) {
        for (final String dow : DAY_OF_WEEK) {
            final int i = text.lastIndexOf(dow);
            if (i != -1) {
                return text.substring(i);
            }
        }
        return null;
    }
}
