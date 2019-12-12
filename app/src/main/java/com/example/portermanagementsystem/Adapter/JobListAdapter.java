package com.example.portermanagementsystem.Adapter;


import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.portermanagementsystem.Activity.AssignPorterActivity;
import com.example.portermanagementsystem.Activity.JobDetailActivity;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.JobListViewHolder>{
    public static final String MyPREFERENCES = "MyPrefs" ;
    private static final String TAG = "JobListAdapter";

    List<User> userList = new ArrayList<>();
    private Context context;
    String page;
    String staffName;
    List<Job> mJobs;
//    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private int position = -1;
    SharedPreferences sharedPreferences ;

    public JobListAdapter(List<Job> jobList, Context context,String page, List<User> userList){
        this.userList = userList;
        this.context = context;
        this.mJobs = jobList;
        this.page = page;
        mInflater = LayoutInflater.from(context);
    }
    private final LayoutInflater mInflater;

    @Override
    public JobListAdapter.JobListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.job_list_item, parent, false);
        return new JobListAdapter.JobListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final JobListViewHolder jobListViewHolder, int position) {
        if (mJobs != null) {
            final Job job = mJobs.get(jobListViewHolder.getAdapterPosition());
            //Set information of job in the job list
            jobListViewHolder.textViewCaseID.setText(job.getCaseID());
            String jobType = job.getTypeOfJob();
            try {
                switch (jobType){
                    case "X-Ray":
                        jobListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_skeleton);
                        break;
                    case "Labs":
                        jobListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_lab);
                        break;
                    case "Discharge":
                        jobListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_discharge_black_24dp);
                        break;
                    case "Document":
                        jobListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_assignment_black_24dp);
                        break;
                    case "Transport":
                        jobListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_transport_black_24dp);
                        break;
                    case "Inpatient":
                        jobListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_inpatient_black_24dp);
                        break;
                    case "Day Surgery":
                        jobListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_surgery);
                        break;
                    case "Maternity":
                        jobListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_pregnant_woman_black_24dp);
                        break;
                }
            }
            catch (Exception e){
                Log.d(TAG, "No job type");
            }
            jobListViewHolder.textViewCreatedTime.setText(String.valueOf(job.getCreatedTime()));
            jobListViewHolder.textViewStartTime.setText(String.valueOf(job.getStartTime()));
            jobListViewHolder.textViewCompletedTime.setText(String.valueOf(job.getCompleteTime()));
            jobListViewHolder.textViewDateCreated.setText(String.valueOf(job.getCreatedOn()));
            if(job.getAssigned().equals("") || job.getAssigned().equals("-")){
                jobListViewHolder.textViewAssigned.setText("-");
            }else{
                for(User user: userList){
                    if(user.getStaffID().equals(job.getAssigned())){
                        Log.d(TAG, "Porter name assigned " + user.getName());
                        jobListViewHolder.textViewAssigned.setText(user.getName());
                        staffName =user.getName();
                        break;
                    }
                }
            }

            String jobStatusNow = job.getStatus();
            if (jobStatusNow.equals("Cancelled")){
                jobListViewHolder.cardViewJob.setBackgroundColor(Color.parseColor("#b2bec3"));
            }
            else {
                int jobUrgency = job.getJobUrgency();
                try{
                    switch (jobUrgency){
                        case 1:
                            jobListViewHolder.cardViewJob.setBackgroundColor(Color.parseColor("#ff7675"));
                            break;
                        case 2:
                            jobListViewHolder.cardViewJob.setBackgroundColor(Color.parseColor("#fdcb6e"));
                            break;
                        case 3:
                            jobListViewHolder.cardViewJob.setBackgroundColor(Color.parseColor("#87CEFA"));
                            break;
                    }
                }
                catch (Exception e){
                    Log.d(TAG, "NullPointerException");
                }
            }
        }
        else {
            jobListViewHolder.textViewCaseID.setText("No Porter Available");
        }
    }

    @Override
    public int getItemCount() {
        if (mJobs != null){
            return mJobs.size();
        }
        else {
            return 0;
        }
    }

    public class JobListViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewCaseID, textViewCreatedTime, textViewAssigned, textViewStartTime, textViewCompletedTime, textViewDateCreated;
        private final ImageView imageViewAssigned, jobTypeIcon, buttonAssign, startTimeIV, completedTimeIV, dateImageView;
        private final CardView cardViewJob;
        private static final long CLICK_TIME_INTERVAL = 300;
        private long mLastClickTime = System.currentTimeMillis();

        private JobListViewHolder(View itemView) {
            super(itemView);
            sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            textViewCaseID = itemView.findViewById(R.id.textViewCaseID);
            jobTypeIcon = itemView.findViewById(R.id.jobTypeIcon);
            textViewAssigned = itemView.findViewById(R.id.textViewAssigned);
            textViewCreatedTime = itemView.findViewById(R.id.textViewCreatedTime);
            textViewStartTime = itemView.findViewById(R.id.textViewStartTime);
            textViewCompletedTime = itemView.findViewById(R.id.textViewCompletedTime);
            textViewDateCreated = itemView.findViewById(R.id.textViewDateCreated);
            dateImageView = itemView.findViewById(R.id.dateImageView);
            imageViewAssigned = itemView.findViewById(R.id.imageViewAssigned);
            startTimeIV = itemView.findViewById(R.id.startTimeIV);
            completedTimeIV = itemView.findViewById(R.id.completedTimeIV);
            cardViewJob = itemView.findViewById(R.id.cardViewJob);
            buttonAssign = itemView.findViewById(R.id.buttonAssign);

            if (sharedPreferences.getString("Role", null).equals("Porter")){
                buttonAssign.setVisibility(View.GONE);
                textViewAssigned.setVisibility(View.GONE);
                imageViewAssigned.setVisibility(View.GONE);
            }else{
                if (page.equals("Ongoing") ){
                    buttonAssign.setVisibility(View.GONE);
                    textViewCompletedTime.setVisibility(View.INVISIBLE);
                    completedTimeIV.setVisibility(View.INVISIBLE);
                    textViewDateCreated.setVisibility(View.GONE);
                    dateImageView.setVisibility(View.GONE);
                }else if (page.equals("Queue")){
                    buttonAssign.setVisibility(View.VISIBLE);
                    textViewAssigned.setVisibility(View.VISIBLE);
                    startTimeIV.setVisibility(View.GONE);
                    textViewStartTime.setVisibility(View.GONE);
                    textViewCompletedTime.setVisibility(View.GONE);
                    completedTimeIV.setVisibility(View.GONE);
                    textViewDateCreated.setVisibility(View.GONE);
                    dateImageView.setVisibility(View.GONE);
                } else if (page.equals("History")){
                    buttonAssign.setVisibility(View.GONE);
                }
            }

            cardViewJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTime = now;
                    position = getAdapterPosition();
                    final Job job = mJobs.get(position);
                    Intent intent = new Intent(context, JobDetailActivity.class);
                    intent.putExtra("JobID", job.getJobID());
                    intent.putExtra("Page", page);
                    context.startActivity(intent);
                }
            });

            buttonAssign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTime = now;
                position = getAdapterPosition();
                final Job job = mJobs.get(position);
                Intent intent = new Intent(context, AssignPorterActivity.class);
                intent.putExtra("JobID", job.getJobID());
                context.startActivity(intent);
                }
            });
        }
    }
}
