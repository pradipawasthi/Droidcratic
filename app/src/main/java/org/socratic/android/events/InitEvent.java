package org.socratic.android.events;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class InitEvent {
    public static final int CONTEXT_STARTING = 0;
    public static final int CONTEXT_FINISHED = 1;

    private int context;

    public InitEvent(int context) {
        this.context = context;
    }

    public int getContext() {
        return context;
    }
}
