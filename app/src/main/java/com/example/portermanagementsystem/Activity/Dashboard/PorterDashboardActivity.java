package com.example.portermanagementsystem.Activity.Dashboard;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.portermanagementsystem.Activity.CreateJob.CreateJob1Activity;
import com.example.portermanagementsystem.Activity.EditJobDetailActivity;
import com.example.portermanagementsystem.Activity.JobHistoryActivity;
import com.example.portermanagementsystem.Activity.LoginActivity;
import com.example.portermanagementsystem.Activity.RemarkActivity;
import com.example.portermanagementsystem.Activity.Scanner.ScannerJobActivity;
import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class PorterDashboardActivity extends AppCompatActivity {
    private static final String TAG = "PorterDashboardActivity";
    TextView textViewStartLocation, textViewDestination, textViewCaseID, textViewCompleteTime, textViewArrivalTime, textViewPickUpTime, textViewStartTime,textViewPendingJob, textViewDescription;
    ImageView imageViewStartTime, imageViewPickUp, imageViewArrival, imageViewEndTime, imageViewJobType, imageViewDelete;
    public Button button;
    Button fabPorter;
    RelativeLayout surroundAdd;
    LinearLayout descriptionLinearLayout;
    CardView haveJobCardView, noJobCardView;
    public String staffID,role,currentStaffID, porterName;
    private ProgressDialog Dialog;
    JobFirebaseInterface jobFirebase = new JobFirebase();
    UserFirebaseInterface userFirebase = new UserFirebase();
    UserControllerInterface userController = new UserController();
    JobControllerInterface jobController = new JobController();
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    List<User> userList = new ArrayList<>();
    //Initialise Job
    Job cJob;
    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();
    //push
    Boolean ack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porter_dashboard);
        init();
        final ProgressDialog Dialog = new ProgressDialog(PorterDashboardActivity.this);
        Dialog.setMessage("Loading,please hold.");
        Dialog.show();

        haveJobCardView.setVisibility(View.GONE);
        //Get the total number of job
        jobFirebase.getJobByUrgencyLevel("Porter", staffID, new JobCallBack() {
            @Override
            public void onCallBack(Job value) { }

            @Override
            public void onCallBack(List<Job> value) {
                List<Job> jobList =  ArrayList.class.cast(jobController.getPorterJob(value).get(0));
                int queue =0;
                cJob = Job.class.cast(jobController.getPorterJob(value).get(1));
                queue = Integer.valueOf((Integer) jobController.getPorterJob(value).get(2));

                if(jobList.size() <=0){
                    noJobCardView.setVisibility(View.VISIBLE);
                    haveJobCardView.setVisibility(View.GONE);
                }else{
                    haveJobCardView.setVisibility(View.VISIBLE);
                    noJobCardView.setVisibility(View.GONE);
                    if(cJob == null){
                        for(Job job: jobList){
                            if(job.getStatus().equals("Assigned")){
                                cJob = job;
                                displayDetail(job);
                                break;
                            }else{
                                noJobCardView.setVisibility(View.VISIBLE);
                                haveJobCardView.setVisibility(View.GONE);

                                fabPorter.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
                                surroundAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
                                fabPorter.setEnabled(true);

                            }
                        }
                    }else{
                        displayDetail(cJob);
                    }
                }
                if (cJob != null) {
                    if (cJob.getStatus().equals("Assigned")) {
                        Intent intent2 = new Intent(PorterDashboardActivity.this, PorterDashboardActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(PorterDashboardActivity.this, 0, intent2,
                                PendingIntent.FLAG_ONE_SHOT);

                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(PorterDashboardActivity.this, "MAH");
                        mBuilder.setContentTitle("MAH Porter System")
                                .setContentText("A newly posted job has been assigned to you!")
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

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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

                Dialog.hide();
                textViewPendingJob.setText(String.format("Job in queue: %s", String.valueOf(queue)));
            }

            @Override
            public void onCallBack(boolean value) { }

            @Override
            public void onCallBack(String value) { }
        });
        Log.d(TAG, "Porter name: " + porterName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void addJobPorter(View view) {
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(PorterDashboardActivity.this, CreateJob1Activity.class);
        startActivity(intent);
    }

    public void scanQRFunction(View view) {
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        if (button.getText().equals("Pick Up")) {
            if (cJob.getTypeOfJob().equals("Document")) {
                showDialog("Is the document ready?");
            } else {
                showDialog("Is the patient ready?");
            }
        } else if (button.getText().equals("Complete")) {
            showDialog("Is the nurse ready?");
        } else if (button.getText().equals("Acknowledge")) {
            showDialog("I acknowledge this job.");
        } else {
            Intent intent = new Intent(PorterDashboardActivity.this, ScannerJobActivity.class);
            intent.putExtra("CurrentJob", cJob);
            startActivity(intent);
        }
    }

    public void showDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PorterDashboardActivity.this);
        switch (message) {
            case "Is the document ready?":
                builder.setTitle("Pick Up Confirmation");
                break;
            case "Is the patient ready?":
                builder.setTitle("Pick Up Confirmation");
                break;
            case "Is the nurse ready?":
                builder.setTitle("Complete Confirmation");
        }
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //intent to completeJob Activity
                dialog.dismiss();
                if(!cJob.getStatus().equals("Arrived")){
                    jobController.updateJobStatus(cJob);
                }
                if(cJob.getStatus().equals("Assigned")){
                    userController.updatePorterStatus(staffID, "busy");
                }else if(cJob.getStatus().equals("Arrived")){
                    updateCompleteStatus("Completed");
                }
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void logOutFunction(MenuItem item) {
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        staffID = sharedpreferences.getString("StaffID", null);
        userController.clearFcmToken(staffID);
        sharedpreferences.edit().clear().commit();
        Intent intent = new Intent(PorterDashboardActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void showAll(View view) {
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(PorterDashboardActivity.this, JobHistoryActivity.class);
        startActivity(intent);
    }

    public void init() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        staffID = sharedpreferences.getString("StaffID", null);
        role = sharedpreferences.getString("Role", null);
        currentStaffID = sharedpreferences.getString("StaffID", null);
        userFirebase.getAllUser(new UserCallBack() {
            @Override
            public void onCallBack(User value) {

            }

            @Override
            public void onCallBack(List<User> value) {
                userList = value;
            }

            @Override
            public void onCallBack(String value) {
                porterName = value;

            }

            @Override
            public void onCallBack(Boolean value) {

            }
        });
        haveJobCardView = findViewById(R.id.haveJobCardView);
        surroundAdd = findViewById(R.id.surroundAdd);
        noJobCardView = findViewById(R.id.noJobCardView);
        textViewStartLocation = findViewById(R.id.textViewStartLocation);
        textViewDestination = findViewById(R.id.textViewDestination);
        textViewCaseID = findViewById(R.id.textViewCaseID);
        textViewCompleteTime = findViewById(R.id.textViewCompleteTime);
        textViewArrivalTime = findViewById(R.id.textViewArrivalTime);
        textViewPickUpTime = findViewById(R.id.textViewPickUpTime);
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewDescription = findViewById(R.id.textViewDescription);
        descriptionLinearLayout = findViewById(R.id.descriptionLinearLayout);
        imageViewStartTime = findViewById(R.id.imageViewStartTime);
        imageViewPickUp = findViewById(R.id.imageViewPickUp);
        imageViewArrival = findViewById(R.id.imageViewArrival);
        imageViewEndTime = findViewById(R.id.imageViewEndTime);
        imageViewDelete = findViewById(R.id.imageViewDelete);
        imageViewJobType = findViewById(R.id.imageViewJobType);
        textViewPendingJob = findViewById(R.id.textViewPendingJob);
        button = findViewById(R.id.button);
        fabPorter = findViewById(R.id.fabPorter);
    }

    public void toggleCircle(TextView tv, ImageView iv) {
        if (tv.getText().equals("-")) {
            iv.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        } else {
            iv.setImageResource(R.drawable.ic_check_circle_white_24dp);
        }
    }

    public void toggleButton(String status) {
        if (status.equals("Assigned")) {
            button.setText("Acknowledge");
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return;
        }

        if (status.equals("Acknowledged")) {
            button.setText("Scan");
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camera_alt_black_24dp, 0, 0, 0);
            return;
        }

        if (status.equals("Started")) {
            button.setText("Pick Up");
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return;
        }

        if (status.equals("Pick Up")) {
            button.setText("Scan");
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camera_alt_black_24dp, 0, 0, 0);
            return;
        }

        if (status.equals("Arrived")) {
            button.setText("Complete");
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return;
        }
    }

    public void deleteJob(View view) {
////        Log.d(TAG,"Deleting Job " + staffID);
//        long now = System.currentTimeMillis();
//        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
//
//            return;
//
//        }
//        mLastClickTime = now;
        updateCompleteStatus("Cancelled");
    }

    public void editDetail(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;

        Intent intent = new Intent(this, EditJobDetailActivity.class);
        intent.putExtra("jobID", cJob.getJobID());
        startActivity(intent);
    }

    public void updateCompleteStatus(final String status){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        for(User user: userList){
            if(user.getStaffID().equals(staffID)){
                porterName = user.getName();
                break;
            }
        }
        Intent intent = new Intent(PorterDashboardActivity.this, RemarkActivity.class);
        intent.putExtra("JobID", cJob.getJobID());
        intent.putExtra("staffName", porterName);
        intent.putExtra("status", status);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void displayDetail(Job job){
        fabPorter.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.grey)));
        surroundAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.grey)));

        fabPorter.setEnabled(false);

//        if(job.getStatus().equals("Cancelled")){
//
//        }
        haveJobCardView.setVisibility(View.VISIBLE);
        noJobCardView.setVisibility(View.GONE);
        String jobCaseID = job.getCaseID();
        String jobFromLocation = job.getFromLocation();
        String jobToLocation = job.getToLocation();
        int jobUrgency = Integer.valueOf(job.getJobUrgency());
        String jobStartTime = job.getStartTime();
        String jobPickUpTime = job.getPickUpTime();
        String jobArrivalTime = job.getArrivalTime();
        String jobCompleteTime = job.getCompleteTime();
        try {
            String jobDescription = job.getDescription();
            if (jobDescription.equals("-")){
                descriptionLinearLayout.setVisibility(View.GONE);
            }
            else {
                descriptionLinearLayout.setVisibility(View.VISIBLE);
                textViewDescription.setText(jobDescription);
            }
        }
        catch(Exception e) {
            descriptionLinearLayout.setVisibility(View.GONE);
        }
        textViewCaseID.setText(jobCaseID);
        textViewStartLocation.setText(jobFromLocation);
        textViewDestination.setText(jobToLocation);
        textViewStartTime.setText(jobStartTime);
        textViewPickUpTime.setText(jobPickUpTime);
        textViewArrivalTime.setText(jobArrivalTime);
        textViewCompleteTime.setText(jobCompleteTime);
        toggleCircle(textViewStartTime, imageViewStartTime);
        toggleCircle(textViewPickUpTime, imageViewPickUp);
        toggleCircle(textViewArrivalTime, imageViewArrival);
        toggleCircle(textViewCompleteTime, imageViewEndTime);
        toggleButton(cJob.getStatus());
        String typeOfJob = cJob.getTypeOfJob();
        switch (typeOfJob){
            case "X-Ray":
                imageViewJobType.setImageResource(R.drawable.ic_skeleton);
                break;
            case "Labs":
                imageViewJobType.setImageResource(R.drawable.ic_lab);
                break;
            case "Discharge":
                imageViewJobType.setImageResource(R.drawable.ic_discharge_black_24dp);
                break;
            case "Document":
                imageViewJobType.setImageResource(R.drawable.ic_assignment_black_24dp);
                break;
            case "Transport":
                imageViewJobType.setImageResource(R.drawable.ic_transport_black_24dp);
                break;
            case "Inpatient":
                imageViewJobType.setImageResource(R.drawable.ic_inpatient_black_24dp);
                break;
            case "Day Surgery":
                imageViewJobType.setImageResource(R.drawable.ic_surgery);
                break;
            case "Maternity":
                imageViewJobType.setImageResource(R.drawable.ic_pregnant_woman_black_24dp);
                break;
        }
        switch (jobUrgency) {
            case 1:
                haveJobCardView.setBackgroundColor(Color.parseColor("#ff7675"));
                break;
            case 2:
                haveJobCardView.setBackgroundColor(Color.parseColor("#fdcb6e"));
                break;
            case 3:
                haveJobCardView.setBackgroundColor(Color.parseColor("#87CEFA"));
                break;
        }
    }
}