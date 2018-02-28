package org.socratic.android.api;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class AllMessagesGetRequest extends SocraticBaseRequest {
    private int channelID;

    public AllMessagesGetRequest(int personID, int channelID, String before, String after) {
        this.channelID = channelID;

        putParam("before", before);
        putParam("after", after);
        putParam("person_id", String.valueOf(personID));
    }

    @Override
    public String getPath() { return String.format("/channel/%s/messages", this.channelID); }

    @Override
    public String getHttpMethod() { return BaseRequest.GET; }

}


