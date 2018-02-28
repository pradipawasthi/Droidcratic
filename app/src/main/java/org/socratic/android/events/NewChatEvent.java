package org.socratic.android.events;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class NewChatEvent {
    public static final int CONTEXT_STARTING = 0;
    public static final int CONTEXT_FINISHED = 1;

    private int context;
    private String chatName;

    public  NewChatEvent(int context, String name) {
        this.context = context;
        this.chatName = name;
    }

    public int getContext() {
        return context;
    }
    public String getChatName() {
        return chatName;
    }
}
