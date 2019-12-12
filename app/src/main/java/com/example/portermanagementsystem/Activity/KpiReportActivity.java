package com.example.portermanagementsystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.portermanagementsystem.Adapter.ReportListAdapter;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class KpiReportActivity extends AppCompatActivity {
    private static final String TAG = "KpiReportActivity";
    List<Job> jobList = new ArrayList<>();
    List<Job> smileyList = new ArrayList<>();
    List<Job> sadList = new ArrayList<>();
    ImageView imageViewFace;
    LinearLayout linearLayoutFace;
    RecyclerView recyclerViewJob;
    ReportListAdapter mAdapter;
    TextView textViewNoJob;
    JobControllerInterface jobController = new JobController();
    JobFirebaseInterface jobFirebase = new JobFirebase();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpi_report);
        init();
        getList();
    }

    public void changeFace(View view) {
        if(imageViewFace.getTag().equals("smiley")){
            imageViewFace.setImageResource(R.drawable.ic_sentiment_neutral_black_24dp);
            imageViewFace.setTag("sad");
            getList();
        }
        else if (imageViewFace.getTag().equals("sad")){
            imageViewFace.setImageResource(R.drawable.ic_sentiment_satisfied_black_24dp);
            imageViewFace.setTag("smiley");
            getList();
        }
    }

    public void getList() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("Type");
        final String monthSpinner = intent.getStringExtra("Month");
        final String yearSpinner = intent.getStringExtra("Year");
        setTitle(type);
        jobFirebase.getAllJobByJobType(type, new JobCallBack() {
            @Override
            public void onCallBack(Job value) {

            }

            @Override
            public void onCallBack(List<Job> value) {
                jobList.clear();
                smileyList= (List<Job>) jobController.getKpiReport(value, String.valueOf(monthSpinner), String.valueOf(yearSpinner)).get(0);
                sadList = (List<Job>) jobController.getKpiReport(value, String.valueOf(monthSpinner), String.valueOf(yearSpinner)).get(1);

                if(imageViewFace.getTag().equals("smiley")){
                    smileyList.addAll(sadList);
                    jobList.addAll(smileyList);
                }
                else if(imageViewFace.getTag().equals("sad")){
                    sadList.addAll(smileyList);
                    jobList.addAll(sadList);
                }

                if(jobList.size()>0){
                    recyclerViewJob.setVisibility(View.VISIBLE);
                    textViewNoJob.setVisibility(View.GONE);
                    mAdapter = new ReportListAdapter(jobList, KpiReportActivity.this);
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
        recyclerViewJob = findViewById(R.id.recyclerViewKPI);
        textViewNoJob = findViewById(R.id.textViewNoJob);
        imageViewFace = findViewById(R.id.imageViewFace);
        linearLayoutFace = findViewById(R.id.linearLayoutFace);
        LinearLayoutManager layoutManager = new LinearLayoutManager(KpiReportActivity.this);
        recyclerViewJob.setLayoutManager(layoutManager);
    }
}
