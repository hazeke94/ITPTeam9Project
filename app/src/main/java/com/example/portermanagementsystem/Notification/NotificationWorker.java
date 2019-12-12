package com.example.portermanagementsystem.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationWorker extends Worker {

    public NotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //retrieve arg from activity
        String argX = getInputData().getString("X");

        //if argX not null, send notification
        if(argX != null)
        {
            addNotification(argX);
        }

        return Result.success();
    }

    private void addNotification(String notiID) {
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "Portering System";
        String Description = "Job Channel";

        String BigText = "";
        int NOTIFICATION_ID = 0;

        if(notiID.equals("two")) {
            BigText = "2 mins have passed, please assign a Porter!";
            NOTIFICATION_ID = 235;
        }
        else if(notiID.equals("three")) {
            BigText = "5 mins have passed, please assign a Porter!";
            NOTIFICATION_ID = 236;
        }
        else
         {
            BigText = "Times up.. Time exceeded 10mins!";
            NOTIFICATION_ID = 237;
        }

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 100});
            mChannel.setShowBadge(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        Intent resultIntent = new Intent(getApplicationContext(), ReceptionistDashboardActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Portering job")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(BigText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_check_circle_white_24dp)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_foreground, "Check Job progress!", resultPendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
