package org.socratic.android.api.model;

import java.sql.Date;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public interface Chat {
    String getPhoneNumberNational();

    String getName();

    int getChannelID();

    int getPersonID();

    String getState();

    Date getLastMessageSentAt();

    String getIsGroupChat();
}
