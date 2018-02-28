package org.socratic.android.api;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class ChatListGetRequest extends SocraticBaseRequest {
    private int personID;
    public ChatListGetRequest(int personID) { this.personID = personID; }

    @Override
    public String getPath() { return String.format("/person/%s/chats", this.personID); }

    @Override
    public String getHttpMethod() { return BaseRequest.GET; }
}