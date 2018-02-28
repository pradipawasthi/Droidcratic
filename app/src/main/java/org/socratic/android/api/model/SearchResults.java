package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.response.SocraticBaseResponse;
import org.socratic.android.api.response.SearchResponse;

import java.util.ArrayList;

/**
 * @date 2017-02-06
 *
 * Used as both a sub-model for OcrTextResponse as well as a standalone model for Text Search
 */
public class SearchResults extends SocraticBaseResponse implements SearchResponse{

    private ArrayList<CardResponse> results;

    private MetaData metadata;

    @SerializedName("turnon_appsee")
    private boolean isAppseeOn;

    private String query;

    @SerializedName("query_type")
    private String queryType;

    @SerializedName("web_search_id")
    private long webSearchId;

    public ArrayList<CardResponse> getResults() {
        return results;
    }

    public void setResults(ArrayList<CardResponse> results) {
        this.results = results;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(MetaData metadata) {
        this.metadata = metadata;
    }

    public boolean isAppseeOn() {
        return isAppseeOn;
    }

    public void setAppseeOn(boolean appseeOn) {
        isAppseeOn = appseeOn;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public long getWebSearchId() {
        return webSearchId;
    }

    public void setWebSearchId(long webSearchId) {
        this.webSearchId = webSearchId;
    }

    public boolean hasResults() {
        return results != null &&
                !results.isEmpty();
    }

    @Override
    public String getQuestionText() {
        return getQuery();
    }

    @Override
    public String getQuestionQueryType() {
        return getQueryType();
    }

    @Override
    public ArrayList<CardResponse> getCardResults() {
        if (hasResults()) {
            return results;
        }

        return null;
    }
}