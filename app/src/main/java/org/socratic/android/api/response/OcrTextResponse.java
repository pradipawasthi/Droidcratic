package org.socratic.android.api.response;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.CardResponse;
import org.socratic.android.api.model.SearchResults;

import java.util.ArrayList;

/**
 * @date 2017-02-03
 */
public class OcrTextResponse extends SocraticBaseResponse implements SearchResponse {

    public static final String QUERY_TYPE_LATEX = "latex";

    @SerializedName("query_type")
    private String queryType;

    private String text;
    private String urlEncodedText;

    @SerializedName("search_results")
    private SearchResults searchResults;


    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUrlEncodedText(String encodedText){
        this.urlEncodedText = encodedText;
    }

    public String getUrlEncodedText(){
        return this.urlEncodedText;
    }

    public SearchResults getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults) {
        this.searchResults = searchResults;
    }

    public boolean hasResults() {
        return searchResults != null &&
                searchResults.getResults() != null &&
                !searchResults.getResults().isEmpty();
    }

    @Override
    public String getQuestionText() {
        return getText();
    }

    @Override
    public String getQuestionQueryType() {
        return getQueryType();
    }

    @Override
    public ArrayList<CardResponse> getCardResults() {
        if (hasResults()) {
            return searchResults.getResults();
        }

        return null;
    }
}