package com.example.portermanagementsystem.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.portermanagementsystem.Activity.Scanner.BarcodeJobActivity;
import com.example.portermanagementsystem.Activity.Scanner.NurseScannerActivity;
import com.example.portermanagementsystem.Activity.Scanner.StaffIDScannerActivity;
import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;

public class CreateUserActivity extends AppCompatActivity {
    private static final String TAG = "CreateUser";
    RadioGroup radioGroupGender;
    String status = "online";
    String fcmtoken = "";
    EditText editTextStaffID, editTextStaffName, editTextStaffContact, editTextStaffEmail;
    UserFirebaseInterface userFirebase = new UserFirebase();
    Button btnCreateUser;
    UserControllerInterface userController = new UserController();
    Spinner spinnerStaffRole;
    SharedPreferences sharedpreferences;



    private static final String[] Staffroles = {"Manager", "Receptionist", "Porter"};

    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        init();
        String BarcodeID = getIntent().getStringExtra("barcode_id");
        if(BarcodeID != null){
            Log.d("BarcodeID",BarcodeID);
            editTextStaffID.setText(BarcodeID);
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
//                        save user inputs/selected values
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putString("staffName", editTextStaffName.getText().toString().trim());
//                        editor.putString("staffContact", editTextStaffContact.getText().toString().trim());
//                        editor.putString("staffEmail", editTextStaffEmail.getText().toString().trim());
//                        editor.apply();

                        //Insert barcode scanner function here
                        Intent intent = new Intent(CreateUserActivity.this, StaffIDScannerActivity.class);
                        startActivityForResult(intent, 1);
                    }
                }
                return false;
            }
        });
    }

    //Initialise value
    private void init(){
        editTextStaffID = findViewById(R.id.editTextStaffID);
        editTextStaffName = findViewById(R.id.editTextStaffName);
        editTextStaffContact = findViewById(R.id.editTextStaffContact);
        editTextStaffEmail = findViewById(R.id.editTextStaffEmail);
        spinnerStaffRole = findViewById(R.id.spinnerStaffRole);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(CreateUserActivity.this,
                android.R.layout.simple_spinner_item,Staffroles);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStaffRole.setAdapter(adapter);


    }

    private void  addArrayList(){
        String staffID = editTextStaffID.getText().toString();
        String staffName = editTextStaffName.getText().toString();
        String staffEmail = editTextStaffEmail.getText().toString();
        String staffContact = editTextStaffContact.getText().toString();
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String gender =radioButton.getText().toString();
        String role = spinnerStaffRole.getSelectedItem().toString();
        if (staffID.equals("")){
            Toast.makeText(CreateUserActivity.this, R.string.cannotEmpty, Toast.LENGTH_SHORT).show();
        }
        else if (staffName.equals("")){
            Toast.makeText(CreateUserActivity.this,"Staff name cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else
        {
            btnCreateUser.setEnabled(false);
            User newUser = new User(staffID,staffName,gender,staffContact,role,fcmtoken,status,staffEmail);
            final String StaffID = userController.insertUser(newUser);
            Toast.makeText(CreateUserActivity.this, R.string.addUserSuccess, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,StaffActivity.class));

        }

    }

    public void createUser(View view){
        try {
            addArrayList();
        }
        catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }
    }

    public void iconBack(View view) {
        editTextStaffID.setError(null);
        editTextStaffID.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_camera_alt_black_24dp, 0);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(CreateUserActivity.this, StaffListActivity.class));
        finish();
    }

}
