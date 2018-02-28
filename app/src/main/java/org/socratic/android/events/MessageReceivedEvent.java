package org.socratic.android.events;

import org.socratic.android.api.model.Channel;
import org.socratic.android.api.model.Message;
import org.socratic.android.api.model.Person;

/**
 * Created by jessicaweinberg on 10/5/17.
 */

public class MessageReceivedEvent {
    private Message message;
    private Person person;
    private Channel channel;

    public MessageReceivedEvent(Message message, Person person, Channel channel) {
        this.message = message;
        this.person = person;
        this.channel = channel;
    }

    public Message getMessage() {return message;}
    public Person getPerson() {return person;}
    public Channel getChannel() {return channel;}
}
