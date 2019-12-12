package com.example.portermanagementsystem.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.Adapter.AssignPorterListAdapter;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Model.User;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AssignPorterActivity extends AppCompatActivity {
    private static final String TAG = "AssignPorterActivity";
    AssignPorterListAdapter adapter;
    RecyclerView recyclerViewAvailability;
    protected String JobID;
    UserFirebaseInterface userFirebase = new UserFirebase();

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_porter);
        init();
        LinearLayoutManager layoutManager = new LinearLayoutManager((AssignPorterActivity.this));
        recyclerViewAvailability.setLayoutManager(layoutManager);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        userFirebase.getAllOnlinePorter(new UserCallBack() {
            @Override
            public void onCallBack(User value) {

            }

            @Override
            public void onCallBack(List<User> value) {
                final String staffID = sharedpreferences.getString("StaffID", null);
                adapter = new AssignPorterListAdapter(value, AssignPorterActivity.this, staffID);
                recyclerViewAvailability.setAdapter(adapter);
            }

            @Override
            public void onCallBack(String value) {

            }

            @Override
            public void onCallBack(Boolean value) {

            }
        });
    }

    //Initialise value
    private void init(){
        Intent intent = getIntent();
        JobID = intent.getStringExtra("JobID");
        recyclerViewAvailability = findViewById(R.id.recyclerViewAvailability);
    }

    //Button Home Page Clicked
    public void home(View view){
        Intent intent = new Intent(AssignPorterActivity.this, ReceptionistDashboardActivity.class);
        startActivity(intent);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(AssignPorterActivity.this, ReceptionistDashboardActivity.class));
        finish();
    }


}
