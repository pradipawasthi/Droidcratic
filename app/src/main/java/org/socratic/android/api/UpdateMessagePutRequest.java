package org.socratic.android.api;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class UpdateMessagePutRequest extends SocraticBaseRequest {


    public UpdateMessagePutRequest(int personID, int channelID) {
        putParam("person_id", String.valueOf(personID));
        putParam("channel_id", String.valueOf(channelID));
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.PUT;
    }

    @Override
    public String getPath() {
        return "/messages_read";
    }
}