package org.socratic.android.adapter;

import android.content.Context;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.socratic.android.R;
import org.socratic.android.SocraticApp;
import org.socratic.android.api.model.ChannelInfo;
import org.socratic.android.api.model.Chat;
import org.socratic.android.api.model.Person;
import org.socratic.android.api.response.ChatListResponse;
import org.socratic.android.globals.ChatListManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by jessicaweinberg on 9/27/17.
 */

public class ConnectedContactsAdapter extends RecyclerView.Adapter<ConnectedContactsAdapter.ViewHolder> {
    @Inject ChatListManager chatListManager;
    @Inject SharedPreferences sharedPreferences;

    private List<Chat> allChats;
    private Context context;
    private OnItemClickListener listener;

    public ConnectedContactsAdapter(Context context, List<Chat> allChats) {
        this.context = context;
        this.allChats = allChats;

        SocraticApp.getViewHolderComponent(context).inject(this);
    }

    public void updateChatList(ChatListResponse response) {

        int previousSize = allChats.size();

        if (allChats != null && !allChats.isEmpty()) {
            allChats.clear();
        }

        List<Chat> chats = new ArrayList<>();

        chats.clear();

        //make sure no group chats are added
        if (response != null) {
            if (response.getChannels() != null) {
                for (ChannelInfo channelInfo : response.getChannels()) {
                    if (channelInfo.getChannel().getIsTwoPerson()) {
                        chats.add(channelInfo);
                    }
                }
            }
        }

        //commented out code that includes group chats
//        chats.addAll(response.getChannels());

        HashSet<Integer> channelSet = new HashSet<>();

        if (response != null) {
            if (response.getChannels() != null) {
                for (ChannelInfo channel : response.getChannels()) {
                    channelSet.add(channel.getPersonID());
                }
            }
        }

        // only add person object if they do not have a channel already
        if (channelSet != null && response.getPersons() != null) {
            for (Person person: response.getPersons()) {
                if (!channelSet.contains(person.getPersonID())) {
                    chats.add(person);
                }
            }
        }

        List<Chat> sortedChats = sortChats(chats);
        allChats.addAll(sortedChats);
        notifyItemRangeRemoved(0, previousSize);
        notifyItemRangeInserted(0, sortedChats.size() - 1);
    }

    private List<Chat> sortChats(List<Chat> chats) {
        //First sort by most recent message exchanged and after that sort alphabetically by first name

        List<Chat> sortedChats = new ArrayList<>();

        if (chats != null) {

            Iterator<Chat> iter = chats.iterator();
            while (iter.hasNext()) {
                Chat chat = iter.next();

                if (chat.getLastMessageSentAt() != null) {
                    sortedChats.add(chat);
                    iter.remove();
                }
            }


            Collections.sort(sortedChats, new Comparator<Chat>() {
                public int compare(Chat chat1, Chat chat2) {
                    return chat1.getLastMessageSentAt().compareTo(chat2.getLastMessageSentAt());
                }
            });

            Collections.reverse(sortedChats);

            Collections.sort(chats, new Comparator<Chat>() {
                @Override
                public int compare(Chat chat1, Chat chat2) {
                    String s1 = chat1.getName();
                    String s2 = chat2.getName();
                    return s1.compareToIgnoreCase(s2);
                }

            });

            sortedChats.addAll(chats);

        }
        return sortedChats;
    }

    //inflate layout from xml and set in view holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View contactView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_connected_contact, parent, false);

        return new ViewHolder(contactView);
    }

    //populate data into list item through view holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data item for this position
        final Chat chat = allChats.get(position);

        ImageView icon = holder.connectedContactIcon;
        TextView name = holder.connectedContactText;
        ImageView groupIcon = holder.groupChatIcon;

        if (!chat.getState().isEmpty()) {
            if (chat.getState().equalsIgnoreCase("empty")) {
                icon.setImageResource(R.drawable.status_none);
            } else if (chat.getState().equalsIgnoreCase("sent_not_read")) {
                icon.setImageResource(R.drawable.status_sent);
            } else if (chat.getState().equalsIgnoreCase("sent_read")) {
                icon.setImageResource(R.drawable.status_sent_read);
            } else if (chat.getState().equalsIgnoreCase("received_not_read")) {
                icon.setImageResource(R.drawable.status_unread);
            } else if (chat.getState().equalsIgnoreCase("received_read")) {
                icon.setImageResource(R.drawable.status_read);
            }
        }

        if (!chat.getIsGroupChat().isEmpty()) {
            if (chat.getIsGroupChat().equals("true")) {
                groupIcon.setVisibility(View.VISIBLE);
            }
        }

        name.setText(chat.getName());
    }

    //set count of items in list
    @Override
    public int getItemCount() {
        return allChats.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    //View Holder caches views within the item layout for efficiency
    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView connectedContactIcon;
        private TextView connectedContactText;
        private ImageView groupChatIcon;

        //constructor to take in list item view and look up sub views
        public ViewHolder(final View itemView) {
            super(itemView);

            //TODO: find way to inject views with data binding
            this.connectedContactIcon = (ImageView) itemView.findViewById(R.id.connected_contact_icon);
            this.connectedContactText = (TextView) itemView.findViewById(R.id.connected_contact_name);
            this.groupChatIcon = (ImageView) itemView.findViewById(R.id.group_chat_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }
    }
}
