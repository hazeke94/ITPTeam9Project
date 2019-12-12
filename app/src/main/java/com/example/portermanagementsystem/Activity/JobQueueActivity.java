package com.example.portermanagementsystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Adapter.JobListAdapter;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.CallBack.UserCallBack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class JobQueueActivity extends AppCompatActivity {
    List<Job> jobList = new ArrayList<>();
    List<User> userList = new ArrayList<>();
    JobListAdapter mAdapter;
    RecyclerView recyclerViewJob;
    TextView textViewNoJob, numberOfAvailablePorterTV;
    Spinner spinnerFilterJobType;
    LinearLayout timeFilter, urgencyFilter;
    ImageView timeArrow, urgencyArrow;
    String filter="";

    JobFirebaseInterface jobFirebase = new JobFirebase();
    JobControllerInterface jobController = new JobController();
    UserFirebaseInterface userFirebase = new UserFirebase();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_queue);
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
        userFirebase.getAllOnlinePorter(new UserCallBack() {
            @Override
            public void onCallBack(User value) {

            }

            @Override
            public void onCallBack(List<User> value) {
                int availablePorter = value.size();
                numberOfAvailablePorterTV.setText(String.valueOf(availablePorter));
            }

            @Override
            public void onCallBack(String value) {

            }

            @Override
            public void onCallBack(Boolean value) {

            }
        });

        //Spinner Filter Job Type
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.SortAllItems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterJobType.setAdapter(adapter);
        allJob();
        spinnerFilterJobType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                allJob();
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    public void allJob() {
        jobFirebase.getAllJobByJobType(spinnerFilterJobType.getSelectedItem().toString(), new JobCallBack() {
            @Override
            public void onCallBack(Job value) {

            }

            @Override
            public void onCallBack(List<Job> value) {
                jobList = jobController.getQueueJob(value);
                if (jobList.size()>0){
                    if (filter.equals("urgency")){
                        setUrgencyFilter();
                    }else{
                        setTimeFilter();
                    }
                    recyclerViewJob.setVisibility(View.VISIBLE);
                    textViewNoJob.setVisibility(View.GONE);
                    mAdapter = new JobListAdapter(jobList, JobQueueActivity.this,"Queue",userList);
                    recyclerViewJob.setAdapter(mAdapter);
                }else{
                    textViewNoJob.setVisibility(View.VISIBLE);
                    recyclerViewJob.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCallBack(boolean value) {

            }

            @Override
            public void onCallBack(String value) {

            }
        });
    }

    protected void init(){
        textViewNoJob = findViewById(R.id.textViewNoJob);
        recyclerViewJob = findViewById(R.id.recyclerViewJob);
        spinnerFilterJobType = findViewById(R.id.spinnerFilterJobType);
        timeFilter = findViewById(R.id.timeFilter);
        timeArrow = findViewById(R.id.timeArrow);
        urgencyFilter = findViewById(R.id.urgencyFilter);
        urgencyArrow = findViewById(R.id.urgencyArrow);
        numberOfAvailablePorterTV = findViewById(R.id.numberOfAvailablePorterTV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(JobQueueActivity.this);
        recyclerViewJob.setLayoutManager(layoutManager);
    }

    // Filter job list by urgency in either descending or ascending order
    public void filterUrgency(View view) {
        setUrgencyFilter();
    }
    // Filter job list by Time in either descending or ascending order
    public void filterTime(View view) {
        setTimeFilter();
    }

    public void setTimeFilter(){
        filter="time";
        String arrowTimePosition = String.valueOf(timeArrow.getTag());
        urgencyArrow.setVisibility(View.INVISIBLE);
        if (timeArrow.getVisibility() == View.INVISIBLE){
            timeArrow.setVisibility(View.VISIBLE);
            timeArrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
            timeArrow.setTag("Up");
            Collections.sort(jobList, new Comparator<Job>(){
                @Override
                public int compare(Job o1, Job o2){
                    try {
                        Date date1 = new SimpleDateFormat("hh:mm:ss").parse(o1.getCreatedTime());
                        Date date2 = new SimpleDateFormat("hh:mm:ss").parse(o2.getCreatedTime());
                        return date2.compareTo(date1);
                    }catch(Exception ex){
                        return 0;
                    }
                }
            });
        }
        else {
            Collections.reverse(jobList);
            if ("Up".equals(arrowTimePosition)){
                timeArrow.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
                timeArrow.setTag("Down");
            }
            else {
                timeArrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                timeArrow.setTag("Up");
            }
        }
        mAdapter = new JobListAdapter(jobList, JobQueueActivity.this, "Queue",userList);
        recyclerViewJob.setAdapter(mAdapter);
    }

    private void setUrgencyFilter(){
        filter="urgency";
        timeArrow.setVisibility(View.INVISIBLE);
        String arrowUrgencyPosition = String.valueOf(urgencyArrow.getTag());
        if (urgencyArrow.getVisibility() == View.INVISIBLE){
            urgencyArrow.setVisibility(View.VISIBLE);
            urgencyArrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
            urgencyArrow.setTag("Up");
            Collections.sort(jobList, new Comparator<Job>(){
                @Override
                public int compare(Job o1, Job o2) {
                    return Integer.valueOf(o2.getJobUrgency()).compareTo(Integer.valueOf(o1.getJobUrgency()));
                }
            });
        }
        else{
            Collections.reverse(jobList);
            if ("Up".equals(arrowUrgencyPosition)){
                urgencyArrow.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
                urgencyArrow.setTag("Down");
            }
            else {
                urgencyArrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                urgencyArrow.setTag("Up");
            }
        }
        mAdapter = new JobListAdapter(jobList, JobQueueActivity.this, "Queue",userList);
        recyclerViewJob.setAdapter(mAdapter);

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(JobQueueActivity.this, ReceptionistDashboardActivity.class));
        finish();

    }
}
