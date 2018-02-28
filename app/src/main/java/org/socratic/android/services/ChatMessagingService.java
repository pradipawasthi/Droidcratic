package org.socratic.android.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.socratic.android.R;
import org.socratic.android.SocraticApp;
import org.socratic.android.activities.ChatDetailActivity;

import java.util.Map;

import javax.inject.Inject;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

/**
 * Created by jessicaweinberg on 10/2/17.
 */

public class ChatMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";

    @Inject SharedPreferences sharedPreferences;
    private boolean isFirstTimeUser;

    @Override
    public void onCreate() {
        super.onCreate();

        SocraticApp.getServiceComponent(this).inject(this);

        isFirstTimeUser = sharedPreferences.getBoolean("firstTime", true);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /**
         * Stop-gap for broken notifications, needs further testing,
         * re-visit for full solution if notification
         * functionality required by product
         */

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Map<String, String> data = remoteMessage.getData();
            String personName = data.get("chat_name");
            String channelId = data.get("channel_id");
            String chatState = data.get("chat_state");

            if (!isFirstTimeUser) {

                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                                .setLargeIcon(icon)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.cbo)
                                .setContentTitle(personName)
                                .setContentText("Sent a message");

                Intent resultIntent = new Intent(this, ChatDetailActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(ChatDetailActivity.class);
                resultIntent.putExtra("open_chat_fragment", true);
                resultIntent.putExtra("chat_name", personName);
                resultIntent.putExtra("channel_id", channelId);
                resultIntent.putExtra("chat_state", chatState);
                stackBuilder.addNextIntent(resultIntent);

                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                int mNotificationId = 001;
                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mBuilder.setContentIntent(resultPendingIntent);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        }

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    public boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }
}
