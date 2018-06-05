package org.socratic.android.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.socratic.android.BuildConfig;
import org.socratic.android.R;
import org.socratic.android.SocraticApp;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.databinding.ViewInvitationsBinding;
import org.socratic.android.globals.ShareURLManager;

import java.util.Random;

import javax.inject.Inject;

/**
 * Created by jessicaweinberg
 * Updated by williamxu
 */

public class InvitationsView extends LinearLayout {

    @Inject AnalyticsManager analyticsManager;
    @Inject ShareURLManager shareURLManager;
    @Inject SharedPreferences sharedPreferences;

    ViewInvitationsBinding binding;

    SharedPreferences.Editor editor;

    RelativeLayout facebookMessenger;
    RelativeLayout copyLink;
    RelativeLayout messages;
    RelativeLayout more;

    TextView facebookMessengerText;
    TextView copyLinkText;
    TextView messagesText;
    TextView moreText;

    ImageButton settingsButton;
    TextView termsText;
    TextView privacyText;

    String facebookShareURL;
    String copyLinkShareURL;
    String messagesShareURL;
    String moreShareURL;

    String socraticURL;

    private float downX, downY, upX, upY;
    private int CLICK_ACTION_THRESHOLD = 200;

    public interface InvitationsClickListener {
        void onFacebookMessengerClick(String facebookShareURL);
        void onCopyLinkClick(String copyLinkShareURL);
        void onMessagesClick(String messagesShareURL);
        void onMoreClick(String moreShareURL);
        void onSettingsClick();
        void onTermsTextClick();
        void onPrivacyTextClick();
    }

    private InvitationsClickListener invitationsClickListener;

    public InvitationsView(Context context) {
        this(context, null);
    }

    public InvitationsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InvitationsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        SocraticApp.getViewComponent(this, context).inject(this);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = ViewInvitationsBinding.inflate(inflater, this, true);

        editor = sharedPreferences.edit();

        facebookMessenger = binding.facebookMessenger;
        copyLink = binding.copyLink;
        messages = binding.messages;
        more = binding.more;

        facebookMessengerText = binding.facebookMessengerTextView;
        copyLinkText = binding.copyLinkTextView;
        messagesText = binding.messagesTextView;
        moreText = binding.moreTextView;

        settingsButton = binding.settingsButton;
        termsText = binding.tosTextView;
        privacyText = binding.privacyPolicyTextView;

        socraticURL = BuildConfig.API_HOST;

        facebookShareURL = getShareURL("facebook");
        copyLinkShareURL = getShareURL("copy_link");
        messagesShareURL = getShareURL("messages");
        moreShareURL = getShareURL("more");

        facebookMessenger.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleFacebookMessengerClick(v, event);
                return true;
            }
        });

        copyLink.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleCopyLinkClick(v, event);
                return true;
            }
        });

        messages.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleMessagesClick(v, event);
                return true;
            }
        });

        more.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleMoreClick(v, event);
                return true;
            }
        });

        settingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationsClickListener.onSettingsClick();
            }
        });

        termsText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationsClickListener.onTermsTextClick();
            }
        });

        privacyText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationsClickListener.onPrivacyTextClick();
            }
        });
    }

    public void setInvitationsClickListener(InvitationsClickListener invitationsClickListener) {
        this.invitationsClickListener = invitationsClickListener;
    }

    private void handleFacebookMessengerClick(View v, MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN :
                downX = event.getX();
                downY = event.getY();

                facebookMessengerText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.facebookMessenger));
                break;

            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();

                if (isAClick(downX, downY, upX, upY)) {
                    invokeChannelTappedEvent("facebook", facebookShareURL);


                    AnalyticsManager.InvitationsFacebookMessageSent facebookMessageSentEvent = new AnalyticsManager.InvitationsFacebookMessageSent();
                    facebookMessageSentEvent.shareURL = facebookShareURL;
                    analyticsManager.track(facebookMessageSentEvent);

                    invitationsClickListener.onFacebookMessengerClick(facebookShareURL);
                }

                facebookMessengerText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));
                break;
        }
    }

    private void handleCopyLinkClick(View v, MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN :
                downX = event.getX();
                downY = event.getY();
                copyLinkText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.copyLink));
                break;

            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();

                if (isAClick(downX, downY, upX, upY)) {
                    invokeChannelTappedEvent("copy_link", copyLinkShareURL);

                    invitationsClickListener.onCopyLinkClick(copyLinkShareURL);
                }

                copyLinkText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));
                break;
        }
    }

    private void handleMessagesClick(View v, MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN :
                downX = event.getX();
                downY = event.getY();
                messagesText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.messages));
                break;

            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();

                if (isAClick(downX, downY, upX, upY)) {
                    invokeChannelTappedEvent("messages", messagesShareURL);

                    AnalyticsManager.InvitationsSMSSent smsSentEvent = new AnalyticsManager.InvitationsSMSSent();
                    smsSentEvent.shareURL = messagesShareURL;
                    analyticsManager.track(smsSentEvent);

                    invitationsClickListener.onMessagesClick(messagesShareURL);
                }

                messagesText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));
                break;
        }
    }

    private void handleMoreClick(View v, MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN :
                downX = event.getX();
                downY = event.getY();
                moreText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.more));
                break;

            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();

                if (isAClick(downX, downY, upX, upY)) {
                    invokeChannelTappedEvent("more", moreShareURL);

                    AnalyticsManager.InvitationsShareSheetMediumTapped shareSheetMediumTappedEvent = new AnalyticsManager.InvitationsShareSheetMediumTapped();
                    shareSheetMediumTappedEvent.shareURL = moreShareURL;
                    analyticsManager.track(shareSheetMediumTappedEvent);

                    invitationsClickListener.onMoreClick(moreShareURL);
                }

                moreText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));
                break;
        }
    }

    private boolean isAClick(float downX, float downY, float upX, float upY) {
        float differenceX = Math.abs(downX - upX);
        float differenceY = Math.abs(downY - upY);
        return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD);
    }

    protected String generateRandomString(int len) {
        String SALTCHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < len) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    protected String generateShareURL(String invitationType) {
        String code = generateRandomString(10);
        shareURLManager.postShareURL(code, invitationType);

        return socraticURL+"/a/"+code;
    }

    protected String getShareURL(String invitationType) {
        String shareURLType = String.format("%s_share_url", invitationType);
        String shareURL = sharedPreferences.getString(shareURLType, "");
        if (shareURL != "") {
            return shareURL;
        }

        shareURL = generateShareURL(invitationType);
        editor.putString(shareURLType, shareURL).apply();

        return shareURL;
    }

    protected void invokeChannelTappedEvent(String channel, String shareURL) {
        AnalyticsManager.InvitationsChannelTapped channelTappedEvent = new AnalyticsManager.InvitationsChannelTapped();
        channelTappedEvent.channel = channel;
        channelTappedEvent.shareURL = shareURL;
        analyticsManager.track(channelTappedEvent);
    }

}
