package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by byfieldj on 10/3/17.
 */

public class Definitions {


    ArrayList<Definition> definitions;

    @SerializedName("sources")
    String[] sources;

    public void setDefinitionsList(ArrayList<Definition> definitions){
        this.definitions = definitions;
    }

    public ArrayList<Definition> getDefinitionsList(){
        return this.definitions;
    }

    public void setSources(String[] sources){
        this.sources = sources;
    }

    public String[] getSources(){
        return this.sources;
    }
}
