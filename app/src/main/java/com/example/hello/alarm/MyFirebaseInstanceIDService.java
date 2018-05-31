package com.example.hello.alarm;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        String refreshedToken;
        try {
            refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("", "Refreshed token: " + refreshedToken);
        } catch (Exception e) {
            Log.d("", "Refreshed token, catch: " + e.toString());
            e.printStackTrace();
        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }
//
//    public void sendRegistrationToServer(String refreshedToken){
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null){
//            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid()).child("FCM Token");
//            myRef.setValue(refreshedToken);
//        }
//    }
}
