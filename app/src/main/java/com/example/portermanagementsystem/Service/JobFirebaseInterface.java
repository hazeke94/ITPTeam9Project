package com.example.portermanagementsystem.Service;

import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.Model.Job;

public interface JobFirebaseInterface {
    void getJobDetail(final String jobId, final JobCallBack callback);
    void updateJob(final Job job);
    void isCompleted(final String jobId, final JobCallBack callback);
    void getAllJobByUser(String role, String userId, final JobCallBack callback);
    void getAllJob(final JobCallBack callback);
    void getAllJobByJobType(String type, final JobCallBack callback);
    void getJobByUrgencyLevel(String role, String userId, final JobCallBack callBack);
    void updateJobStatus(String jobId, final String time,final String field, final String status);
    void updateRemarks(final String jobId, final String remarks, final String status);
    void getAllCompletedJob(final JobCallBack callback);
    void getAllReportByDate(final String selectedMonth, final String selectedyear, final JobCallBack callback);
    String insertJob(Job job);
    void assignPorter(String JobID, String assigned, String assignedBy);
}
