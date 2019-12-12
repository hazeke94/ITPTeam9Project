package com.example.portermanagementsystem.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.Adapter.StaffListAdapter;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;

import java.util.List;

public class StaffListActivity extends AppCompatActivity {
    UserFirebaseInterface userFirebase = new UserFirebase();
    StaffListAdapter adapter;
//    UserControllerInterface userController = new UserController();
    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);

        final RecyclerView recyclerView = findViewById(R.id.rvStaff);
        LinearLayoutManager layoutManager = new LinearLayoutManager((StaffListActivity.this));
        recyclerView.setLayoutManager(layoutManager);

        userFirebase.getAllUser(new UserCallBack() {
            @Override
            public void onCallBack(User value) {

            }

            @Override
            public void onCallBack(List<User> value) {
                adapter = new StaffListAdapter(value, StaffListActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCallBack(String value) {

            }

            @Override
            public void onCallBack(Boolean value) {

            }
        });
    }

    public void addStaff(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(StaffListActivity.this, CreateUserActivity.class);
        startActivity(intent);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(StaffListActivity.this, ReceptionistDashboardActivity.class));
        finish();
    }
}
