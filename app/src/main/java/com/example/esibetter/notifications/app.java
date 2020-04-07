package com.example.esibetter.notifications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.esibetter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class app extends Application {
    public static final String NOTIFICATION = "notification";

    @Override
    public void onCreate() {
        super.onCreate();
        //intro ...
        SharedPreferences store_data = getSharedPreferences("seen_intro", MODE_PRIVATE);
        if (!store_data.contains("have_seen_intro"))
            store_data.edit().putBoolean("have_seen_intro", false).apply();
        // notification ... SHARED PREFS ...

        if (!getSharedPreferences(NOTIFICATION, MODE_PRIVATE).contains(NOTIFICATION))
            getSharedPreferences(NOTIFICATION, MODE_PRIVATE).edit()
                    .putBoolean(NOTIFICATION, true).apply();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        Log.d("hbhb", token);
                    }
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            String channelIdc = getString(R.string.community_notification_channel_id);
            String channelNamec = getString(R.string.community_notification_channel_name);
            String channelIdi = getString(R.string.ideas_notification_channel_id);
            String channelNamei = getString(R.string.ideas_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            NotificationChannel generalNotifications = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT);
            generalNotifications.setDescription("General Notifications");
            NotificationChannel communityNotifications = new NotificationChannel(channelIdc,
                    channelNamec, NotificationManager.IMPORTANCE_MAX);
            communityNotifications.setDescription("Community Notifications");
            NotificationChannel ideasNotifications = new NotificationChannel(channelIdi,
                    channelNamei, NotificationManager.IMPORTANCE_HIGH);
            ideasNotifications.setDescription("Articles Notifications");

            notificationManager.createNotificationChannel(generalNotifications);
            notificationManager.createNotificationChannel(communityNotifications);
            notificationManager.createNotificationChannel(ideasNotifications);

        }
    }


}