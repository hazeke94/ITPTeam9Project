package com.example.portermanagementsystem.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.CallBack.UserCallBack;

import java.util.ArrayList;
import java.util.List;

public class JobDetailActivity extends AppCompatActivity {
    private static final String TAG = "JobDetailActivity";
    public static final String MyPREFERENCES = "MyPrefs" ;
    private TextView textViewCaseID, textViewFrom, textViewTo, textViewCreatedTime, textViewCreatedBy, textViewAcknowledgeTime, textViewAcknowledgeBy, textViewStartTime, textViewPickUpTime, textViewArrivalTime, textViewCompleteTime, textViewRemark, textViewDescription;
    private CardView cardViewDetails;
    private LinearLayout descriptionLinearLayout;
    SharedPreferences sharedpreferences;
    List<User> userList = new ArrayList<>();
    private ImageView imageViewJobType, imageViewCreated, imageViewAck, imageViewReached, imageViewPickUp, imageViewArrived, imageViewCompleted, imageViewEdit;
    String JobID, staffName, staffID,role, page;
    UserFirebaseInterface userFirebase = new UserFirebase();
    JobFirebaseInterface jobFirebase = new JobFirebase();

    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        init();
        userFirebase.getAllUser(new UserCallBack() {
            @Override
            public void onCallBack(User value) {

            }

            @Override
            public void onCallBack(List<User> value) {
                userList = value;
            }

            @Override
            public void onCallBack(String value) {

            }

            @Override
            public void onCallBack(Boolean value) {

            }
        });
        jobFirebase.getJobDetail(JobID, new JobCallBack(){
            @Override
            public void onCallBack(Job value) {
                if (value.getStatus().equals("Completed") ||value.getStatus().equals("Cancelled")){
                    imageViewEdit.setVisibility(View.GONE);
                }
                textViewAcknowledgeBy.setText(value.getAssigned());
                textViewCreatedBy.setText(value.getCreatedBy());
                textViewCaseID.setText(value.getCaseID());
                textViewFrom.setText(value.getFromLocation());
                textViewTo.setText(value.getToLocation());
                textViewCreatedTime.setText(value.getCreatedTime());
                textViewPickUpTime.setText(value.getPickUpTime());
                textViewArrivalTime.setText(value.getArrivalTime());
                textViewCompleteTime.setText(value.getCompleteTime());
                try {
                    String jobDescription = value.getDescription();
                    if (jobDescription.equals("-") || jobDescription.equals("")){
                        descriptionLinearLayout.setVisibility(View.GONE);
                    }
                    else {
                        descriptionLinearLayout.setVisibility(View.VISIBLE);
                        textViewDescription.setText(jobDescription);
                    }
                }
                catch(Exception e) {
                    descriptionLinearLayout.setVisibility(View.GONE);
                }

                if (value.getAssigned().equals("-")){
                    textViewAcknowledgeBy.setText(value.getAssigned());
                }
                else {
                    for (User user:userList){
                        if(user.getStaffID().equals(value.getAssigned())){
                            textViewAcknowledgeBy.setText(user.getName());
                            break;
                        }
                    }
                }

                if (value.getCreatedBy().equals("-")){
                    textViewCreatedBy.setText(value.getCreatedBy());
                }
                else {
                    for (User user:userList){
                        if(user.getStaffID().equals(value.getCreatedBy())){
                            textViewCreatedBy.setText(user.getName());
                            break;
                        }
                    }
                }

                textViewStartTime.setText(value.getStartTime());
                textViewAcknowledgeTime.setText(value.getAcknowledgeTime());
                textViewRemark.setText(value.getRemark());
                toggleCircle(textViewCreatedTime, imageViewCreated);
                toggleCircle(textViewAcknowledgeTime, imageViewAck);
                toggleCircle(textViewStartTime, imageViewReached);
                toggleCircle(textViewPickUpTime, imageViewPickUp);
                toggleCircle(textViewArrivalTime, imageViewArrived);
                toggleCircle(textViewCompleteTime, imageViewCompleted);

                String jobStatusNow = value.getStatus();
                if (jobStatusNow.equals("Cancelled")){
                    cardViewDetails.setBackgroundColor(Color.parseColor("#b2bec3"));
                }
                else {
                    int jobUrgency = value.getJobUrgency();
                    switch (jobUrgency){
                        case 1:
                            cardViewDetails.setBackgroundColor(Color.parseColor("#ff7675"));
                            break;
                        case 2:
                            cardViewDetails.setBackgroundColor(Color.parseColor("#fdcb6e"));
                            break;
                        case 3:
                            cardViewDetails.setBackgroundColor(Color.parseColor("#87CEFA"));
                            break;
                    }
                }

                String jobType = value.getTypeOfJob();
                try {
                    switch (jobType){
                        case "X-Ray":
                            imageViewJobType.setImageResource(R.drawable.ic_skeleton);
                            break;
                        case "Labs":
                            imageViewJobType.setImageResource(R.drawable.ic_lab);
                            break;
                        case "Discharge":
                            imageViewJobType.setImageResource(R.drawable.ic_discharge_black_24dp);
                            break;
                        case "Document":
                            imageViewJobType.setImageResource(R.drawable.ic_assignment_black_24dp);
                            break;
                        case "Transport":
                            imageViewJobType.setImageResource(R.drawable.ic_transport_black_24dp);
                            break;
                        case "Inpatient":
                            imageViewJobType.setImageResource(R.drawable.ic_inpatient_black_24dp);
                            break;
                        case "Day Surgery":
                            imageViewJobType.setImageResource(R.drawable.ic_surgery);
                            break;
                        case "Maternity":
                            imageViewJobType.setImageResource(R.drawable.ic_pregnant_woman_black_24dp);
                            break;
                    }
                }
                catch (Exception e){
                    Log.d(TAG, "No job Type");
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

    public void toggleCircle(TextView tv, ImageView iv) {
        if (tv.getText().equals("-")) {
            iv.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        } else {
            iv.setImageResource(R.drawable.ic_check_circle_white_24dp);
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        jobFirebase.isCompleted(JobID, new JobCallBack() {
            @Override
            public void onCallBack(Job value) {

            }

            @Override
            public void onCallBack(List<Job> value) {

            }

            @Override
            public void onCallBack(boolean value) {
                MenuItem item = menu.findItem(R.id.delete);
                item.setVisible(value);
            }

            @Override
            public void onCallBack(String value) {

            }
        });

        return true;
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
        Intent intent = new Intent(JobDetailActivity.this, RemarkActivity.class);
        intent.putExtra("JobID", JobID);
        intent.putExtra("staffName", staffName);
        intent.putExtra("status", "Cancelled");
        startActivity(intent);
    }
    protected void init(){
        imageViewEdit = findViewById(R.id.imageViewEdit);
        imageViewJobType = findViewById(R.id.imageViewJobType);
        imageViewCreated = findViewById(R.id.imageViewCreated);
        imageViewAck = findViewById(R.id.imageViewAck);
        imageViewReached = findViewById(R.id.imageViewReached);
        imageViewPickUp = findViewById(R.id.imageViewPickUp);
        imageViewArrived = findViewById(R.id.imageViewArrived);
        imageViewCompleted = findViewById(R.id.imageViewCompleted);
        textViewCaseID = findViewById(R.id.textViewCaseID);
        textViewFrom = findViewById(R.id.textViewFrom);
        textViewTo = findViewById(R.id.textViewTo);
        textViewCreatedTime = findViewById(R.id.textViewCreatedTime);
        textViewCreatedBy = findViewById(R.id.textViewCreatedBy);
        textViewAcknowledgeTime = findViewById(R.id.textViewAcknowledgeTime);
        textViewAcknowledgeBy = findViewById(R.id.textViewAcknowledgeBy);
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewPickUpTime = findViewById(R.id.textViewPickUpTime);
        textViewArrivalTime = findViewById(R.id.textViewArrivalTime);
        textViewCompleteTime = findViewById(R.id.textViewCompleteTime);
        textViewRemark = findViewById(R.id.textViewRemark);
        textViewDescription = findViewById(R.id.textViewDescription);
        descriptionLinearLayout = findViewById(R.id.descriptionLinearLayout);
        cardViewDetails = findViewById(R.id.cardViewDetails);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        staffID = sharedpreferences.getString("StaffID", null);
        role = sharedpreferences.getString("Role", null);
        Intent intent = getIntent();
        JobID = intent.getStringExtra("JobID");
    }

    public void editDetail(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        page = getIntent().getStringExtra("Page");
        Intent intent = new Intent(this, EditJobDetailActivity.class);
        intent.putExtra("jobID", JobID);
        intent.putExtra("Page", page);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        page = getIntent().getStringExtra("Page");
        if (page == null || role.equals("Porter")){
            super.onBackPressed();
            finish();
        }
        else {
            switch (page) {
                case "Queue":
                    super.onBackPressed();
                    startActivity(new Intent(JobDetailActivity.this, JobQueueActivity.class));
                    finish();
                    break;
                case "Ongoing":
                    super.onBackPressed();
                    startActivity(new Intent(JobDetailActivity.this, JobOngoingActivity.class));
                    finish();
                    break;
                case "History":
                    super.onBackPressed();
                    startActivity(new Intent(JobDetailActivity.this, JobHistoryActivity.class));
                    finish();
                    break;
            }
        }
    }

}