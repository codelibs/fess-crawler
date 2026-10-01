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
package org.codelibs.fess.crawler.helper;

/**
 * How a crawler client applies robots.txt.
 *
 * @param useAllows whether Allow rules are honoured; when false, only Disallow rules apply
 * @param useDisallows whether Disallow rules are honoured; when false, everything is allowed
 * @param allowOnUnavailable whether an unavailable robots.txt (429, 5xx, network error) is treated as allow-all at once
 * @param maxRetries the number of retries after the first failed robots.txt fetch; when they fail as well, the origin is
 *            treated as disallow-all
 */
public record RobotsTxtPolicy(boolean useAllows, boolean useDisallows, boolean allowOnUnavailable, int maxRetries) {
}
