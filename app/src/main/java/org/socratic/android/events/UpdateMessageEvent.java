package org.socratic.android.events;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class UpdateMessageEvent {
    public static final int CONTEXT_STARTING = 0;
    public static final int CONTEXT_FINISHED = 1;

    private int context;

    public  UpdateMessageEvent(int context) {
        this.context = context;
    }

    public int getContext() {
        return context;
    }
}
