package com.example.portermanagementsystem.Activity.CreateJob;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Activity.Scanner.BarcodeJobActivity;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Service.LocationFirebase;
import com.example.portermanagementsystem.Service.LocationFirebaseInterface;
import com.example.portermanagementsystem.CallBack.LocationCallBack;
import com.example.portermanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class CreateJob1Activity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    AutoCompleteTextView spinnerFrom, spinnerTo;
    EditText editTextCaseID, editTextDescription;
    TextView textViewCaseID;
    ArrayAdapter<String> addressToAdapter, addressFromAdapter;
    String role, errorMessage, typeOfJob = "";
    LocationFirebaseInterface locationFirebase = new LocationFirebase();
    JobControllerInterface jobController = new JobController();
    List<String> location = new ArrayList<>();

    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job_1);
        init();

        role = sharedpreferences.getString("Role", null);

        if (role.equals("Porter")){
            editTextCaseID.setHint("");
            textViewCaseID.setText(R.string.case_id);
            typeOfJob = typeOfJob.replace(typeOfJob, "admission");
        }
        else {
            editTextCaseID.setHint(R.string.hint_bed_ward);
            textViewCaseID.setText(R.string.identifier);
            typeOfJob = typeOfJob.replace(typeOfJob, "adhoc");
        }
        locationFirebase.getLocationBytype(typeOfJob, new LocationCallBack() {
            @Override
            public void onCallBack(List<String> value) {
                addressToAdapter = new ArrayAdapter<String>(CreateJob1Activity.this, android.R.layout.simple_spinner_dropdown_item, value);
                addressFromAdapter = new ArrayAdapter<String>(CreateJob1Activity.this, android.R.layout.simple_spinner_dropdown_item, value);
                location = value;
                spinnerFrom.setThreshold(1);
                spinnerTo.setThreshold(1);
                spinnerFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View arg0) {
                        spinnerFrom.showDropDown();
                    }
                });

                spinnerTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View arg0) {
                        spinnerTo.showDropDown();
                    }
                });
                spinnerFrom.setAdapter(addressFromAdapter);
                spinnerTo.setAdapter(addressToAdapter);
            }

            @Override
            public void onCallBack(boolean result) {

            }
        });

        //retrieve CaseID is available
        String caseID = getIntent().getStringExtra("CaseID");
        if(caseID != null){
            editTextCaseID.setText(caseID);

            //set From/To
            spinnerFrom.setText(sharedpreferences.getString("from", "L1, BO Business Office"));
            spinnerTo.setText(sharedpreferences.getString("to", "L1, SF St Francis Ward, 1"));
        }
        else {
            if (role.equals("Porter")) {
                spinnerFrom.setText("L1, BO Business Office");
            }
        }

        editTextCaseID.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (editTextCaseID.getRight() - editTextCaseID.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        //save job inputs/selected values
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("from", spinnerFrom.getText().toString().trim());
                        editor.putString("to", spinnerTo.getText().toString().trim());
                        editor.apply();

                        //Insert barcode scanner function here
                        Intent intent = new Intent(CreateJob1Activity.this, BarcodeJobActivity.class);
                        startActivityForResult(intent, 1);
                    }
                }
                return false;
            }
        });
    }

    //Initialise value
    private void init(){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editTextCaseID = findViewById(R.id.editTextCaseID);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        textViewCaseID = findViewById(R.id.textViewCaseID);
        editTextDescription = findViewById(R.id.editTextDescription);
    }

    //Button Next Page Clicked
    public void nextPage(View view){

        String fromLocation = spinnerFrom.getText().toString();
        String toLocation = spinnerTo.getText().toString();
        String CaseID = editTextCaseID.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        boolean validation = true;



        if(!jobController.validateCaseID(CaseID, role)){
            editTextCaseID.setError(jobController.errorMessageCaseID(CaseID));
            editTextCaseID.requestFocus();
            editTextCaseID.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_camera_alt_black_24dp, 0);
            validation =false;
        }
        else {
            editTextCaseID.setError(null);
            editTextCaseID.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_camera_alt_black_24dp, 0);
            validation =true;
        }

        if(jobController.validateDescription(description)){
            description = description.replace(" ", "-");
        }

        if(jobController.validateLocation(fromLocation)){
            spinnerFrom.requestFocus();
            spinnerFrom.setError(getText(R.string.locationNull));
            validation =false;
        }


        if(jobController.validateLocation(toLocation)){
            spinnerTo.requestFocus();
            spinnerTo.setError(getText(R.string.locationNull));
            validation =false;
        }
        if(!jobController.validateCorrectToLocation(toLocation,location)){
            spinnerTo.requestFocus();
            spinnerTo.setError(getText(R.string.locationNull));
            validation =false;
        }
        Log.d("Location", String.valueOf(location.size()));
        if(!jobController.validateCorrectFromLocation(fromLocation,location)){
            spinnerTo.requestFocus();
            spinnerTo.setError(getText(R.string.locationNull));
            validation =false;
        }
        if(jobController.validateFromLocation(fromLocation,toLocation)){
            spinnerTo.setError(getText(R.string.sameLocation));
            //Toast.makeText(getApplicationContext(), "Pick up point should be different from destination point", Toast.LENGTH_SHORT).show();
            validation =false;
        }
        Log.d("Create1", String.valueOf(validation));
        if(!validation){
            spinnerTo.setError(getText(R.string.errorDefault));
        }
        else {
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;
            Intent nextPage = new Intent(getApplicationContext(), CreateJob2Activity.class);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("fromLocation", fromLocation);
            editor.putString("toLocation", toLocation);
            editor.putString("caseId", CaseID);
            editor.putString("description", description);
            editor.apply();
            startActivity(nextPage);

        }
    }

    public void iconBack(View view) {
        editTextCaseID.setError(null);
        editTextCaseID.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_camera_alt_black_24dp, 0);
    }
}
