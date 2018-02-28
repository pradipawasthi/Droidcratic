package org.socratic.android.api.response;

import org.socratic.android.api.model.CardResponse;

import java.util.ArrayList;

/**
 * Created by williamxu on 6/26/17.
 */

public interface SearchResponse {

    public String getQuestionText();

    public String getQuestionQueryType();

    public ArrayList<CardResponse> getCardResults();
}
