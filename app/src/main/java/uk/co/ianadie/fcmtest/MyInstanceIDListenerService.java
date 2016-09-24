package uk.co.ianadie.fcmtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

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
    public void onTokenRefresh() {
        Log.d(TAG, "onTokenRefresh");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM token refreshed: " + refreshedToken);
        prefsEditor.putString("fcm_token", refreshedToken).apply();
        Intent intent = new Intent(MainActivity.INTENT_FILTER);
        intent.putExtra("text", "FCM token: " + refreshedToken);
        broadcaster.sendBroadcast(intent);
    }
}
