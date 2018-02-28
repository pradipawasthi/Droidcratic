package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by byfieldj on 10/3/17.
 */

public class Definition {

    @SerializedName("word")
    String word;

    @SerializedName("definition")
    String definition;


    public void setWord(String word){
        this.word = word;
    }

    public String getWord(){
        return this.word;
    }

    public void setDefinition(String definition){
        this.definition = definition;
    }

    public String getDefinition(){
        return this.definition;
    }
}
