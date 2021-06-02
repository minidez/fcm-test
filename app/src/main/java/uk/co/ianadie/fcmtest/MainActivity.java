package uk.co.ianadie.fcmtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String WEB_API_KEY = ""; // TODO: YOUR WEB API KEY HERE

    private static final String TAG = MainActivity.class.getName();
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    static final String INTENT_FILTER = "uk.co.ianadie.fcmtest.MESSAGE_RECEIVED";
    private FirebaseAnalytics firebaseAnalytics;

    SharedPreferences sharedPrefs;
    TextView logWindow;
    Button sendButton;
    EditText messageView;
    BroadcastReceiver receiver;
    String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        logWindow = (TextView) findViewById(R.id.bigTextView);
        logWindow.setMovementMethod(new ScrollingMovementMethod());
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
        messageView = (EditText) findViewById(R.id.messageText);
        messageView.setOnEditorActionListener(this);

        getFcmToken(sharedPrefs, logWindow);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String text = intent.getStringExtra("text");
                logWindow.append(text);
                firebaseAnalytics.logEvent("receive_message", null);
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendButton) {
            sendMessage();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // Send message when user clicks "send" on keyboard
        sendMessage();
        return true;
    }

    private void sendMessage() {
        fcmToken = sharedPrefs.getString("fcm_token", null);
        if (fcmToken != null) {
            try {
                sendMessage(messageView.getText().toString(), fcmToken);
            } catch (JSONException e) {
                Log.e(TAG, "JSON Exception");
                logWindow.append("\n\nJSONExceptionl, sending failed");
            }
        } else {
            Log.e(TAG, "FCM token is null");
            logWindow.append("\n\nFCM token is null, sending failed");
        }
    }

    private void sendMessage(String message, String fcmToken) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject notification = new JSONObject();
        notification.put("message", message);
        JSONObject body = new JSONObject();
        body.put("to", fcmToken);
        body.put("data", notification);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, FCM_URL, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleErrorResponse(error);
                    }
                }) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new ArrayMap<>();
                headers.put("Authorization", "key=" + WEB_API_KEY);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        logWindow.append("\n\nSending message");
        queue.add(jsonRequest);
    }

    private void handleResponse(JSONObject response) {
        try {
            int success = response.getInt("success");
            if (success == 1) {
                logWindow.append("\n\nNotification sent successfully");
            } else {
                String error = response.getJSONArray("results").getJSONObject(0).getString("error");
                logWindow.append("\n\nError sending notification: " + error);
            }
            Bundle params = new Bundle();
            params.putString("success", success == 1 ? "true" : "false");
            firebaseAnalytics.logEvent("send_message", params);
        } catch (JSONException e) {
            Log.e(TAG, "Failure parsing JSON response");
            logWindow.append("\nFailure parsing JSON response");
        }
    }

    private void handleErrorResponse(VolleyError error) {
        int statusCode = error.networkResponse.statusCode;
        if (statusCode == 401)
            logWindow.append("\n\nHTTP 401, check your web API key");
        else if (statusCode == 400)
            logWindow.append("\n\nHTTP 400, check json request format");
        else
            logWindow.append("\n\nHTTP " + statusCode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register broadcast receiver to allow services to update log window
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(INTENT_FILTER)
        );
    }

    @Override
    protected void onStop() {
        // Remove broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    private void getFcmToken(final SharedPreferences sharedPrefs, final TextView logWindow) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        Log.d(TAG, "FCM token: "+token);
                        sharedPrefs.edit().putString("fcm_token", token).apply();
                        logWindow.append("FCM token: " + token);
                    }
                });
    }
}
