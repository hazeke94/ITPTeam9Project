package com.example.portermanagementsystem.Util;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import androidx.work.WorkManager;

import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Model.Report;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class util {
    private static final String TAG = "util";
    static String assignedTo,assignedBy, createdBy;
    static UserFirebaseInterface userFirebase = new UserFirebase();
    public static boolean passKpi(String createdTime, String startTime){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date d1,d2;
        try
        {
            d1 = format.parse(createdTime);
            d2 = format.parse(startTime);

            long timestamp1 = d1.getTime();
            long timestamp2 = d2.getTime();
            if (Math.abs(timestamp2 - timestamp1) < TimeUnit.MINUTES.toMillis(10)) {
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
            return false;
        }
    }

    public static void generateReport(String filename, final List<Job> jobList, List<Report> reportList , List<Report> porterList, String tagName) throws WriteException, IOException {
        File sd = Environment.getExternalStorageDirectory();
        String csvFile = filename + ".xls";
        Log.d(TAG, tagName);
        File directory = new File(sd.getAbsolutePath());
        if(!directory.isDirectory()){
            directory.mkdirs();
        }
        File file = new File(directory, csvFile);
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;
        workbook = Workbook.createWorkbook(file, wbSettings);
        //Excel sheet name. 0 represents first sheet
        final WritableSheet sheet = workbook.createSheet("Job Data", 0);
        // column and row
        sheet.addCell(new Label(0, 0, "CaseID"));
        sheet.addCell(new Label(1, 0, "Date"));
        sheet.addCell(new Label(2, 0, "Time Created"));
        sheet.addCell(new Label(3, 0, "Origin"));
        sheet.addCell(new Label(4, 0, "Destination"));
        sheet.addCell(new Label(5, 0, "Porter"));
        sheet.addCell(new Label(6, 0, "Receptionist"));
        sheet.addCell(new Label(7, 0, "Assigned By"));
        sheet.addCell(new Label(8, 0, "Acknowledge Time"));
        sheet.addCell(new Label(9, 0, "Start Time"));
        sheet.addCell(new Label(10, 0, "Pick Up Time"));
        sheet.addCell(new Label(11, 0, "Arrival Time"));
        sheet.addCell(new Label(12, 0, "Completed Time"));
        sheet.addCell(new Label(13, 0, "Remarks"));
        int i=0;
        do {
            final int j=i+1;
            sheet.addCell(new Label(0, j, jobList.get(i).getCaseID()));
            sheet.addCell(new Label(1, j, jobList.get(i).getCreatedOn()));
            sheet.addCell(new Label(2, j, jobList.get(i).getCreatedTime()));
            sheet.addCell(new Label(3, j, jobList.get(i).getFromLocation()));
            sheet.addCell(new Label(4, j, jobList.get(i).getToLocation()));
            sheet.addCell(new Label(5, j, jobList.get(i).getAssigned()));
            sheet.addCell(new Label(6, j, jobList.get(i).getCreatedBy()));
            sheet.addCell(new Label(7, j, jobList.get(i).getAssignedBy()));
            sheet.addCell(new Label(8, j, jobList.get(i).getAcknowledgeTime()));
            sheet.addCell(new Label(9, j, jobList.get(i).getStartTime()));
            sheet.addCell(new Label(10, j, jobList.get(i).getPickUpTime()));
            sheet.addCell(new Label(11, j, jobList.get(i).getArrivalTime()));
            sheet.addCell(new Label(12, j, jobList.get(i).getCompleteTime()));
            sheet.addCell(new Label(13, j, jobList.get(i).getRemark()));
            i++;
        }while (i < jobList.size());


        WritableSheet sheet1 = workbook.createSheet("Job Summary", 1);
        // column and row
        sheet1.addCell(new Label(0, 0, "Job Type"));
        sheet1.addCell(new Label(1, 0, "Total Number of Job"));
        sheet1.addCell(new Label(2, 0, "Kpi %"));
        int x=0;
        do {
            int y=x+1;
            sheet1.addCell(new Label(0, y, reportList.get(x).getTypeOfJob()));
            sheet1.addCell(new Label(1, y, reportList.get(x).getTotal()));
            sheet1.addCell(new Label(2, y, reportList.get(x).getKpi()));
            x++;
        }while (x < reportList.size());

        WritableSheet sheet2 = workbook.createSheet("Porter Summary", 2);
        // column and row
        sheet2.addCell(new Label(0, 0, "Porter"));
        sheet2.addCell(new Label(1, 0, "Total Number of Job"));
        sheet2.addCell(new Label(2, 0, "Kpi %"));
        int a=0;
        do {
            int b=a+1;
            sheet2.addCell(new Label(0, b, porterList.get(a).getTypeOfJob()));
            sheet2.addCell(new Label(1, b, porterList.get(a).getTotal()));
            sheet2.addCell(new Label(2, b, porterList.get(a).getKpi()));
            a++;
        }while (a < porterList.size());
        workbook.write();
        workbook.close();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int getMonth(String month){
        switch(month){
            case "JAN":
                return 1;
            case "FEB":
                return 2;
            case "MAR":
                return 3;
            case "APR":
                return 4;
            case "MAY":
                return 5;
            case "JUN":
                return 6;
            case "JUL":
                return 7;
            case "AUG":
                return 8;
            case "SEP":
                return 9;
            case "OCT":
                return 10;
            case "NOV":
                return 11;
            case "DEC":
                return 12;
            default:
                return 0;
        }
    }

    public static void cancelWork(String caseID){
        //Log.d(TAG, "Job case ID: " +  caseID + " assigned");
        Log.d(TAG, "Cancel : " +  caseID + " thread");
        WorkManager.getInstance().cancelAllWorkByTag(caseID);
    }
}
