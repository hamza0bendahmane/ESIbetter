package com.example.esibetter.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.esibetter.R;
import com.example.esibetter.articles.Article_activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.esibetter.notifications.app.NOTIFICATION;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Context mContext;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: ");
        Log.d(TAG, "onMessageReceived: " + getSharedPreferences(NOTIFICATION, MODE_PRIVATE).getBoolean(NOTIFICATION, true));

        mContext = MyFirebaseMessagingService.this;
        // Check if message contains a notification payload.
        handleDataMessage(remoteMessage);
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String token) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app servegetString(R.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]


    private void sendRegistrationToServer(String token) {
        DatabaseReference tokenId = FirebaseDatabase.getInstance().getReference().
                child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        tokenId.setValue(token);

    }


    private void handleDataMessage(RemoteMessage remoteMessage) {

        final String title = remoteMessage.getData().get("title");
        String likes = remoteMessage.getData().get("likes");
        String dislikes = remoteMessage.getData().get("dislikes");
        String post_id = remoteMessage.getData().get("post_id");
        String type = remoteMessage.getData().get("type");
        String date = remoteMessage.getData().get("date");
        String image = remoteMessage.getData().get("image");
        String body = remoteMessage.getData().get("body");
        String uuid = remoteMessage.getData().get("uid");
        String name = remoteMessage.getData().get("name");
        String reply = remoteMessage.getData().get("reply");
        String number = remoteMessage.getData().get("number");
        String comment = remoteMessage.getData().get("comment");


        final Intent resultIntent = getIntent("");

        Bundle rresultIntent = new Bundle();
        rresultIntent.putString("likes", likes);
        rresultIntent.putString("dislikes", dislikes);
        rresultIntent.putString("PostId", post_id);
        rresultIntent.putString("title", title);
        rresultIntent.putString("date", date);
        rresultIntent.putString("image", image);
        rresultIntent.putString("uid", uuid);
        rresultIntent.putString("reply", reply);
        rresultIntent.putString("body", body);
        resultIntent.putExtras(rresultIntent);
        boolean active = getSharedPreferences(NOTIFICATION, MODE_PRIVATE).getBoolean(NOTIFICATION, true);

        if (active)
            showNotificationMessage(title, likes, dislikes, resultIntent, type, name, number, comment, reply);


    }


    public void showNotificationMessage(final String title
            , final String likes,
                                        final String dislikes, Intent intent, String notification_type,
                                        String name, String number, String comment, String reply) {

        // Check for empty push message

        int requestID = (int) System.currentTimeMillis();

        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        requestID,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT //PendingIntent.FLAG_CANCEL_CURRENT
                );
        try {

            showSmallNotification(title, likes, dislikes, resultPendingIntent, notification_type, name, number, comment, reply);

        } catch (Exception e) {
            Log.e("showNotificationMessage", e.getMessage() == null ? "" : e.getMessage());
        }
    }

    public Intent getIntent(String click_action) {

        Intent resultIntent = new Intent(mContext, Article_activity.class);

/*        switch (click_action) {
            case "com.amsavarthan.social.hify.TARGETNOTIFICATION":
                resultIntent = new Intent(mContext, NotificationActivity.class);
                break;
            case "com.amsavarthan.social.hify.TARGETNOTIFICATIONREPLY":
                resultIntent = new Intent(mContext, NotificationReplyActivity.class);
                break;
            case "com.amsavarthan.social.hify.TARGETNOTIFICATION_IMAGE":
                resultIntent = new Intent(mContext, NotificationImage.class);
                break;

        }*/
        return resultIntent;

    }


    private void showSmallNotification(String title, String likes, String dislikes,
                                       PendingIntent resultPendingIntent, String notification_type,
                                       String name, String number, String comment, String reply) {

        int id;
        String mess;
        NotificationCompat.Builder mBuilder;
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);

        switch (notification_type) {

            case "like":
                id = 100;
                mBuilder = new NotificationCompat.Builder(mContext, "like_channel");
                mess = getString(R.string.new_reactions) + likes + getString(R.string.likes) + dislikes + getString(R.string.dislikes);

                break;
            case "comment":
                id = 200;
                mBuilder = new NotificationCompat.Builder(mContext, "comment_channel");
                mess = getString(R.string.new_comment) + name + getString(R.string.it_said) + comment + getString(R.string.your_post_has) + number + getString(R.string.comments);
                break;
            case "reply":
                id = 300;
                mBuilder = new NotificationCompat.Builder(mContext, "reply_channel");
                mess = getString(R.string.you_got_new_reply) + comment + getString(R.string.from) + name + getString(R.string.it_said) + reply + getString(R.string.in_th_post) + title + "  .";
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + notification_type);
        }

        Notification notification;

        notification = mBuilder
                .setAutoCancel(true)
                .setContentTitle(title)
                .setTicker(title)
                .setContentIntent(resultPendingIntent)
                .setColorized(true)
                .setShowWhen(true)
                .setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.hify_sound))
                .setColor(getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.logo_brand)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(mess))
                .setContentText(mess)
                .build();


        notificationManagerCompat.notify(id, notification);

    }
}
