package org.socratic.android.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.socratic.android.R;
import org.socratic.android.activities.CameraActivity;
import org.socratic.android.activities.ChatDetailActivity;
import org.socratic.android.contract.ChatContract;
import org.socratic.android.adapter.ConnectedContactsAdapter;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.analytics.ChatAnalytics;
import org.socratic.android.api.model.Chat;
import org.socratic.android.api.model.UserContact;
import org.socratic.android.api.response.ChatListResponse;
import org.socratic.android.api.response.NewChatResponse;
import org.socratic.android.databinding.FragmentChatBinding;
import org.socratic.android.events.ChatListEvent;
import org.socratic.android.events.MessageReceivedEvent;
import org.socratic.android.events.NewChatEvent;
import org.socratic.android.globals.ChatListManager;
import org.socratic.android.storage.DiskStorage;
import org.socratic.android.util.ContactsUtil;
import org.socratic.android.util.Util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class ChatFragment extends BaseFragment<FragmentChatBinding, ChatContract.ViewModel>
    implements ChatContract.View {

    private static final String TAG = ChatFragment.class.getSimpleName();

    @Inject ChatListManager chatListManager;
    @Inject SharedPreferences sharedPreferences;
    @Inject AnalyticsManager analyticsManager;
    @Inject DiskStorage diskStorage;

    private ConnectedContactsAdapter connectedContactsAdapter;
    private List<Chat> chats;
    private boolean preparedContacts;
    private int personID;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        preparedContacts = sharedPreferences.getBoolean("prepared_contacts", false);

        boolean contactsLoadedOnboarding = sharedPreferences.getBoolean("contacts_loaded_onboarding", false);

        List<UserContact> contacts = ContactsUtil.fetchContacts(getActivity());

        if (!preparedContacts && !contactsLoadedOnboarding) {
            uploadContacts(contacts, null);

            sharedPreferences.edit().putBoolean("prepared_contacts", true).apply();
        } else {
            updateContacts(contacts);
            viewModel.getChatList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_chat);

        chats = new ArrayList<>();
        connectedContactsAdapter = new ConnectedContactsAdapter(getContext(), chats);

        connectedContactsAdapter.setOnItemClickListener(new ConnectedContactsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Chat chat = chats.get(position);

                final UserContact userContact = new UserContact(chat.getPersonID(), chat.getName());
                userContact.addPhoneNumber(0, chat.getPhoneNumberNational());

                if (chat.getChannelID() == 0) {
                    viewModel.createChat(userContact);
                } else {
                    Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                    intent.putExtra("channel_id", chat.getChannelID());
                    intent.putExtra("chat_name", chat.getName());
                    intent.putExtra("chat_state", chat.getState());
                    startActivity(intent);
                }
            }
        });

        binding.connectedContactsList.setAdapter(connectedContactsAdapter);
        binding.connectedContactsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.connectedContactsList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        binding.chatBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                CameraActivity activity = (CameraActivity) getActivity();
                activity.returnToCameraScreen();
            }
        });

        return rootView;
    }

    public void fireChatListAnalytics() {
        ChatAnalytics.ChatListSeen chatListSeen = new ChatAnalytics.ChatListSeen();
        chatListSeen.numConnectedContacts = chats.size();
        analyticsManager.track(chatListSeen);
    }

    //compare current contacts list with old contacts list for any change in contacts
    //upload new contacts if necessary
    private void updateContacts(List<UserContact> contacts) {
        String currentContacts = null;
        try {
            currentContacts = diskStorage.loadContactsList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String newContacts = ContactsUtil.getContactIDs(contacts);

        if (contacts != null) {
            if (ContactsUtil.hasNewContacts(currentContacts, newContacts)) {
                uploadContacts(contacts, newContacts);
            }
        }
    }

    private void uploadContacts(List<UserContact> contacts, String contactIDs) {
        sharedPreferences.edit().putInt("numContacts", contacts.size()).apply();

        JSONArray contactsJSON = new JSONArray();
        for (UserContact contact : contacts) {
            contactsJSON.put(contact.toJSON());
        }

        String deviceName = Util.getUserDeviceName(getActivity());

        if (deviceName == null) {
            deviceName = "";
        }

        viewModel.sendContacts(contactsJSON, deviceName);

        try {
            if (contactIDs != null) {
                diskStorage.saveContactsList(contactIDs);
            } else {
                diskStorage.saveContactsList(ContactsUtil.getContactIDs(contacts));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupChatList() {
        viewModel.getChatList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChatListEvent event) {
        switch (event.getContext()) {
            case ChatListEvent.CONTEXT_FINISHED:
                ChatListResponse response = chatListManager.getChatListResponse();
                if (response != null) {
                    connectedContactsAdapter.updateChatList(response);

                    if (connectedContactsAdapter.getItemCount() == 0) {
                        binding.emptyState.setVisibility(View.VISIBLE);
                        binding.connectedContactsList.setVisibility(View.GONE);
                    }
                }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageReceivedEvent event) {
        viewModel.getChatList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewChatEvent event) {
        switch (event.getContext()) {
            case NewChatEvent.CONTEXT_FINISHED:
                NewChatResponse response = chatListManager.getNewChatResponse();
                if (response != null) {
                    Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                    intent.putExtra("channel_id", response.getChannel().getChannelID());
                    intent.putExtra("chat_state", "empty");
                    intent.putExtra("chat_name", event.getChatName());
                    getActivity().startActivity(intent);
                }
        }
    }
}
