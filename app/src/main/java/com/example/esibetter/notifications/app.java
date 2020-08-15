package com.example.esibetter.notifications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.esibetter.R;
import com.google.firebase.database.FirebaseDatabase;

public class app extends Application {

    public static final String NOTIFICATION = "notification";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // notification ... SHARED PREFS ...
        if (!getSharedPreferences("seen_intro", MODE_PRIVATE).contains("have_seen_intro"))
            getSharedPreferences("seen_intro", MODE_PRIVATE).edit().putBoolean("have_seen_intro", false).apply();
        if (!getSharedPreferences(NOTIFICATION, MODE_PRIVATE).contains(NOTIFICATION))
            getSharedPreferences(NOTIFICATION, MODE_PRIVATE).edit()
                    .putBoolean(NOTIFICATION, true).apply();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            String reply_channel = getString(R.string.community_notification_channel_id);
            String reply_channelNamec = getString(R.string.community_notification_channel_name);
            String comment_channel = getString(R.string.iideas_notification_channel_id);
            String comment_channelNamei = getString(R.string.iideas_notification_channel_name);
            String like_channel = getString(R.string.ideas_notification_channel_id);
            String like_channelNamei = getString(R.string.ideas_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            NotificationChannel generalNotifications = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT);
            generalNotifications.setDescription("General Notifications");
            NotificationChannel communityNotifications = new NotificationChannel(reply_channel,
                    reply_channelNamec, NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel gg = new NotificationChannel(comment_channel,
                    comment_channelNamei, NotificationManager.IMPORTANCE_HIGH);
            communityNotifications.setDescription("Comments Notifications");

            communityNotifications.setDescription("Replies Notifications");
            NotificationChannel ideasNotifications = new NotificationChannel(like_channel,
                    like_channelNamei, NotificationManager.IMPORTANCE_HIGH);
            ideasNotifications.setDescription("Likes Notifications");

            notificationManager.createNotificationChannel(gg);
            notificationManager.createNotificationChannel(generalNotifications);
            notificationManager.createNotificationChannel(communityNotifications);
            notificationManager.createNotificationChannel(ideasNotifications);

        }
    }


}