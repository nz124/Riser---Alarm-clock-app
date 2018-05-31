package com.example.hello.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    public static final int REQUEST_ACCEPT_CHALLENGE = 101;
    public static final int REQUEST_DECLINE_CHALLENGE = 100;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("MESSAGE", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("MESSAGE", "Message data payload: " + remoteMessage.getData());
            // Handle message within 10 seconds
            handleNow(notification, data);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("MESSAGE BODY", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void handleNow(RemoteMessage.Notification notification, Map<String,String> data) {
        PendingIntent acceptChallenge = PendingIntent.getBroadcast(this, REQUEST_ACCEPT_CHALLENGE, new Intent(this, ChallengeReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent declineChallenge = PendingIntent.getBroadcast(this, REQUEST_DECLINE_CHALLENGE, new Intent(this, ChallengeReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action acceptChallengeAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_accept_challenge,
                        "Accept Challenge", acceptChallenge)
                        .build();

        NotificationCompat.Action declineChallengeAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_run_fast,
                        "Not Today", declineChallenge)
                        .build();


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id");
        String challengeMessage = data.get("challenge_message");
        if (challengeMessage != null && !"".equals(challengeMessage)){
            notificationBuilder
                    .setContentTitle(notification.getTitle())
                    .setContentText(challengeMessage)
                    .setSmallIcon(R.drawable.ic_challenge_icon)
                    .addAction(acceptChallengeAction)
                    .addAction(declineChallengeAction)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

}
