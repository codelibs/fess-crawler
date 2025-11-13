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
package org.codelibs.fess.crawler.entity;

import java.io.Serializable;

/**
 * Represents a video entry within a sitemap URL.
 * This class encapsulates the properties of a video as defined in the Video Sitemap extension.
 *
 * <p>
 * The video extension allows you to provide additional information about videos on your pages.
 * This can help Google index your videos and display them in Google Video search results.
 * </p>
 *
 * @see <a href="https://developers.google.com/search/docs/crawling-indexing/sitemaps/video-sitemaps">Video Sitemaps</a>
 */
public class SitemapVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * A URL pointing to the video thumbnail image file.
     * Images must be at least 160x90 pixels and at most 1920x1080 pixels.
     */
    private String thumbnailLoc;

    /**
     * The title of the video.
     * Maximum 100 characters.
     */
    private String title;

    /**
     * The description of the video.
     * Maximum 2048 characters.
     */
    private String description;

    /**
     * A URL pointing to the actual video media file.
     * Must be one of the supported formats.
     */
    private String contentLoc;

    /**
     * A URL pointing to a player for a specific video.
     * Usually this is the information in the src element of an &lt;embed&gt; tag.
     */
    private String playerLoc;

    /**
     * The duration of the video in seconds.
     * Value must be from 1 to 28800 (8 hours).
     */
    private String duration;

    /**
     * The date the video was first published, in W3C format.
     */
    private String publicationDate;

    /**
     * An optional arbitrary string tag describing the video.
     * Maximum 32 characters.
     */
    private String category;

    /**
     * Indicates whether the video is suitable for family viewing.
     * If omitted, none is assumed.
     */
    private String familyFriendly;

    /**
     * A space-delimited list of countries where the video may or may not be played.
     */
    private String restriction;

    /**
     * The price to download or view the video.
     */
    private String price;

    /**
     * Indicates whether a subscription is required to view the video.
     */
    private String requiresSubscription;

    /**
     * The video uploader's name.
     * Maximum 255 characters.
     */
    private String uploader;

    /**
     * A space-delimited list of platforms where the video may or may not be played.
     */
    private String platform;

    /**
     * Indicates whether the video is a live stream.
     */
    private String live;

    /**
     * Creates a new SitemapVideo instance.
     */
    public SitemapVideo() {
        // Default constructor
    }

    /**
     * Gets the thumbnail location URL.
     * @return the thumbnail URL
     */
    public String getThumbnailLoc() {
        return thumbnailLoc;
    }

    /**
     * Sets the thumbnail location URL.
     * @param thumbnailLoc the thumbnail URL to set
     */
    public void setThumbnailLoc(final String thumbnailLoc) {
        this.thumbnailLoc = thumbnailLoc;
    }

    /**
     * Gets the title of the video.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the video.
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the description of the video.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the video.
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the content location URL.
     * @return the content URL
     */
    public String getContentLoc() {
        return contentLoc;
    }

    /**
     * Sets the content location URL.
     * @param contentLoc the content URL to set
     */
    public void setContentLoc(final String contentLoc) {
        this.contentLoc = contentLoc;
    }

    /**
     * Gets the player location URL.
     * @return the player URL
     */
    public String getPlayerLoc() {
        return playerLoc;
    }

    /**
     * Sets the player location URL.
     * @param playerLoc the player URL to set
     */
    public void setPlayerLoc(final String playerLoc) {
        this.playerLoc = playerLoc;
    }

    /**
     * Gets the duration of the video in seconds.
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the video in seconds.
     * @param duration the duration to set
     */
    public void setDuration(final String duration) {
        this.duration = duration;
    }

    /**
     * Gets the publication date.
     * @return the publication date
     */
    public String getPublicationDate() {
        return publicationDate;
    }

    /**
     * Sets the publication date.
     * @param publicationDate the publication date to set
     */
    public void setPublicationDate(final String publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * Gets the category of the video.
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the video.
     * @param category the category to set
     */
    public void setCategory(final String category) {
        this.category = category;
    }

    /**
     * Gets the family friendly flag.
     * @return the family friendly value
     */
    public String getFamilyFriendly() {
        return familyFriendly;
    }

    /**
     * Sets the family friendly flag.
     * @param familyFriendly the family friendly value to set
     */
    public void setFamilyFriendly(final String familyFriendly) {
        this.familyFriendly = familyFriendly;
    }

    /**
     * Gets the restriction information.
     * @return the restriction
     */
    public String getRestriction() {
        return restriction;
    }

    /**
     * Sets the restriction information.
     * @param restriction the restriction to set
     */
    public void setRestriction(final String restriction) {
        this.restriction = restriction;
    }

    /**
     * Gets the price information.
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * Sets the price information.
     * @param price the price to set
     */
    public void setPrice(final String price) {
        this.price = price;
    }

    /**
     * Gets the requires subscription flag.
     * @return the requires subscription value
     */
    public String getRequiresSubscription() {
        return requiresSubscription;
    }

    /**
     * Sets the requires subscription flag.
     * @param requiresSubscription the requires subscription value to set
     */
    public void setRequiresSubscription(final String requiresSubscription) {
        this.requiresSubscription = requiresSubscription;
    }

    /**
     * Gets the uploader name.
     * @return the uploader
     */
    public String getUploader() {
        return uploader;
    }

    /**
     * Sets the uploader name.
     * @param uploader the uploader to set
     */
    public void setUploader(final String uploader) {
        this.uploader = uploader;
    }

    /**
     * Gets the platform information.
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Sets the platform information.
     * @param platform the platform to set
     */
    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    /**
     * Gets the live streaming flag.
     * @return the live value
     */
    public String getLive() {
        return live;
    }

    /**
     * Sets the live streaming flag.
     * @param live the live value to set
     */
    public void setLive(final String live) {
        this.live = live;
    }

    @Override
    public String toString() {
        return "SitemapVideo [thumbnailLoc=" + thumbnailLoc + ", title=" + title + ", description=" + description + ", contentLoc="
                + contentLoc + ", playerLoc=" + playerLoc + ", duration=" + duration + ", publicationDate=" + publicationDate
                + ", category=" + category + "]";
    }
}
