package com.example.portermanagementsystem.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.Activity.Scanner.LoginScannerActivity;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.Notification.NotificationService;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Model.User;

import com.example.portermanagementsystem.CallBack.UserCallBack;


import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String MyPREFERENCES = "MyPrefs" ;
    EditText editTextStaffID;
    SharedPreferences sharedpreferences;
    NotificationService ns = new NotificationService();
    UserFirebaseInterface userFirebase = new UserFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sharedpreferences.contains("StaffID")){
            if(sharedpreferences.getString("Role", null).equals("Porter")){
                Intent intent = new Intent(getApplicationContext(), PorterDashboardActivity.class);
                startActivity(intent);
            }
            else{
                Intent intphto = new Intent(getApplicationContext(), ReceptionistDashboardActivity.class);
                startActivity(intphto);
            }
            finish();
        }

        setContentView(R.layout.activity_login);
        editTextStaffID = findViewById(R.id.editTextStaffID);
        String staffId = getIntent().getStringExtra("barcode_id");
        if(staffId != null) {
            editTextStaffID.setText(staffId);
        }

        editTextStaffID.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (editTextStaffID.getRight() - editTextStaffID.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        //save job inputs/selected values
                        //Insert barcode scanner function here
                        Intent intent = new Intent(LoginActivity.this, LoginScannerActivity.class);
                        startActivityForResult(intent, 1);
                    }
                }
                return false;
            }
        });
    }

    public void login(View view) {
        final String staffID = editTextStaffID.getText().toString();
        if (staffID.equals("")){
            editTextStaffID.setError(getText(R.string.cannotEmpty));
        }
        else {
            userFirebase.getUserDetail(staffID, new UserCallBack() {
                @Override
                public void onCallBack(User value) {
                    User getUser = value;
                    if(getUser == null){
                        Toast.makeText(LoginActivity.this, R.string.invalidUser, Toast.LENGTH_SHORT).show();
                    }else{
                        if (getUser.getFCMToken().equals("")){
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("StaffID", staffID);
                            editor.putString("Role", getUser.getRole());
                            editor.commit();
                            Log.d(TAG, sharedpreferences.getString("StaffID", null));
                            userFirebase.updateFcmToken(staffID);
                            Toast.makeText(LoginActivity.this, R.string.loginSuccess, Toast.LENGTH_SHORT).show();
                            if (getUser.getRole().equals("Porter")) {
                                Intent intent = new Intent(getApplicationContext(), PorterDashboardActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intphto = new Intent(getApplicationContext(), ReceptionistDashboardActivity.class);
                                startActivity(intphto);
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, R.string.anotherDevice, Toast.LENGTH_SHORT).show();
                        }
                    }
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
    }

    public void iconBack(View view) {
        editTextStaffID.setError(null);
        editTextStaffID.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_camera_alt_black_24dp, 0);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
