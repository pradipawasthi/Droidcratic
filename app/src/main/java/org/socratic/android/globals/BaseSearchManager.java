package org.socratic.android.globals;

import org.socratic.android.api.model.CardResponse;
import org.socratic.android.api.response.MathResponse;
import org.socratic.android.api.response.SearchResponse;

/**
 * Created by williamxu on 6/26/17.
 */

public abstract class BaseSearchManager {

    public abstract SearchResponse getResponse();

    public abstract void setResponse(SearchResponse response);

    public abstract CardResponse getResponseCard(int index);

    public abstract void cancel();


}
