package com.example.portermanagementsystem.Controller;

import com.example.portermanagementsystem.Model.Job;

import java.util.List;

public interface JobControllerInterface {
    void updateJobStatus(Job job);
    void updateJob(Job job);
    String insertJob(Job job);
    boolean validateCaseID(String caseID, String role);
    boolean validateJobType(String type);
    boolean validateUrgency(String urgency);
    boolean validateStaffID(String StaffId);
    boolean validateToLocation(String toLocation, String location);
    boolean validateFromLocation(String fromLocation, String location);
    boolean validateLocation(String location);
    boolean validateDescription(String description);
    boolean validateCorrectToLocation(String location, List<String> listofLocation);
    boolean validateCorrectFromLocation(String location, List<String>listofLocation);
    String errorMessageCaseID(String caseID);
    List<Job> getJobHistory(List<Job> jobList, String type, String month);
    List<Job> getAllHistory(List<Job> jobList);
    List<Object> getPorterJob(List<Job> jobList);
    List<Object> getJobCount(List<Job> jobList);
    List<Job> getOngoingJob(List<Job> jobList);
    List<Job> getQueueJob(List<Job> jobList);
    void updateRemarks(String JobID,String remarks,String status);
    List<Object> getKpiReport(List<Job> jobList, String selectedMonth, String selectedYear);
    int getNumberAssignedOfJob(List<Job> jobList);
    void assignPorter(String JobId, String assigned, String assignedBy);
}
