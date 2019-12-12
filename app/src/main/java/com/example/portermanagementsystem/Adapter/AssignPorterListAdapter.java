package com.example.portermanagementsystem.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.WorkManager;

import com.example.portermanagementsystem.Activity.AssignPorterActivity;
import com.example.portermanagementsystem.Activity.JobQueueActivity;
import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.Util.util;

import java.util.ArrayList;
import java.util.List;


public class AssignPorterListAdapter extends RecyclerView.Adapter<AssignPorterListAdapter.AssignPorterViewHolder> {
    private static final String TAG = "AssignPorterActivity";
    List<User> mUsers;
    JobFirebaseInterface jobFirebase = new JobFirebase();
    UserFirebaseInterface userFirebase = new UserFirebase();
    UserControllerInterface userController = new UserController();
    JobControllerInterface jobController = new JobController();
    private ProgressDialog mDialog;
    String caseID,JobID;
    String staffID = "";
    boolean isClicked = false;
    Activity mContext;
    public AssignPorterListAdapter(List<User> userList, Activity context, String Sid) {
        this.mUsers = userList;
        this.staffID = Sid;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    private final LayoutInflater mInflater;

    @Override
    public AssignPorterListAdapter.AssignPorterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.assign_porter_item, parent, false);
        return new AssignPorterListAdapter.AssignPorterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AssignPorterListAdapter.AssignPorterViewHolder porterViewHolder, final int position) {
        if (mUsers != null) {
            User user = mUsers.get(position);
            porterViewHolder.textViewEmployeeName.setText(user.getName());
            String userGender = user.getGender();
            switch(userGender){
                case "Male":
                    porterViewHolder.imageViewGender.setImageResource(R.drawable.ic_man_);
                    break;
                case "Female":
                    porterViewHolder.imageViewGender.setImageResource(R.drawable.ic_girl);
                    break;

            }
            porterViewHolder.textViewEmployeeStatus.setText("Status: " + user.getStatus());
            final String userID = user.getStaffID();
            jobFirebase.getAllJobByUser("Porter", userID, new JobCallBack() {
                @Override
                public void onCallBack(Job value) { }

                @Override
                public void onCallBack(List<Job> value) {
                    int noOfJobs = jobController.getNumberAssignedOfJob(value);
                    porterViewHolder.textViewNumberOfJobs.setText("Number of jobs: " + String.valueOf(noOfJobs));
                }

                @Override
                public void onCallBack(boolean value) { }

                @Override
                public void onCallBack(String value) { }
            });
        } else {
            porterViewHolder.textViewEmployeeName.setText("No Porter Available");
            porterViewHolder.textViewEmployeeStatus.setText("No Porter Available");
        }
    }

    @Override
    public int getItemCount() {
        if (mUsers != null) {
            return mUsers.size();
        } else {
            return 0;
        }
    }

    public class AssignPorterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewEmployeeName, textViewEmployeeStatus, textViewNumberOfJobs;
        private final ImageView imageViewGender;
        private final Button assignButton;
        private AssignPorterViewHolder(View itemView) {
            super(itemView);
            textViewEmployeeName = itemView.findViewById(R.id.textViewEmployeeName);
            textViewEmployeeStatus = itemView.findViewById(R.id.textViewEmployeeStatus);
            textViewNumberOfJobs = itemView.findViewById(R.id.textViewNumberOfJobs);
            imageViewGender = itemView.findViewById(R.id.imageViewGender);
            assignButton = itemView.findViewById(R.id.buttonAssign);
            assignButton.setOnClickListener(this);
        }

        String staffIDForPorter;
        String staffUID;
        String token;
        String jobType;

        @Override
        public void onClick(final View v) {

            assignButton.setEnabled(false);
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Assigning Porter. Please hold.");
            mDialog.setIndeterminate(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
            if (!isClicked) {

                isClicked = true;
                Intent jobIntent = ((Activity) v.getContext()).getIntent();
                JobID = jobIntent.getStringExtra("JobID");
                Log.d(TAG, "Assign Porter i want to get Job ID " + JobID);
                final String clickedItem = textViewEmployeeName.getText().toString();
                staffIDForPorter = retrieveStaffID(clickedItem);
                for (User user : mUsers) {
                    if (user.getName().equals(clickedItem)) {
                        token = user.getFCMToken();
                        staffUID = user.getStaffID();
                        break;
                    }
                }
                    userController.updatePorterStatus(staffID, "online");
                    Log.d(TAG, "Showing dialog");

                Log.d(TAG, String.valueOf(mDialog.isShowing()));
                    jobFirebase.getJobDetail(JobID, new JobCallBack() {
                        @Override
                        public void onCallBack(Job value) {
//                            mDialog.dismiss();
                            Log.d(TAG, "Dismiss dialog");
                            caseID = value.getCaseID();
                            jobType = value.getTypeOfJob();

                            jobController.assignPorter(JobID, staffIDForPorter, staffID);
                            util.cancelWork(caseID);
                            Log.d(TAG, "Job case ID: " + caseID + " assigned to " + clickedItem + " successfully" + staffIDForPorter + "AssignedBy " + staffID);
                            Toast.makeText(v.getContext(), "Job case ID: " + caseID + " assigned to " + clickedItem + " successfully", Toast.LENGTH_SHORT).show();
                            final Intent intentAfterAssign = new Intent(v.getContext(), JobQueueActivity.class);
                            v.getContext().startActivity(intentAfterAssign);
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
        }


    private String retrieveStaffID(String staffName){
        for (User user : mUsers) {
            if(user.getName().equals(staffName))
                return user.getStaffID();
        }
        return "";
    }

    /**
     * For assign porter notification
     */
    private void cancelWork(String caseID){
        //WorkManager.getInstance().cancelWorkById();
        Log.d(TAG, "Stopping : " +  caseID);
        WorkManager.getInstance().cancelAllWorkByTag(caseID);
//        //WorkManager.getInstance().cancelAllWork();
        Log.d(TAG, "check : " +  WorkManager.getInstance().getWorkInfosByTag(caseID).isCancelled());

    }
}
