package org.socratic.android.events;

/**
 * Created by jessicaweinberg on 9/28/17.
 */

public class ChatListEvent {
    public static final int CONTEXT_STARTING = 0;
    public static final int CONTEXT_FINISHED = 1;

    private int context;

    public  ChatListEvent(int context) {
        this.context = context;
    }

    public int getContext() {
        return context;
    }
}
