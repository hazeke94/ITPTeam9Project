package com.example.portermanagementsystem.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.Adapter.JobListAdapter;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class JobHistoryActivity extends AppCompatActivity {
    private static final String TAG = "JobHistoryActivity";
    List<User> userList = new ArrayList<>();
    public static final String MyPREFERENCES = "MyPrefs" ;
    List<Job> jobList = new ArrayList<>();
    List<Job> filterList = new ArrayList<>();
    JobListAdapter mAdapter;
    SharedPreferences sharedpreferences;
    String role, staffID, filter ="";
    TextView textViewNoJob;
    RecyclerView recyclerViewJob;
    Spinner spinnerFilterJobType, spinnerByMonth;
    LinearLayout timeFilter, urgencyFilter;
    ImageView dateArrow, urgencyArrow;
    JobFirebaseInterface jobFirebase = new JobFirebase();
    JobControllerInterface jobController = new JobController();

    UserFirebaseInterface userFirebase = new UserFirebase();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_history);
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
        init();
        allJob();

        //Spinner Filter Job Type
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.SortAllItems, android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterJobType.setAdapter(adapter);

        //Spinner By Month
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.month, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerByMonth.setAdapter(monthAdapter);
        Calendar c = Calendar.getInstance();
        int todayMonth = c.get(Calendar.MONTH)+1;
        spinnerByMonth.setSelection(todayMonth);

        spinnerFilterJobType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                getFilterList(spinnerFilterJobType.getSelectedItem().toString(), spinnerByMonth.getSelectedItem().toString());
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        spinnerByMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                getFilterList(spinnerFilterJobType.getSelectedItem().toString(), spinnerByMonth.getSelectedItem().toString());

            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }
    protected void init(){
        textViewNoJob = findViewById(R.id.textViewNoJob);
        recyclerViewJob = findViewById(R.id.recyclerViewJob);
        spinnerFilterJobType = findViewById(R.id.spinnerFilterJobType);
        spinnerByMonth = findViewById(R.id.spinnerByMonth);
        timeFilter = findViewById(R.id.timeFilter);
        dateArrow = findViewById(R.id.dateArrow);
        urgencyFilter = findViewById(R.id.urgencyFilter);
        urgencyArrow = findViewById(R.id.urgencyArrow);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        role = sharedpreferences.getString("Role", null);
        staffID = sharedpreferences.getString("StaffID", null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(JobHistoryActivity.this);
        recyclerViewJob.setLayoutManager(layoutManager);
    }

    public void allJob() {
        jobFirebase.getAllJobByUser(role, staffID, new JobCallBack() {
            @Override
            public void onCallBack(Job value) {

            }

            @Override
            public void onCallBack(List<Job> value) {
                 if(value.size()>0){
                     jobList = jobController.getAllHistory(value);
                 } else{
                 recyclerViewJob.setVisibility(View.GONE);
                 textViewNoJob.setVisibility(View.VISIBLE);
                }

                getFilterList(spinnerFilterJobType.getSelectedItem().toString(), spinnerByMonth.getSelectedItem().toString());
                if (filter.equals("urgency")){
                    setUrgencySort();
                }else{
                    setTimeSort();
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

    // Filter job list by urgency in either descending or ascending order
    public void sortUrgency(View view) {
        setUrgencySort();
    }
    // Filter job list by Time in either descending or ascending order
    public void sortTime(View view) {
        setTimeSort();
    }

    public void getFilterList(String type, String month){
        filterList = jobController.getJobHistory(jobList, type, month);
        if (filterList.size() >0) {
            recyclerViewJob.setVisibility(View.VISIBLE);
            textViewNoJob.setVisibility(View.GONE);
            mAdapter = new JobListAdapter(filterList, JobHistoryActivity.this, "History", userList);
            recyclerViewJob.setAdapter(mAdapter);
        }else{
            recyclerViewJob.setVisibility(View.GONE);
            textViewNoJob.setVisibility(View.VISIBLE);
        }

    }

    public void setTimeSort(){
        filter="time";
        String arrowTimePosition = String.valueOf(dateArrow.getTag());
        urgencyArrow.setVisibility(View.INVISIBLE);
        if (dateArrow.getVisibility() == View.INVISIBLE){
            dateArrow.setVisibility(View.VISIBLE);
            dateArrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
            dateArrow.setTag("Up");
            Collections.sort(filterList, new Comparator<Job>(){
                @Override
                public int compare(Job o1, Job o2){
                    try {
                        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(o1.getCreatedOn());
                        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(o2.getCreatedOn());
                        return date2.compareTo(date1);
                    }catch(Exception ex){
                        Log.d(TAG, ex.getMessage());
                        return 0;
                    }
                }
            });
        }
        else {
            Collections.reverse(filterList);
            if ("Up".equals(arrowTimePosition)){
                dateArrow.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
                dateArrow.setTag("Down");
            }
            else {
                dateArrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                dateArrow.setTag("Up");
            }
        }
        mAdapter = new JobListAdapter(filterList, JobHistoryActivity.this, "History",userList);
        recyclerViewJob.setAdapter(mAdapter);
    }

    private void setUrgencySort(){
        filter="urgency";
        dateArrow.setVisibility(View.INVISIBLE);
        String arrowUrgencyPosition = String.valueOf(urgencyArrow.getTag());
        if (urgencyArrow.getVisibility() == View.INVISIBLE){
            urgencyArrow.setVisibility(View.VISIBLE);
            urgencyArrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
            urgencyArrow.setTag("Up");
            Collections.sort(filterList, new Comparator<Job>(){
                @Override
                public int compare(Job o1, Job o2) {
                    return Integer.valueOf(o2.getJobUrgency()).compareTo(Integer.valueOf(o1.getJobUrgency()));
                }
            });
        }
        else{
            Collections.reverse(filterList);
            if ("Up".equals(arrowUrgencyPosition)){
                urgencyArrow.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
                urgencyArrow.setTag("Down");
            }
            else {
                urgencyArrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                urgencyArrow.setTag("Up");
            }
        }
        recyclerViewJob.setVisibility(View.VISIBLE);
        textViewNoJob.setVisibility(View.GONE);
        mAdapter = new JobListAdapter(filterList, JobHistoryActivity.this, "History",userList);
        recyclerViewJob.setAdapter(mAdapter);
    }

    public void onBackPressed()
    {
        if (role.equals("Porter")){
            super.onBackPressed();
            startActivity(new Intent(JobHistoryActivity.this, PorterDashboardActivity.class));
            finish();
        }
        else {
            super.onBackPressed();
            startActivity(new Intent(JobHistoryActivity.this, ReceptionistDashboardActivity.class));
            finish();
        }

    }
}
