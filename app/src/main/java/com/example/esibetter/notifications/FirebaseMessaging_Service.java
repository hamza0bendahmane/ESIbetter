package com.example.esibetter.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.esibetter.R;
import com.example.esibetter.articles.news;
import com.example.esibetter.community.Study;
import com.example.esibetter.courses.Tutorials;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging_Service extends FirebaseMessagingService {

    public static final String NOTIFICATION = "notification";

//boolean notificationIsEnabled =        getSharedPreferences(NOTIFICATION, MODE_PRIVATE).getBoolean(NOTIFICATION, false);


    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {


        if (remoteMessage.getData().size() > 0) {
            Log.d("hbhbhb", "onMessageReceived: data not null ");
        }
        if (remoteMessage.getNotification() != null && true) {

            String body = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();
            String channelId = remoteMessage.getNotification().getChannelId();

            sendNotification(body, channelId, title);

        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d("hbhbhb", "onNewToken: ");
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // save user token ....


    }


    private void sendNotification(String messageBody, String chanel_id, String title) {

        Intent intent;
        switch (chanel_id) {
            case "COMMUNITY":
                intent = new Intent(this, Study.class);
                break;
            case "IDEAS":
                intent = new Intent(this, news.class);
                break;
            default:
                intent = new Intent(this, Tutorials.class);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.
                getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        Notification notificationBuilder =
                new NotificationCompat.Builder(this, chanel_id)
                        .setSmallIcon(R.drawable.logo_brand)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent).build();

        notificationManagerCompat.notify(1, notificationBuilder);
    }

}