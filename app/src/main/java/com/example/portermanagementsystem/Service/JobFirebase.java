package com.example.portermanagementsystem.Service;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JobFirebase implements JobFirebaseInterface{
    private static final String TAG = "JobFirebase";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    @Override
    public void getJobDetail(final String jobId, final JobCallBack callBack){
        Query jobQuery = mDatabase.child("Job").child(jobId);
        jobQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (dataSnapshot.getKey().equals(jobId)) {
                            Job job = dataSnapshot.getValue(Job.class);
                            job.setJobID(dataSnapshot.getKey());
                            callBack.onCallBack(job);
//                            break;
                        }
                    }
//                }
//            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public void updateJob(final Job job){
        Query jobQuery = mDatabase.child("Job");
        jobQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.getKey().equals(job.getJobID())) {
                        DatabaseReference jobRef = dataSnapshot.getRef();
                        jobRef.child("caseID").setValue(job.getCaseID());
                        jobRef.child("status").setValue(job.getStatus());
                        jobRef.child("arrivalTime").setValue(job.getArrivalTime());
                        jobRef.child("pickUpTime").setValue(job.getPickUpTime());
                        jobRef.child("startTime").setValue(job.getStartTime());
                        jobRef.child("completeTime").setValue(job.getCompleteTime());
                        jobRef.child("typeOfJob").setValue(job.getTypeOfJob());
                        jobRef.child("fromLocation").setValue(job.getFromLocation());
                        jobRef.child("toLocation").setValue(job.getToLocation());
                        jobRef.child("description").setValue(job.getDescription());
                        jobRef.child("jobUrgency").setValue(job.getJobUrgency());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    @Override
    public void isCompleted(final String jobId, final JobCallBack callBack){
        final Query detailQuery = mDatabase.child("Job");
        detailQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isComplete=true;
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    if (childSnapshot.getKey().equals(jobId)){
                        Job job = childSnapshot.getValue(Job.class);
                        job.setJobID(childSnapshot.getKey());
                        if (job.getStatus().equals("Completed") ||job.getStatus().equals("Cancelled")){
                            isComplete =false;
                            break;
                        }
                    }
                }
                callBack.onCallBack(isComplete);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void getAllJobByUser(String role, String userId, final JobCallBack callBack){
        Query jobQuery;
        if(role != null) {
            if (role.equals("Porter")) {
                jobQuery = mDatabase.child("Job").orderByChild("assigned").equalTo(userId);
            } else {
                jobQuery = mDatabase.child("Job");
            }

            jobQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Job> jobList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Job job = snapshot.getValue(Job.class);
                        job.setJobID(snapshot.getKey());
                        jobList.add(job);
                    }
                    callBack.onCallBack(jobList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void getJobByUrgencyLevel(final String role, final String userId, final JobCallBack callBack){
        Query jobQuery;
        jobQuery = mDatabase.child("Job").orderByChild("jobUrgency");
        jobQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Job> jobList= new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, snapshot.getKey());
                    Job job = snapshot.getValue(Job.class);
                    job.setJobID(snapshot.getKey());

                    if(role.equals("Porter")){
                        if(job.getAssigned() != null && role.equals("Porter") && job.getAssigned().equals(userId)){
                                jobList.add(job);
                        }
                    }else{
                        jobList.add(job);
                    }
                }
                callBack.onCallBack(jobList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getAllJob(final JobCallBack callBack){
        Query jobQuery;
        jobQuery = mDatabase.child("Job");
        jobQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Job> jobList= new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);
                    job.setJobID(snapshot.getKey());
                    jobList.add(job);
                }
                callBack.onCallBack(jobList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Get job by types of Job
    @Override
    public void getAllJobByJobType(String type,final JobCallBack callBack){
        Query jobQuery;
        if(type.equals("All Jobs")){
            jobQuery = mDatabase.child("Job");
        }else {
            jobQuery = mDatabase.child("Job").orderByChild("typeOfJob").equalTo(type);
        }

        jobQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Job> jobList= new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);
                    job.setJobID(snapshot.getKey());
                    jobList.add(job);
                }
                callBack.onCallBack(jobList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Update time
    @Override
    public void updateJobStatus(String jobId, final String time,final String field, final String status) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Job/" + jobId);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.child(field).getRef().setValue(time);
                snapshot.child("status").getRef().setValue(status, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Data could not be saved. " + databaseError.getMessage());
                            //reload data
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //Update Remarks
    @Override
    public void updateRemarks(final String jobId, final String remarks, final String status){
        mDatabase = FirebaseDatabase.getInstance().getReference("Job");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Job job = dataSnapshot.getValue(Job.class);
                    job.setJobID(dataSnapshot.getKey());
                    if (job.getJobID().equals(jobId)) {
                        dataSnapshot.child("status").getRef().setValue(status);
                        dataSnapshot.child("remark").getRef().setValue(remarks);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Get all completed Job
    @Override
    public void getAllCompletedJob(final JobCallBack callBack){
        Query jobQuery;
        jobQuery = mDatabase.child("Job");
        jobQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Job> jobList= new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);
                    job.setJobID(snapshot.getKey());
                    if(job.getStatus().equals("Completed") ||job.getStatus().equals("Cancelled"))
                    jobList.add(job);
                }
                callBack.onCallBack(jobList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getAllReportByDate(final String selectedMonth, final String selectedYear, final JobCallBack callBack){
        final Query jobQuery = mDatabase.child("Job").orderByChild("status").equalTo("Completed");
        jobQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Job> jobList= new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Job job = childSnapshot.getValue(Job.class);
                        job.setJobID(childSnapshot.getKey());
                        String createdDate = job.getCreatedOn();
                        String[] calend = createdDate.split("-");
                        Calendar now = Calendar.getInstance();
                        int year = Integer.parseInt(calend[2]);
                        int month = Integer.parseInt(calend[1]);
                        if (selectedMonth.equals(String.valueOf(month)) && String.valueOf(year).equals(selectedYear)) {
                            jobList.add(job);
                        }
                    }
                }
                callBack.onCallBack(jobList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public String insertJob(Job job){
        mDatabase = FirebaseDatabase.getInstance().getReference("Job");
        final String JobID = mDatabase.child("Job").push().getKey();
        Log.d(TAG, "Job case ID: " +  JobID);
        mDatabase.child(JobID).setValue(job);
        return JobID;
    }

    @Override
    public void assignPorter(String JobID,final String assigned,final String assignedBy) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Job").child(JobID);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child("assigned").getRef().setValue(assigned);
                dataSnapshot.child("status").getRef().setValue(("Assigned"));
                dataSnapshot.child("assignedBy").getRef().setValue(assignedBy);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}