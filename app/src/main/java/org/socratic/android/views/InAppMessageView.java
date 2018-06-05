package org.socratic.android.views;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import org.socratic.android.R;
import org.socratic.android.SocraticApp;
import org.socratic.android.activities.InAppWebViewActivity;
import org.socratic.android.databinding.ViewInAppMessageBinding;
import org.socratic.android.globals.InAppMessageManager;
import org.socratic.android.storage.DiskStorage;

import javax.inject.Inject;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class InAppMessageView extends LinearLayout {

    @Inject SharedPreferences sharedPreferences;
    @Inject InAppMessageManager inAppMessageManager;
    @Inject DiskStorage diskStorage;

    SharedPreferences.Editor editor;

    Button buttonInAppMessage;

    String messageIDStr;

    ViewInAppMessageBinding binding;

    private static final String TAG = InAppMessageView.class.getSimpleName();
    Handler mHandler;

    static final int MIN_DISTANCE = 10;
    private float downX, downY, upX, upY;

    public InAppMessageView(Context context) {
        this(context, null);
    }

    public InAppMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InAppMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        SocraticApp.getViewComponent(this, context).inject(this);
        init(context);
    }

    /**
     * Initialize view
     */
    private void init(Context context) {
        editor = sharedPreferences.edit();

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = ViewInAppMessageBinding.inflate(inflater, this, true);

        buttonInAppMessage = binding.inAppMessageButton;

        mHandler = new Handler();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        downY = event.getY();
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        upY = event.getY();

                        float deltaY = downY - upY;

                        if(Math.abs(deltaY) > MIN_DISTANCE){
                            if(deltaY > 0) { onBottomToTopSwipe(); return true; }
                        }
                        else {
                            Log.d(TAG, "Swipe was only long, need at least " + MIN_DISTANCE);
                            return false; // We don't consume the event
                        }

                        return true;
                    }
                }
                return false;            }
        });

        setVisibility(GONE);
    }

    public void setup(String previousActivity) {
        boolean inAppMessageExistsForUser = sharedPreferences.getBoolean("in_app_message_exists_for_user", false);
        int secondsDelay = sharedPreferences.getInt("message_seconds_delay", 0);
        long appOpenedTS = sharedPreferences.getLong("app_opened_timestamp", 0);
        boolean inAppMessagePresented = sharedPreferences.getBoolean("in_app_message_presented", false);
        String messageText = sharedPreferences.getString("message_text", "");
        String buttonText = sharedPreferences.getString("button_text", "");

        long millis = System.currentTimeMillis();
        long currentTimeTS = millis / 1000;

        long showInAppMessageTS = appOpenedTS + secondsDelay;
        long updatedSecondsToDelay = showInAppMessageTS - currentTimeTS;

        int idInt = sharedPreferences.getInt("message_id", 0);
        messageIDStr = String.valueOf(idInt);

        buttonInAppMessage.setText(buttonText);
        binding.inAppMessageText.setText(messageText);

        setOnClick(buttonInAppMessage, previousActivity);

        if (inAppMessageExistsForUser) {
            Log.d(TAG, "in app message exists for user");

            if (!inAppMessagePresented) {
                Log.d(TAG, "in app message has not been presented");

                inAppMessageManager.postInAppMessageSeen(messageIDStr);
                mHandler.postDelayed(mRunnableDelayShow, updatedSecondsToDelay*1000);
            } else {
                setVisibility(VISIBLE);
            }
        }
    }

    public void onBottomToTopSwipe(){
        Log.d(TAG, "Swipe up gesture recognized.");
        Animation slideUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.in_app_message_slide_up);
        startAnimation(slideUpAnimation);

        editor.putBoolean("in_app_message_exists_for_user", false).apply();
        setVisibility(GONE);
        Log.d(TAG, "In app message dismissed.");

        inAppMessageManager.postInAppMessageDismissed(messageIDStr);
        Log.d(TAG, "Marked in app message as dismissed.");
    }

    private void setOnClick(final Button btn, final String previousActivity){
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), InAppWebViewActivity.class);
                intent.putExtra("caller", previousActivity);
                intent.putExtra("from_in_app_message", true);
                getContext().startActivity(intent);

                editor.putBoolean("in_app_message_exists_for_user", false).apply();
                inAppMessageManager.postInAppMessageTapped(messageIDStr);
                setVisibility(GONE);
            }
        });
    }


    Runnable mRunnableDelayShow = new Runnable() {
        @Override
        public void run() {
            animateShowEntireView();
        }
    };

    private void animateShowEntireView() {
        setTranslationY(0f);
        setVisibility(VISIBLE);

        Animation slideDownAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.in_app_message_slide_down);
        startAnimation(slideDownAnimation);

        editor.putBoolean("in_app_message_presented", true).apply();
        Log.d(TAG, "In app message has been presented");
    }
}
