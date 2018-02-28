package org.socratic.android.events;

import org.socratic.android.api.model.Message;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class NewMessageEvent {
    public static final int CONTEXT_STARTING = 0;
    public static final int CONTEXT_FINISHED = 1;

    private int context;
    private Message message;

    public  NewMessageEvent(int context, Message message) {
        this.context = context;
        this.message = message;
    }

    public int getContext() {
        return context;
    }

    public Message getMessage() { return message; }
}
