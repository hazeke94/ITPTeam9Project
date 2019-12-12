package com.example.portermanagementsystem.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Activity.EditJobDetailActivity;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;

import java.util.ArrayList;
import java.util.List;

public class StaffDetailActivity extends AppCompatActivity {
    private static final String TAG = "StaffDetailActivity";
    public static final String MyPREFERENCES = "MyPrefs" ;

    UserFirebaseInterface userFirebase = new UserFirebase();
    UserControllerInterface userController = new UserController();

    EditText edStaffName, edStaffContact, edStaffEmail;
    Spinner staffRole;
    User user;
    String userId;
    List<String> list;


    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);
        init();

        list = new ArrayList<>();
        list.add("Porter");
        list.add("Receptionist");
        list.add("Manager");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staffRole.setAdapter(dataAdapter);

        //retrieve staff information on load
        userFirebase.getUserDetail(getIntent().getStringExtra("Staff"), new UserCallBack() {
            @Override
            public void onCallBack(User value) {
                edStaffName.setText(value.getName());
                userId = value.getStaffID();
                checkRole(value.getRole());

            }

            @Override
            public void onCallBack(List<User> value) {

            }

            @Override
            public void onCallBack(String value) {

            }

            @Override
            public void onCallBack(Boolean value) {

            }
        });
    }

    private void checkRole(String role) {
        for(int i =0;i<list.size();i++){
            if(role.equals(list.get(i))){
                staffRole.setSelection(i);
                break;
            }
        }
    }

    private void init() {
        edStaffName = findViewById(R.id.textViewStaffName);
//        edStaffContact = findViewById(R.id.textViewStaffName);
//        edStaffEmail = findViewById(R.id.textViewStaffName);
        staffRole = findViewById(R.id.spinnerRole);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void deleteFunction(MenuItem item) {
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        //dialog do you want to remove staff ""
//        deleteUser();
        final AlertDialog.Builder builder = new AlertDialog.Builder(StaffDetailActivity.this);
        builder.setMessage("Do you want to remove this user?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //intent to completeJob Activity
                dialog.dismiss();
                if(userId!=null){
                    userController.deleteUser(userId);
                }
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void updateStaff(View view){
        String staff_role = staffRole.getSelectedItem().toString();
        userController.updateStaff(userId, staff_role);
        Log.d(TAG, "Role updated : " + staff_role);
        Toast.makeText(this, "Role updated : " + staff_role, Toast.LENGTH_SHORT).show();
        finish();

    }
}
