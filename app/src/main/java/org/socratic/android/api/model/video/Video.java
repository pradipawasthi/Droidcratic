package org.socratic.android.api.model.video;

import com.google.gson.annotations.SerializedName;

/**
 * Created by byfieldj on 9/22/17.
 */

public class Video {

    @SerializedName("id")
    String id;

    @SerializedName("url")
    String url;

    @SerializedName("title")
    String title;

    @SerializedName("channel_title")
    String channel;

    @SerializedName("description")
    String description;

    @SerializedName("thumbnail_high_url")
    String thumbnailHighUrl;

    @SerializedName("thumbnail_medium_url")
    String thumbnailMediumUrl;

    @SerializedName("thumbnail_default_url")
    String thumbnailDefaultUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailHighUrl() {
        return thumbnailHighUrl;
    }

    public void setThumbnailHighUrl(String thumbnailHighUrl) {
        this.thumbnailHighUrl = thumbnailHighUrl;
    }

    public String getThumbnailMediumUrl() {
        return thumbnailMediumUrl;
    }

    public void setThumbnailMediumUrl(String thumbnailMediumUrl) {
        this.thumbnailMediumUrl = thumbnailMediumUrl;
    }

    public String getThumbnailDefaultUrl() {
        return thumbnailDefaultUrl;
    }

    public void setThumbnailDefaultUrl(String thumbnailDefaultUrl) {
        this.thumbnailDefaultUrl = thumbnailDefaultUrl;
    }

    public String getVideoId(){
        return this.id;
    }






}
