package uk.co.ianadie.fcmtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyInstanceIDListenerService extends FirebaseMessagingService {

    private static final String TAG = MyInstanceIDListenerService.class.getName();
    LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onNewToken(String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();

        prefsEditor.putString("fcm_token", token).apply();
        Intent intent = new Intent(MainActivity.INTENT_FILTER);
        intent.putExtra("text", "FCM token: " + token);
        broadcaster.sendBroadcast(intent);
    }
}
