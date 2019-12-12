package com.example.portermanagementsystem.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.R;

public class StaffActivity extends AppCompatActivity {

    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff2);
    }

    public void addStaff(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(StaffActivity.this, CreateUserActivity.class);
        startActivity(intent);
    }

    public void manageStaff(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(StaffActivity.this, StaffListActivity.class);
        startActivity(intent);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(StaffActivity.this, ReceptionistDashboardActivity.class));
        finish();
    }

}
