/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.git;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holder for Git authentication information.
 * This class manages multiple Git authentication credentials and matches them to repository URLs.
 *
 * @author shinsuke
 */
public class GitAuthenticationHolder {

    /** List of Git authentications. */
    protected List<GitAuthentication> gitAuthenticationList = new ArrayList<>();

    /**
     * Creates a new GitAuthenticationHolder instance.
     */
    public GitAuthenticationHolder() {
        // Default constructor
    }

    /**
     * Adds a Git authentication to the holder.
     *
     * @param gitAuthentication The Git authentication to add.
     */
    public void add(final GitAuthentication gitAuthentication) {
        gitAuthenticationList.add(gitAuthentication);
    }

    /**
     * Gets the Git authentication that matches the given URL.
     *
     * @param url The URL to match.
     * @return The matching Git authentication, or null if no match is found.
     */
    public GitAuthentication get(final String url) {
        if (url == null) {
            return null;
        }

        for (final GitAuthentication gitAuthentication : gitAuthenticationList) {
            final Pattern pattern = gitAuthentication.getServerPattern();
            if (pattern != null) {
                final Matcher matcher = pattern.matcher(url);
                if (matcher.matches()) {
                    return gitAuthentication;
                }
            }
        }
        return null;
    }
}
