package org.socratic.android.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.socratic.android.SocraticApp;
import org.socratic.android.globals.TokenManager;

import javax.inject.Inject;

/**
 * Created by jessicaweinberg on 10/2/17.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Inject TokenManager tokenManager;

    @Override
    public void onCreate() {
        super.onCreate();

        SocraticApp.getServiceComponent(this).inject(this);
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        tokenManager.startSendToken(token);
        // Add custom implementation, as needed.
    }
}
