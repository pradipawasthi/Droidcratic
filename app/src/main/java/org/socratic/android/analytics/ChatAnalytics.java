package org.socratic.android.analytics;

/**
 * Created by williamxu on 10/10/17.
 */

public class ChatAnalytics {

    public static class ChatListSeen extends Event {
        public int numConnectedContacts;
        public String getEventName() { return "chatListSeen"; }
    }

    public static class ChatSpaceOpened extends Event {
        public int numMessages;
        public String messageType;
        public String getEventName() { return "chatSpaceOpened"; }
    }

    public static class TextChatSent extends Event {
        public String messageText;
        public String getEventName() { return "textChatSent"; }
    }

    public static class PhotoChatSent extends Event {
        public String getEventName() { return "photoChatSent"; }
    }

}
