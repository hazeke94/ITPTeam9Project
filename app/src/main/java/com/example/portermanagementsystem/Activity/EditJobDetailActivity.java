package com.example.portermanagementsystem.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.Service.LocationFirebase;
import com.example.portermanagementsystem.Service.LocationFirebaseInterface;
import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.CallBack.LocationCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class EditJobDetailActivity extends AppCompatActivity {
    private static final String TAG = "EditJobDetailActivity";
    Spinner spinnerJobType;
    AutoCompleteTextView spinnerFrom, spinnerTo;
    EditText editTextCaseID, editTextDescription;
    RadioGroup radioGroupJobUrgency;
    Job currentJob = new Job();
    RadioButton radioButtonEmergency, radioButtonUrgent, radioButtonNormal;
    public String fromLocation, toLocation, JobID, description, role,page;
    List<String> LocationToList, LocationFromList;
    ArrayAdapter<String> addressToAdapter, addressFromAdapter;
    int position = 0;
    JobControllerInterface jobController = new JobController();
    JobFirebaseInterface jobFirebase = new JobFirebase();
    LocationFirebaseInterface locationFirebase = new LocationFirebase();
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);
        init();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.AllItems, android.R.layout.simple_spinner_dropdown_item);
        spinnerJobType.setAdapter(adapter);
        sharedpreferences =getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        role = sharedpreferences.getString("Role", null);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            JobID = extras.getString("jobID");
            page = getIntent().getStringExtra("Page");


            //Get job detail
            jobFirebase.getJobDetail(JobID, new JobCallBack() {
                @Override
                public void onCallBack(Job value) {
                    currentJob = value;
                    if(value.getTypeOfJob().equals("Inpatient") ||value.getTypeOfJob().equals("Day Surgery") || value.getTypeOfJob().equals("Maternity")){
                        locationFirebase.getLocationBytype("admission", new LocationCallBack() {
                            @Override
                            public void onCallBack(List<String> value) {
                                LocationToList = value;
                                LocationFromList= value;
                                addressToAdapter = new ArrayAdapter<>(EditJobDetailActivity.this, android.R.layout.simple_spinner_item, LocationToList);
                                addressFromAdapter = new ArrayAdapter<>(EditJobDetailActivity.this, android.R.layout.simple_spinner_item, LocationFromList);
                                addressToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                addressFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                                spinnerFrom.setText(fromLocation);
                                spinnerTo.setText(toLocation);
                                spinnerFrom.setAdapter(addressFromAdapter);
                                spinnerTo.setAdapter(addressToAdapter);
                            }

                            @Override
                            public void onCallBack(boolean result) {

                            }
                        });
                    }else{
                        locationFirebase.getLocationBytype("adhoc", new LocationCallBack() {
                            @Override
                            public void onCallBack(List<String> value) {
                                LocationToList = value;
                                LocationFromList= value;
                                addressToAdapter = new ArrayAdapter<>(EditJobDetailActivity.this, android.R.layout.simple_spinner_item, LocationToList);
                                addressFromAdapter = new ArrayAdapter<>(EditJobDetailActivity.this, android.R.layout.simple_spinner_item, LocationFromList);
                                addressToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                addressFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                                spinnerFrom.setText(fromLocation);
                                spinnerTo.setText(toLocation);
                                spinnerFrom.setAdapter(addressFromAdapter);
                                spinnerTo.setAdapter(addressToAdapter);
                            }

                            @Override
                            public void onCallBack(boolean result) {

                            }
                        });
                    }

                    editTextCaseID.setText(value.getCaseID());
                    spinnerFrom.setText(value.getFromLocation());
                    spinnerTo.setText(value.getToLocation());
                    fromLocation = value.getFromLocation();
                    toLocation = value.getToLocation();
                    try{
                        description = value.getDescription();
                        if (!description.equals("-")){
                            editTextDescription.setText(description);
                        }
                    }
                    catch (Exception e){
                        Log.d(TAG, "Description is empty");
                    }
                    String typeOfJob = value.getTypeOfJob();
                    switch (typeOfJob) {
                        case "X-Ray":
                            spinnerJobType.setSelection(position);
                            break;
                        case "Labs":
                            spinnerJobType.setSelection(position + 1);
                            break;
                        case "Discharge":
                            spinnerJobType.setSelection(position + 2);
                            break;
                        case "Document":
                            spinnerJobType.setSelection(position + 3);
                            break;
                        case "Transport":
                            spinnerJobType.setSelection(position + 4);
                            break;
                        case "Inpatient":
                            spinnerJobType.setSelection(position + 5);
                            break;
                        case "Day Surgery":
                            spinnerJobType.setSelection(position + 6);
                            break;
                        case "Maternity":
                            spinnerJobType.setSelection(position + 7);
                            break;
                    }
                    int urgency = value.getJobUrgency();
                    if (urgency != 0) {
                        switch (urgency) {
                            case 1:
                                radioButtonEmergency.setChecked(true);
                                break;
                            case 2:
                                radioButtonUrgent.setChecked(true);
                                break;
                            case 3:
                                radioButtonNormal.setChecked(true);
                                break;
                        }
                    }
                }

                @Override
                public void onCallBack(List<Job> value) {

                }

                @Override
                public void onCallBack(boolean value) {

                }

                @Override
                public void onCallBack(String value) {

                }
            });
        }
    }

    public void saveJobDetails(View view) {
        updateJobDatabase();
        ProgressDialog mDialog = new ProgressDialog(EditJobDetailActivity.this);
        mDialog.setMessage("Updating job details. Please hold.");
        mDialog.setIndeterminate(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void updateJobDatabase() {
        String newCaseID = editTextCaseID.getText().toString();
        String typeText = spinnerJobType.getSelectedItem().toString();
        String fromText = spinnerFrom.getText().toString();
        String toText = spinnerTo.getText().toString();
        String descriptionNow = editTextDescription.getText().toString();
        int selectedId = radioGroupJobUrgency.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String jobUrgencyText = radioButton.getText().toString();
        int urgencyLevel = getUrgentLevel(jobUrgencyText);

        if(!jobController.validateCaseID(newCaseID, role)){
            spinnerTo.setError(getText(R.string.caseIDnull));
        }

        else if(!jobController.validateJobType(typeText)){
            spinnerTo.setError(getText(R.string.jobNull));
        }

        else if(!jobController.validateUrgency(jobUrgencyText)){
            spinnerTo.setError(getText(R.string.urgencyNull));
        }

        else if (jobController.validateToLocation(toLocation,fromText)){
            spinnerTo.setError(getText(R.string.sameLocation));
            return;
        }

        else if (jobController.validateFromLocation(fromLocation,toText)){
            spinnerTo.setError(getText(R.string.sameLocation));
            return;
        }


        if (currentJob.getStatus().equals("Pending")){
            currentJob.setStatus("Pending");
            currentJob.setJobID(JobID);
            currentJob.setCaseID(newCaseID);
            currentJob.setDescription(descriptionNow);
            currentJob.setJobUrgency(urgencyLevel);
            currentJob.setPickUpTime("-");
            currentJob.setStartTime("-");
            currentJob.setArrivalTime("-");
            currentJob.setCompleteTime("-");
            currentJob.setTypeOfJob(typeText);
            currentJob.setFromLocation(fromText);
            currentJob.setToLocation(toText);
        }
        else {
            if (!fromLocation.equals(fromText) && !currentJob.getStatus().equals("Assigned")) {
                currentJob.setStatus("Acknowledged");
                currentJob.setPickUpTime("-");
                currentJob.setStartTime("-");
            }
            else if(!toLocation.equals(toText) && currentJob.getStatus().equals("Arrived")){
                currentJob.setStatus("Pick Up");
                currentJob.setArrivalTime("-");
            }
        }
        currentJob.setJobID(JobID);
        currentJob.setCaseID(newCaseID);
        currentJob.setDescription(descriptionNow);
        currentJob.setJobUrgency(urgencyLevel);


        currentJob.setCompleteTime("-");
        currentJob.setTypeOfJob(typeText);
        currentJob.setFromLocation(fromText);
        currentJob.setToLocation(toText);

        jobController.updateJob(currentJob);


        if(role.equals("Porter")){
            finish();
        }
        else {
            Intent myIntent = new Intent(EditJobDetailActivity.this, JobDetailActivity.class);
            myIntent.putExtra("JobID", JobID);
            myIntent.putExtra("Page", page);
            startActivity(myIntent);
        }
    }

    public void init(){
        //initialize the fields in EditJob
        editTextCaseID = findViewById(R.id.editTextCaseID);
        editTextDescription = findViewById(R.id.editTextDescription);
        radioGroupJobUrgency = findViewById(R.id.radioGroupJobUrgency);
        radioButtonEmergency = findViewById(R.id.radioButtonEmergency);
        radioButtonUrgent = findViewById(R.id.radioButtonUrgent);
        radioButtonNormal = findViewById(R.id.radioButtonNormal);
        spinnerJobType = findViewById(R.id.spinnerJobType);

        radioGroupJobUrgency = findViewById(R.id.radioGroupJobUrgency);
        LocationToList = new ArrayList<>();
        LocationFromList = new ArrayList<>();
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);


    }

    private int getUrgentLevel(String urgency){
        switch(urgency){
            case "Emergency":
                return 1;
            case "Urgent":
                return 2;
            case "Normal":
                return 3;
        }
        return 0;
    }
}
