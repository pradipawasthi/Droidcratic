package org.socratic.android.events;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class AllMessagesEvent {
    public static final int CONTEXT_STARTING = 0;
    public static final int CONTEXT_FINISHED = 1;

    private int context;

    public  AllMessagesEvent(int context) {
        this.context = context;
    }

    public int getContext() {
        return context;
    }
}

