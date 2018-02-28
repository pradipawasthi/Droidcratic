package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import org.joda.time.DateTime;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class Message implements IMessage, MessageContentType.Image {

    @SerializedName("person_id")
    private String personID;

    @SerializedName("channel_id")
    private String channelID;

    @SerializedName("content_type")
    private String contentType;

    @SerializedName("content")
    private String content;

    @SerializedName("state")
    private String state;

    @SerializedName("id")
    private int id;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    private Date createdAtDate;
    private Author author;

    public String getPersonID() { return personID; }
    public String getChannelID() { return channelID; }
    public String getContentType() { return contentType; }
    public String getState() { return state; }
    public String getUpdatedAt() { return updatedAt; }
    public String getCreatedAtString() { return createdAt; }

    public void setCreatedAtDate(Date date) {
        this.createdAtDate = date;
    }
    public void setText(String text) {
        this.content = text;
    }
    public void setAuthor(Author author) {
        this.author = author;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setContentType(String contentType) { this.contentType = contentType; }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public String getText() {
        return content;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        if (this.createdAtDate != null) {
            return this.createdAtDate;
        } else {
            java.util.Date date = new DateTime(this.createdAt).toDate();
            Date data = new java.sql.Date(date.getTime());
            return data;
        }
    }

    @Override
    public String getImageUrl() {
        if (this.contentType != null) {
            if (this.contentType.equalsIgnoreCase("image")) {
                return this.content;
            }
        }
            return null;
    }
}
