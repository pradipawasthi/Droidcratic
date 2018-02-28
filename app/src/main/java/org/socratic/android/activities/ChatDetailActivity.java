package org.socratic.android.activities;

/**
 * Created by jessicaweinberg on 10/11/17.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.appsee.Appsee;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.socratic.android.R;
import org.socratic.android.contract.ChatDetailContract;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.analytics.ChatAnalytics;
import org.socratic.android.api.model.Author;
import org.socratic.android.api.model.Channel;
import org.socratic.android.api.model.Message;
import org.socratic.android.api.model.Person;
import org.socratic.android.api.response.AllMessagesResponse;
import org.socratic.android.databinding.ActivityChatDetailBinding;
import org.socratic.android.events.AllMessagesEvent;
import org.socratic.android.events.MessageReceivedEvent;
import org.socratic.android.events.NewMessageEvent;
import org.socratic.android.globals.MessagesManager;
import org.socratic.android.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;


public class ChatDetailActivity extends BaseActivity<ActivityChatDetailBinding, ChatDetailContract.ViewModel>
        implements ChatDetailContract.View {

    private static final String TAG = ChatDetailActivity.class.getSimpleName();

    @Inject MessagesManager messagesManager;
    @Inject SharedPreferences sharedPreferences;
    @Inject AnalyticsManager analyticsManager;

    public static final int GET_FROM_GALLERY = 3;

    private MessagesListAdapter messagesListAdapter;
    private int personID;
    private int channelID;
    private String chatName;
    private String chatState;
    private String lastMessageTime;
    private boolean hasEarlierMessages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setRegisterEventBus();

        personID = sharedPreferences.getInt("person_id", 0);

        Intent intent = getIntent();
        Bundle intentData = intent.getExtras();
        if (intentData != null) {
            String messageData = intentData.getString("message", "");
            if (!messageData.isEmpty()) {
                Gson gson = new Gson();
                String personData = intentData.getString("person", "");
                if (!personData.isEmpty()) {
                    Person person = gson.fromJson(personData, Person.class);
                    chatName = person.getDisplayName();
                }
                String channelData = intentData.getString("channel", "");
                if (!channelData.isEmpty()) {
                    Channel channel = gson.fromJson(channelData, Channel.class);
                    channelID = channel.getChannelID();
                }
            } else {
                chatName = intentData.getString("chat_name", "");
                channelID = intentData.getInt("channel_id", 0);
                chatState = intentData.getString("chat_state", "");
            }
        }

        setAndBindContentView(savedInstanceState, R.layout.activity_chat_detail);

        if (channelID != 0) {
            if (chatState != null) {
                if (!chatState.equalsIgnoreCase("empty")) {
                    viewModel.getAllMessages(channelID);
                } else {
                    binding.chateStateCopyText.setVisibility(View.VISIBLE);
                }
            }
        }

        Util.hideStatusBar(this);

        final ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Glide.with(ChatDetailActivity.this)
                        .load(url)
                        .override(700, 500)
                        .centerCrop()
                        .into(imageView);
            }
        };

        messagesListAdapter = new MessagesListAdapter<>(String.valueOf(personID), imageLoader);
        binding.messagesList.setAdapter(messagesListAdapter);

        binding.chatNameText.setText(this.chatName);

        final int channelID = this.channelID;

        binding.messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                viewModel.createTextMessage(channelID, input);

                if (binding.chateStateCopyText.getVisibility() == View.VISIBLE) {
                    binding.chateStateCopyText.setVisibility(View.GONE);
                }
                return true;
            }
        });

        binding.chatDetailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        messagesListAdapter.setOnMessageViewClickListener(new MessagesListAdapter.OnMessageViewClickListener() {
            @Override
            public void onMessageViewClick(View view, IMessage message) {
                Message sMessage = (Message) message;

                if (sMessage.getImageUrl() != null) {
                    final Dialog dialog = new Dialog(ChatDetailActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.image_expanded);

                    //TODO: find way to inject view through data binding
                    ImageView image = (ImageView) dialog.findViewById(R.id.expanded_image);

                    Glide.with(ChatDetailActivity.this)
                            .load(sMessage.getImageUrl())
                            .into(image);

                    dialog.setCanceledOnTouchOutside(true);

                    dialog.show();
                }
            }
        });

        binding.messageInput.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        messagesListAdapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (hasEarlierMessages) {
                    viewModel.getMoreMessages(channelID, lastMessageTime);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);

                byte[] imageData = out.toByteArray(); //Get the underlying array containing the data.

                if (binding.chateStateCopyText.getVisibility() == View.VISIBLE) {
                    binding.chateStateCopyText.setVisibility(View.GONE);
                }

                viewModel.createImageMessage(channelID, imageData, selectedImage.toString());
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    @Override
    public void addMessage(Message message) {
        //add message to messages list before network call for efficiency
        messagesListAdapter.addToStart(message, true);
        updateLastMessageTime(message.getCreatedAtString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewMessageEvent event) {
        switch (event.getContext()) {
            case NewMessageEvent.CONTEXT_FINISHED:

                Message message = event.getMessage();

                if (message.getContentType().equals("image")) {
                    analyticsManager.track(new ChatAnalytics.PhotoChatSent());

                    Appsee.addEvent("photoChatSent");
                } else {

                    ChatAnalytics.TextChatSent textChatSent = new ChatAnalytics.TextChatSent();
                    textChatSent.messageText = message.getText();
                    analyticsManager.track(textChatSent);

                    Appsee.addEvent("textChatSent");
                }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AllMessagesEvent event) {
        switch (event.getContext()) {
            case AllMessagesEvent.CONTEXT_FINISHED:
                AllMessagesResponse response = messagesManager.getAllMessagesResponse();
                if (response != null) {
                    HashMap<String, Person> personIDToPersonMap = new HashMap<>();

                    for (Person person : response.getPersons()) {
                        personIDToPersonMap.put(String.valueOf(person.getPersonID()), person);
                    }

                    for (Message message : response.getMessages()) {

                        if (personIDToPersonMap.containsKey(message.getPersonID())) {
                            Author author = new Author();
                            author.setID(message.getPersonID());
                            author.setName(personIDToPersonMap.get(message.getPersonID()).getName());
                            message.setAuthor(author);
                        }
                    }

                    if (response.getMessages() != null && !response.getMessages().isEmpty()){
                        lastMessageTime = response.getMessages().get(0).getCreatedAtString();
                        hasEarlierMessages = response.getHasEarlierMessages();
                        messagesListAdapter.addToEnd(response.getMessages(), true);
                    }

                    ChatAnalytics.ChatSpaceOpened chatSpaceOpened = new ChatAnalytics.ChatSpaceOpened();
                    chatSpaceOpened.numMessages = response.getMessages().size();
                    analyticsManager.track(chatSpaceOpened);

                    Appsee.addEvent("chatSpaceOpened");
                }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageReceivedEvent event) {
        Message message = event.getMessage();
        Person person = event.getPerson();
        Channel channel = event.getChannel();

        if (this.channelID == channel.getChannelID()) {

            Author author = new Author();
            author.setID(String.valueOf(person.getPersonID()));

            if (person.getDisplayName() != "") {
                author.setName(person.getDisplayName());
            } else {
                author.setName(person.getName());
            }

            message.setAuthor(author);
            messagesListAdapter.addToStart(message, true);

            updateLastMessageTime(message.getCreatedAtString());

            viewModel.updateMessage(person.getPersonID(), channel.getChannelID());
        }
    }

    private void updateLastMessageTime(String messageTimestamp) {
        //Update last message time if this is the first message of the chat.
        if (lastMessageTime == null) {
            lastMessageTime = messageTimestamp;
        }

        if (lastMessageTime != null) {
            if (lastMessageTime.equalsIgnoreCase("")) {
                lastMessageTime = messageTimestamp;
            }
        }
    }
}
