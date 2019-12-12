package com.example.portermanagementsystem.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.portermanagementsystem.Activity.JobDetailActivity;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Util.util;
import java.util.List;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ReportListViewHolder>{
    private static final String TAG = "ReportListAdapter";
    private Context context;
    List<Job> mJobs;
    UserFirebaseInterface userFirebase = new UserFirebase();
    private int position = -1;
    public ReportListAdapter(List<Job> jobList, Context context){
        this.context = context;
        this.mJobs = jobList;
        mInflater = LayoutInflater.from(context);
    }
    private final LayoutInflater mInflater;

    @NonNull
    @Override
    public ReportListAdapter.ReportListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = mInflater.inflate(R.layout.job_kpi_item, viewGroup, false);
        return new ReportListAdapter.ReportListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReportListAdapter.ReportListViewHolder reportListViewHolder,final int position) {
        if (mJobs != null) {
            Job job = mJobs.get(position);
            switch (job.getJobUrgency()){
                case 1:
                    reportListViewHolder.cardViewJob.setBackgroundColor(Color.parseColor("#ff7675"));
                    break;
                case 2:
                    reportListViewHolder.cardViewJob.setBackgroundColor(Color.parseColor("#fdcb6e"));
                    break;
                case 3:
                    reportListViewHolder.cardViewJob.setBackgroundColor(Color.parseColor("#87CEFA"));
                    break;
            }
            switch (job.getTypeOfJob()){
                case "X-Ray":
                    reportListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_skeleton);
                    break;
                case "Labs":
                    reportListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_lab);
                    break;
                case "Discharge":
                    reportListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_discharge_black_24dp);
                    break;
                case "Document":
                    reportListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_assignment_black_24dp);
                    break;
                case "Transport":
                    reportListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_transport_black_24dp);
                    break;
                case "Inpatient":
                    reportListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_inpatient_black_24dp);
                    break;
                case "Day Surgery":
                    reportListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_surgery);
                    break;
                case "Maternity":
                    reportListViewHolder.jobTypeIcon.setImageResource(R.drawable.ic_pregnant_woman_black_24dp);
                    break;
            }
            boolean kpi = util.passKpi(job.getCreatedTime(), job.getStartTime());
            if(kpi){
                reportListViewHolder.imageViewKpi.setImageResource(R.drawable.ic_sentiment_satisfied_black_24dp);
            }else{
                reportListViewHolder.imageViewKpi.setImageResource(R.drawable.ic_sentiment_neutral_black_24dp);
            }
            //reportListViewHolder.textViewAssigned.setText(job.getAssigned());
            if(job.getAssigned().equals("") || job.getAssigned().equals("-")){
                reportListViewHolder.textViewAssigned.setText("-");
            }else{
                userFirebase.getName(job.getAssigned(), new UserCallBack() {
                    @Override
                    public void onCallBack(User value) {

                    }

                    @Override
                    public void onCallBack(List<User> value) {

                    }

                    @Override
                    public void onCallBack(String value) {
                        reportListViewHolder.textViewAssigned.setText(value);
                    }

                    @Override
                    public void onCallBack(Boolean value) {

                    }
                });
            }
            reportListViewHolder.textViewCreatedTime.setText(job.getCreatedTime());
            reportListViewHolder.textViewCaseID.setText(job.getCaseID());
            reportListViewHolder.textViewStartTime.setText(job.getStartTime());
            reportListViewHolder.textViewCompletedTime.setText(job.getCompleteTime());
            reportListViewHolder.textViewDateCreated.setText(job.getCreatedOn());
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

    public class ReportListViewHolder extends RecyclerView.ViewHolder {
        private final TextView  textViewAssigned, textViewCaseID, textViewCreatedTime, textViewDateCreated, textViewStartTime, textViewCompletedTime;//textViewKpi,
        private final ImageView imageViewKpi,jobTypeIcon;
        private final CardView cardViewJob;
        private static final long CLICK_TIME_INTERVAL = 300;
        private long mLastClickTime = System.currentTimeMillis();

        public ReportListViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewJob = itemView.findViewById(R.id.cardViewJob);
            jobTypeIcon = itemView.findViewById(R.id.jobTypeIcon);
            textViewAssigned = itemView.findViewById(R.id.textViewAssigned);
            imageViewKpi = itemView.findViewById(R.id.imageViewKpi);
            textViewCaseID = itemView.findViewById(R.id.textViewCaseID);
            textViewCreatedTime = itemView.findViewById(R.id.textViewCreatedTime);
            textViewStartTime = itemView.findViewById(R.id.textViewStartTime);
            textViewCompletedTime = itemView.findViewById(R.id.textViewCompletedTime);
            textViewDateCreated = itemView.findViewById(R.id.textViewDateCreated);
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
                    context.startActivity(intent);
                }
            });
        }
    }
}
