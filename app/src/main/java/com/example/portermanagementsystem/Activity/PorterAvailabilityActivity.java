package com.example.portermanagementsystem.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.portermanagementsystem.Adapter.PorterListAdapter;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Model.User;

import java.util.List;


public class PorterAvailabilityActivity extends AppCompatActivity{
    UserFirebaseInterface userFirebase = new UserFirebase();
    PorterListAdapter adapter;
    UserControllerInterface userController = new UserController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_availability);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager((PorterAvailabilityActivity.this));
        recyclerView.setLayoutManager(layoutManager);

        userFirebase.getAllPorter(new UserCallBack() {
            @Override
            public void onCallBack(User value) {

            }

            @Override
            public void onCallBack(List<User> value) {
                adapter = new PorterListAdapter(value, PorterAvailabilityActivity.this);
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

    public void resetAllToFalse(View view) {
        userController.resetAllPorter();
    }
}