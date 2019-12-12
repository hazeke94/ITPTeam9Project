package com.example.portermanagementsystem.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Util.util;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

//    private Context context;
    private static final String TAG = "NotificationService";
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String role = "";
    Intent intent;
    String test = " ";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Notification Received");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final Map<String, String> data = remoteMessage.getData();

        if(sharedpreferences.contains("StaffID")){
            role = sharedpreferences.getString("Role", null);
            test = remoteMessage.getData().get("message");
        }

        sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getData().get("title"));
    }


    private void sendNotification(String messageBody,String title) {
        String[] message = messageBody.split(":");
        String[] caseID = message[1].split("is");

        Log.d(TAG, messageBody);
        if(role.equals("Porter")){
            intent = new Intent(this, PorterDashboardActivity.class);
            intent.putExtra("Assigned", true);

            if(messageBody.contains("Urgency") || messageBody.contains("Destination") || messageBody.contains("Type")){
//                Log.d(TAG,"messageBody : " + messageBody);
                notify(title, messageBody);
            }
            else {
//                Log.d(TAG,"message[1] : " + message[1]);
//                Log.d(TAG, "message : " + messageBody);
                notify(title, messageBody);
            }
        }
        else{
            util.cancelWork(caseID[0].trim());
            intent = new Intent(this, ReceptionistDashboardActivity.class);
//            notify(title,message[1]);
            notify(title, messageBody);
        }

    }

    public String onTokenRefreshed() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        
        return refreshedToken;
    }

    public void notify(String title, String messageBody){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "MAH");
        mBuilder.setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(Color.parseColor("#FFD600"))
                .setContentIntent(pendingIntent)
                .setChannelId("MAH")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel("MAH", "Porter", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            mBuilder.setChannelId("MAH");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, mBuilder.build());
    }

}

