package org.socratic.android.api.response;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

import com.google.gson.annotations.SerializedName;
import org.socratic.android.api.model.AppSee;
import org.socratic.android.api.model.ForcedLogin;
import org.socratic.android.api.model.Conversations;
import org.socratic.android.api.model.InAppMessage;
import org.socratic.android.api.model.Person;

public class InitResponse {
    @SerializedName("in_app_message")
    private InAppMessage inAppMessage;

    @SerializedName("unread_message_count")
    private int unreadMessageCount;

    @SerializedName("forced_login")
    private ForcedLogin forcedLogin;

    private Conversations conversations;

    @SerializedName("person")
    private Person person;

    @SerializedName("appsee")
    private AppSee appSee;



    public int getUnreadMessageCount() { return unreadMessageCount; }
    public InAppMessage getInAppMessage() { return inAppMessage; }
    public ForcedLogin getForcedLogin() { return forcedLogin; }
    public Conversations getConversations() { return conversations; }
    public Person getPerson() { return person; }
    public AppSee getAppSee() { return appSee; }
}
