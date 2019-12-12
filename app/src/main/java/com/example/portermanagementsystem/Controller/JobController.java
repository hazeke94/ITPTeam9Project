package com.example.portermanagementsystem.Controller;

import android.util.Log;

import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Util.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class JobController implements JobControllerInterface{
    JobFirebaseInterface jobFirebase = new JobFirebase();

    @Override
    public void updateJobStatus(Job job){
        System.out.println("Update Job Status");
        DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
        String time = df2.format(Calendar.getInstance().getTime());
        switch (job.getStatus()) {
            case "Assigned":
                jobFirebase.updateJobStatus(job.getJobID(), time, "acknowledgeTime", "Acknowledged");
                break;
            case "Started":
                jobFirebase.updateJobStatus(job.getJobID(), time, "pickUpTime", "Pick Up");
                break;
            case "Arrived":
                jobFirebase.updateJobStatus(job.getJobID(), time, "completeTime", "Completed");
                break;
            case "Acknowledged":
                jobFirebase.updateJobStatus(job.getJobID(), time, "startTime", "Started");
                break;
            case "Pick Up":
                jobFirebase.updateJobStatus(job.getJobID(), time, "arrivalTime", "Arrived");
                break;
        }
    }

    @Override
    public void updateJob(Job job) {
        jobFirebase.updateJob(job);
    }

    @Override
    public String insertJob(Job job) {

        return jobFirebase.insertJob(job);
    }

    @Override
    public List<Object> getPorterJob(List<Job> jobList) {
        List<Object> myObject = new ArrayList<>();
        List<Job> filterList = new ArrayList<>();
        int queue =0; Job cJob = null;
        for(Job job: jobList) {
            if (jobList.size() > 0) {
                if (!"Completed".equals(job.getStatus()) && !"Pending".equals(job.getStatus()) && !"Cancelled".equals(job.getStatus())) {
                    filterList.add(job);
                    if ("Assigned".equals(job.getStatus())) {
                        queue += 1;
                    } else {
                        //Get ongoing job
                        cJob = job;
                    }
                }
            }
        }
        myObject.add(jobList);
        myObject.add(cJob);
        myObject.add(queue);
        return myObject;
    }

    @Override
    public List<Object> getJobCount(List<Job> jobList){
        List<Object> myObject = new ArrayList<>();
        int ongoing=0; int queue=0;
        for(Job job : jobList){
            if(!job.getStatus().equals("Completed") && !job.getStatus().equals("Pending") && !job.getStatus().equals("Cancelled") && !job.getStatus().equals("Assigned")){
                ongoing += 1;
            }
            if(job.getStatus().equals("Pending") || job.getStatus().equals("Assigned")){
                queue += 1;
            }
        }
        myObject.add(ongoing);
        myObject.add(queue);
        return myObject;
    }

    @Override
    public boolean validateCaseID(String caseID, String role){
        if (caseID.equals("")) {
            return false;
        } else {
//        if(role.equals("Porter")) {
//            if (caseID.charAt(0) == 'I' || caseID.charAt(0) == 'O') {
//                caseID = caseID.replaceAll("[^\\d]", "");
//                //check if theres 9 numeric value
//                if (caseID.length() == 9) {
//                    return true;
//                }
//            }
//            return false;
//        }
            return true;
        }
    }

    @Override
    public boolean validateJobType(String type) {
        if(!type.equals("")){
            return true;
        }
        return false;
    }

    @Override
    public boolean validateUrgency(String urgency) {
        if(!urgency.equals("")){
            return true;
        }
        return false;
    }

    @Override
    public boolean validateStaffID(String StaffId){
        if (StaffId.equals("")) {
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean validateToLocation(String toLocation, String location) {
        return toLocation.equals(location);
    }

    @Override
    public boolean validateFromLocation(String fromLocation, String location) {
        return fromLocation.equals(location);
    }

    @Override
    public boolean validateLocation(String location) {
        return location.isEmpty();
    }

    @Override
    public boolean validateDescription(String description) {
        return description.equals("");
    }

    @Override
    public boolean validateCorrectToLocation(String location, List<String> listofLocation) {
        for(String item : listofLocation)
        {
            if(item.equals(location)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validateCorrectFromLocation(String location, List<String> listofLocation) {
        for(String item : listofLocation)
        {
            Log.d("location", item);
            if(item.equals(location)){
                Log.d("location", "return true");
                return true;
            }
        }
        Log.d("location", "return false");
        return false;
    }

    @Override
    public String errorMessageCaseID(String caseID) {
            Date d = new Date();
            int year = d.getYear();
            if(caseID.isEmpty()){
                return "Please enter a case ID";
            } else if(caseID.length()<10){
                return "Please enter a valid case ID";
            } else if (!caseID.startsWith("I") && !caseID.startsWith("O")){
                return "Please enter a valid case ID";
            } else if(!(caseID.substring(1,3).equals(String.valueOf(year).substring(1,3)))){
                return "Please enter a valid case ID";
            }
            return "";
    }

    @Override
    public List<Job> getJobHistory(List<Job> jobList, String type, String month){
        List<Job> filterList = new ArrayList<>();
        for(Job job: jobList){
            String splitDate[] = job.getCreatedOn().split("-");
            int jobMonth = Integer.valueOf(splitDate[1]);
            int selectedMonth = util.getMonth(month);
            if((job.getTypeOfJob().equals(type) || type.equals("All Jobs")) && (selectedMonth == jobMonth || selectedMonth ==0)){
                filterList.add(job);
            }
        }
        return filterList;
    }

    @Override
    public List<Job> getAllHistory(List<Job> jobList) {
        List<Job> historyList = new ArrayList<>();
        final int year = Calendar.getInstance().get(Calendar.YEAR);
        for(Job job: jobList){
            String splitdate[] = job.getCreatedOn().split("-");
            String jobYear = splitdate[2];
            if (String.valueOf(year).equals(jobYear)) {
                if (job.getStatus().equals("Completed") || job.getStatus().equals("Cancelled")){
                    historyList.add(job);
                }
            }
        }
        return historyList;
    }

    @Override
    public List<Job> getOngoingJob(List<Job> jobList){
        final int year = Calendar.getInstance().get(Calendar.YEAR);
        List<Job> filterList = new ArrayList<>();
        for(Job job : jobList){
            String splitdate[] = job.getCreatedOn().split("-");
            String jobYear = splitdate[2];
            if ((!"Assigned".equals(job.getStatus()) &&!"Completed".equals(job.getStatus()) && !"Pending".equals(job.getStatus()) && !"Cancelled".equals(job.getStatus()))&& String.valueOf(year).equals(jobYear)) {
                filterList.add(job);
            }
        }
        return filterList;
    }

    @Override
    public List<Job> getQueueJob(List<Job> jobList){
        final int year = Calendar.getInstance().get(Calendar.YEAR);
        List<Job> filterList = new ArrayList<>();
        for(Job job : jobList){
            String splitdate[] = job.getCreatedOn().split("-");
            String jobYear = splitdate[2];
            if (("Pending".equals(job.getStatus()) || "Assigned".equals(job.getStatus())) && String.valueOf(year).equals(jobYear)) {
                filterList.add(job);
            }
        }
        return filterList;
    }

    @Override
    public void updateRemarks(String JobID, String remarks, String status) {
        try{
            jobFirebase.updateRemarks(JobID,remarks,status);
        }catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }
    @Override
    public List<Object> getKpiReport(List<Job> jobList, String selectedMonth, String selectedYear){
        List<Object> myObject = new ArrayList<>();
        List<Job> smileyList = new ArrayList<>();
        List<Job> sadList = new ArrayList<>();
        for (Job job:jobList){
            String[] calend = job.getCreatedOn().split("-");
            int month = Integer.parseInt(calend[1]);
            int year = Integer.parseInt(calend[2]);
            if (String.valueOf(selectedMonth).equals(String.valueOf(month)) && String.valueOf(selectedYear).equals(String.valueOf(year))) {
                if (job.getStatus().equals("Completed")){
                    boolean kpi = util.passKpi(job.getCreatedTime(), job.getStartTime());
                    if(kpi) {
                        smileyList.add(job);
                    }
                    else {
                        sadList.add(job);
                    }
                }
            }
        }
        myObject.add(smileyList);
        myObject.add(sadList);
        return myObject;
    }

    @Override
    public int getNumberAssignedOfJob(List<Job> jobList){
        int number=0;
        for(Job job: jobList){
            if(!"Completed".equals(job.getStatus()) && !"Pending".equals(job.getStatus()) && !"Cancelled".equals(job.getStatus())){
                number +=1;
            }
        }
        return number;
    }

    @Override
    public void assignPorter(String JobId, String assigned, String assignedBy) {
        if((assigned != null && !assigned.equals("")) && (assignedBy != null && !assignedBy.equals(""))){
            jobFirebase.assignPorter(JobId, assigned, assignedBy);
        }
    }
}
