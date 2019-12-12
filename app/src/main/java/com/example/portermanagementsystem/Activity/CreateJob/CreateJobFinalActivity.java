package com.example.portermanagementsystem.Activity.CreateJob;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.portermanagementsystem.Activity.AssignPorterActivity;
import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Notification.NotificationWorker;
import com.example.portermanagementsystem.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CreateJobFinalActivity extends AppCompatActivity {
    private static final String TAG = "CreateJobFinalActivity";
    RadioGroup radioGroupUrgency;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    TextView textViewUrgencyLevel;
    String role, StartTime, AcknowledgeTime, Status, AssignedTo = "", assignedBy="";
    String caseId = "";
    UserFirebaseInterface userFirebase = new UserFirebase();
    JobFirebaseInterface jobFirebase = new JobFirebase();
    Button btnCreateJob;
    JobControllerInterface jobController = new JobController();
    UserControllerInterface userController = new UserController();

    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job_final);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        radioGroupUrgency = findViewById(R.id.radioGroupJobUrgency);
        btnCreateJob = findViewById(R.id.btnNext3);
    }

    private void  addArrayList(){
        btnCreateJob.setEnabled(false);
        String FromStr = sharedpreferences.getString("fromLocation", null);
        String toStr = sharedpreferences.getString("toLocation", null);
        caseId = sharedpreferences.getString("caseId", null);
        String TypeJob = sharedpreferences.getString("typeOfJob", null);
        String Description = sharedpreferences.getString("description", null);
        int selectedId = radioGroupUrgency.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        if(!(radioButton == null)){
            role = sharedpreferences.getString("Role", null);
            String urgency =radioButton.getText().toString();
            int urgencyLevel = getUrgentLevel(urgency);
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());
            String time = df2.format(Calendar.getInstance().getTime());
            String CreatedOn = date;
            String CreatedBy = sharedpreferences.getString("StaffID", null);
            String CreatedTime = time;
            if (role.equals("Porter")){
                StartTime = time;
                AcknowledgeTime = time;
                Status = "Started";
                assignedBy = sharedpreferences.getString("StaffID", null);
                AssignedTo = sharedpreferences.getString("StaffID", null);

                userController.updatePorterStatus(sharedpreferences.getString("StaffID", null), "busy");
            }
            else {
                StartTime = "-";
                AcknowledgeTime="-";
                AssignedTo = "-";
                Status = "Pending";
                assignedBy = "-";
            }
            String PickUpTime = "-";
            String ArrivalTime = "-";
            String CompleteTime = "-";
            String Remark = "-";
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;
            Job newJob = new Job(FromStr, toStr, urgencyLevel, TypeJob, caseId, AssignedTo, CreatedOn, CreatedBy, CreatedTime, StartTime, PickUpTime, ArrivalTime, CompleteTime, Status, AcknowledgeTime, Remark, Description,assignedBy);
            final String JobID = jobController.insertJob(newJob);

            Toast.makeText(CreateJobFinalActivity.this, R.string.addSuccess, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder
                    .setTitle("Job Progression")
                    .setMessage("Job Created Successfully.")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //String JobID = databaseReference.child("Job").push().getKey();
                            //Start Notification
                            sendNotifications();
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove("fromLocation");
                            editor.remove("toLocation");
                            editor.remove("caseId");
                            editor.remove("typeOfJob");
                            editor.commit();

                            if (role.equals("Porter")){
                                Intent AssignPage = new Intent(getApplicationContext(), PorterDashboardActivity.class);
                                startActivity(AssignPage);
                                overridePendingTransition(0, 0);
                                finishAfterTransition();
                            }
                            else {
                                Intent AssignPage = new Intent(getApplicationContext(), AssignPorterActivity.class);
                                AssignPage.putExtra("JobID",JobID);
                                startActivity(AssignPage);
                                overridePendingTransition(0, 0);
                                finishAfterTransition();
                            }
                        }
                    })

                    .show();
        }else{
            textViewUrgencyLevel = findViewById(R.id.textViewUrgencyLevel);
            textViewUrgencyLevel.requestFocus();
            textViewUrgencyLevel.setError(getText(R.string.urgencyNull));
        }
    }
    public void createJob(View view){
        try {
            addArrayList();
        }
        catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }
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

    private void sendNotifications() {
        role = sharedpreferences.getString("Role", null);
        if (role.equals("Receptionist") || role.equals("Manager")) {
            // add some constraints
            Constraints constraints = new Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build();

            // data builder to differenciate the notifications
            Data myData2 = new Data.Builder()
                    .putString("X", "two")
                    .build();

            Data myData3 = new Data.Builder()
                    .putString("X", "three")
                    .build();

            Data myData4 = new Data.Builder()
                    .putString("X", "four")
                    .build();

            //first notification that fires after 1 secs
            //tag can be set as caseid
            String caseID = sharedpreferences.getString("caseId", null);
            // first notification that fires after 2 mins when item about to expire
            Log.d(TAG, "caseID : " +  caseID);
            OneTimeWorkRequest secondNotification =
                    new OneTimeWorkRequest.Builder(NotificationWorker.class)
                            .setConstraints(constraints)
                            .setInputData(myData2)
                            .addTag(caseID)
                            .setInitialDelay(1, TimeUnit.MINUTES) // warning notification fires after 2 mins from the launch of the worker
                            .build();

            // second notification that fires after 5 mins when item about to expire
            OneTimeWorkRequest thirdNotification =
                    new OneTimeWorkRequest.Builder(NotificationWorker.class)
                            .setConstraints(constraints)
                            .setInputData(myData3)
                            .addTag(caseID)
                            .setInitialDelay(3, TimeUnit.MINUTES) // warning notification fires after 5 mins from the launch of the worker
                            .build();

            OneTimeWorkRequest fourthNotification =
                    new OneTimeWorkRequest.Builder(NotificationWorker.class)
                            .setConstraints(constraints)
                            .setInputData(myData4)
                            .addTag(caseID)
                            .setInitialDelay(5, TimeUnit.MINUTES) // warning notification fires after 10 mins from the launch of the worker
                            .build();

            // chaining work
            WorkManager.getInstance()
                    .beginWith(secondNotification)
                    .then(thirdNotification)
                    .then(fourthNotification)
                    .enqueue();
        }
    }

}
