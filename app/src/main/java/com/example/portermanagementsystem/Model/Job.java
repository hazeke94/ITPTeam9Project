package com.example.portermanagementsystem.Model;

import android.app.Dialog;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.EventLogTags;

import java.sql.Time;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.security.auth.Destroyable;

public class Job implements Parcelable{
    private String JobID;
    private String FromLocation;
    private String ToLocation;
    private int JobUrgency;
    private String TypeOfJob;
    private String CaseID;
    private String Assigned;
    private String CreatedBy;
    private String CreatedOn;
    private String CreatedTime;
    private String StartTime;
    private String PickUpTime;
    private String ArrivalTime;
    private String CompleteTime;
    private String AcknowledgeTime;
    private String Status;
    private String Remark;
    private String Description;
    private String assignedBy;

    public String getJobID() {
        return this.JobID;
    }

    public void setJobID(String JobID) { this.JobID = JobID; }

    public String getFromLocation() {
        return this.FromLocation;
    }

    public void setFromLocation(String FromLocation) {
        this.FromLocation = FromLocation;
    }

    public String getToLocation() {
        return this.ToLocation;
    }

    public void setToLocation(String ToLocation) {
        this.ToLocation = ToLocation;
    }

    public int getJobUrgency() {
        return this.JobUrgency;
    }

    public void setJobUrgency(int JobUrgency) {
        this.JobUrgency = JobUrgency;
    }

    public String getTypeOfJob() {
        return this.TypeOfJob;
    }

    public void setTypeOfJob(String TypeOfJob) {
        this.TypeOfJob = TypeOfJob;
    }

    public String getCaseID() {
        return this.CaseID;
    }

    public void setCaseID(String CaseID) {
        this.CaseID = CaseID;
    }

    public String getAssigned() {
        return this.Assigned;
    }

    public void setAssigned(String Assigned) {
        this.Assigned = Assigned;
    }

    public String getCreatedBy() {
        return this.CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        this.CreatedBy = createdBy;
    }

    public String getCreatedOn() {
        return this.CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        this.CreatedOn = createdOn;
    }

    public String getCreatedTime() {
        return this.CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        this.CreatedTime = createdTime;
    }

    public String getPickUpTime() {
        return this.PickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        this.PickUpTime = pickUpTime;
    }

    public String getArrivalTime() {
        return this.ArrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.ArrivalTime = arrivalTime;
    }

    public String getCompleteTime() {
        return this.CompleteTime;
    }

    public void setCompleteTime(String completeTime) {
        this.CompleteTime = completeTime;
    }

    public String getStartTime() {
        return this.StartTime;
    }

    public void setStartTime(String startTime) {
        this.StartTime = startTime;
    }

    public String getStatus() {
        return this.Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getAcknowledgeTime() {
        return this.AcknowledgeTime;
    }

    public void setAcknowledgeTime(String acknowledgeTime) {
        this.AcknowledgeTime = acknowledgeTime;
    }

    public String getRemark() {
        return this.Remark;
    }

    public void setRemark(String remark) {
        this.Remark = remark;
    }

    public String getDescription(){return this.Description;}

    public void setDescription(String description){this.Description = description;}

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Job(){ }

    public Job(String FromLocation, String ToLocation, int JobUrgency, String TypeOfJob, String CaseID, String Assigned, String CreatedOn, String CreatedBy, String CreatedTime, String StartTime, String PickUpTime, String ArrivalTime, String CompleteTime, String Status, String AcknowledgeTime, String Remark, String Description, String assignedBy) {
        this.FromLocation = FromLocation;
        this.ToLocation = ToLocation;
        this.JobUrgency = JobUrgency;
        this.TypeOfJob = TypeOfJob;
        this.CaseID = CaseID;
        this.Assigned = Assigned;
        this.CreatedOn = CreatedOn;
        this.CreatedBy = CreatedBy;
        this.CreatedTime = CreatedTime;
        this.StartTime = StartTime;
        this.PickUpTime = PickUpTime;
        this.ArrivalTime = ArrivalTime;
        this.CompleteTime = CompleteTime;
        this.Status = Status;
        this.AcknowledgeTime = AcknowledgeTime;
        this.Remark = Remark;
        this.Description = Description;
        this.assignedBy = assignedBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FromLocation);
        dest.writeString(ToLocation);
        dest.writeInt(JobUrgency);
        dest.writeString(TypeOfJob);
        dest.writeString(CaseID);
        dest.writeString(Assigned);
        dest.writeString(CreatedOn);
        dest.writeString(CreatedBy);
        dest.writeString(String.valueOf(CreatedTime));
        dest.writeString(String.valueOf(StartTime));
        dest.writeString(String.valueOf(PickUpTime));
        dest.writeString(String.valueOf(ArrivalTime));
        dest.writeString(String.valueOf(CompleteTime));
        dest.writeString(Status);
        dest.writeString(String.valueOf(AcknowledgeTime));
        dest.writeString(JobID);
        dest.writeString(Description);
        dest.writeString(assignedBy);
    }

    public Job(Parcel in) {
        this.FromLocation = in.readString();
        this.ToLocation = in.readString();
        this.JobUrgency = in.readInt();
        this.TypeOfJob = in.readString();
        this.CaseID = in.readString();
        this.Assigned = in.readString();
        this.CreatedOn = in.readString();
        this.CreatedBy = in.readString();
        this.CreatedTime = in.readString();
        this.StartTime = in.readString();
        this.PickUpTime = in.readString();
        this.ArrivalTime = in.readString();
        this.CompleteTime = in.readString();
        this.Status = in.readString();
        this.AcknowledgeTime=in.readString();
        this.JobID =in.readString();
        this.Description = in.readString();
        this.assignedBy = in.readString();
    }

    public static final Parcelable.Creator<Job> CREATOR = new Parcelable.Creator<Job>(){
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };
}
