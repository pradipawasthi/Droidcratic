package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by byfieldj on 10/6/17.
 */

public class Explainer {

    @SerializedName("content")
    private String content;


    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return this.content;
    }
}
