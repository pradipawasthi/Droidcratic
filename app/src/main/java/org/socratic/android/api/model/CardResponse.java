package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.math.MathData;

import java.util.ArrayList;

/**
 * @date 2017-02-06
 */
public class CardResponse {

    @SerializedName("display_url")
    private String displayUrl;

    private ArrayList<ScriptResponseCard> scripts;

    @SerializedName("source")
    private String source;

    private String title;

    private String type;

    private String url;

    private HeaderCard header;

    private BodyCard body;

    @SerializedName("data")
    private NativeDataCard data;

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public ArrayList<ScriptResponseCard> getScripts() {
        return scripts;
    }

    public void setScripts(ArrayList<ScriptResponseCard> scripts) {
        this.scripts = scripts;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HeaderCard getHeader() {
        return header;
    }

    public void setHeader(HeaderCard header) {
        this.header = header;
    }

    public BodyCard getBody() {
        return body;
    }

    public void setBody(BodyCard body) {
        this.body = body;
    }

    public NativeDataCard getData() {
        return this.data;
    }

    public void setData(NativeDataCard data) {
        this.data = data;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {

        return this.source;
    }


}