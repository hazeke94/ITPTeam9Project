package com.example.portermanagementsystem.Activity.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.portermanagementsystem.Activity.CreateJob.CreateJob1Activity;
import com.example.portermanagementsystem.Activity.JobHistoryActivity;
import com.example.portermanagementsystem.Activity.JobOngoingActivity;
import com.example.portermanagementsystem.Activity.JobQueueActivity;
import com.example.portermanagementsystem.Activity.JobReportActivity;
import com.example.portermanagementsystem.Activity.LoginActivity;
import com.example.portermanagementsystem.Activity.PorterAvailabilityActivity;
import com.example.portermanagementsystem.Activity.StaffActivity;
import com.example.portermanagementsystem.Activity.StaffListActivity;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.R;

import java.util.List;

public class ReceptionistDashboardActivity extends AppCompatActivity {
    private static final String TAG = "ReceptionistDashboard";
    CardView cardViewReport, cardViewManageUser;
    TextView textViewNumberOfOngoingTasks, textViewNumberOfQueueJob;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    String role;
    String staffID;
    JobControllerInterface jobController = new JobController();
    JobFirebaseInterface jobFirebase = new JobFirebase();
    UserControllerInterface userController = new UserController();

    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptionist_dashboard);
        init();
//        final ProgressDialog Dialog = new ProgressDialog(ReceptionistDashboardActivity.this);
//        Dialog.setMessage("Loading,please hold.");
//        Dialog.show();


        jobFirebase.getAllJob(new JobCallBack() {
            @Override
            public void onCallBack(Job value) {

            }

            @Override
            public void onCallBack(List<Job> value) {
                textViewNumberOfOngoingTasks.setText(String.valueOf(jobController.getJobCount(value).get(0)));
                textViewNumberOfQueueJob.setText(String.valueOf(jobController.getJobCount(value).get(1)));
//                Dialog.hide();
            }

            @Override
            public void onCallBack(boolean value) {

            }

            @Override
            public void onCallBack(String value) {

            }
        });
    }

    public void init(){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        textViewNumberOfOngoingTasks = findViewById(R.id.textViewNumberOfOngoingTasks);
        textViewNumberOfQueueJob = findViewById(R.id.textViewNumberOfQueueJob);
        cardViewReport = findViewById(R.id.cardViewReport);
        cardViewManageUser = findViewById(R.id.cardViewStaff);

        role = sharedpreferences.getString("Role", null);
        if(!role.equals("Manager")){
            cardViewReport.setVisibility(View.GONE);
            cardViewManageUser.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void porterAvailable(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(ReceptionistDashboardActivity.this, PorterAvailabilityActivity.class);
        startActivity(intent);
    }

    public void addJobRept(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(ReceptionistDashboardActivity.this, CreateJob1Activity.class);
        startActivity(intent);
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
        Intent intent = new Intent(ReceptionistDashboardActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void queue(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(ReceptionistDashboardActivity.this, JobQueueActivity.class);
        startActivity(intent);
    }

    public void ongoing(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(ReceptionistDashboardActivity.this, JobOngoingActivity.class);
        startActivity(intent);
    }

    public void porterReport(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(ReceptionistDashboardActivity.this, JobReportActivity.class);
        startActivity(intent);
    }

    public void recept_history(View view) {
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(ReceptionistDashboardActivity.this, JobHistoryActivity.class);
        startActivity(intent);
    }

    public void staff_mgmt(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(ReceptionistDashboardActivity.this, StaffListActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}


