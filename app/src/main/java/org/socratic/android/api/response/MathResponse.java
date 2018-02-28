package org.socratic.android.api.response;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.CardResponse;
import org.socratic.android.api.model.math.MathResults;
import org.socratic.android.util.MultiLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by byfieldj on 8/29/17.
 *
 * This class will wrap the entire JSON response for a given math formula, it's dependencies are
 * Serializable subclasses that are need to properly marshall individual math steps into POJOs.
 */

public class MathResponse extends SocraticBaseResponse{

    @SerializedName("results")
    ArrayList<CardResponse> results;


    public boolean hasResults(){

        return !results.isEmpty() && results.size() > 0;
    }

    public ArrayList<CardResponse> getResults(){
        return results;
    }










}
