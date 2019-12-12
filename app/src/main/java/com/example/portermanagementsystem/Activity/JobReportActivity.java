package com.example.portermanagementsystem.Activity;

import android.Manifest;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Model.Report;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Util.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jxl.write.WriteException;

public class JobReportActivity extends AppCompatActivity {
    private static final String TAG = "JobReportActivity";
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    static List<Report> reportList= new ArrayList();
    static List<Report> porterList= new ArrayList();
    List<User> userList = new ArrayList<>();
    String email,staffID;
    UserFirebaseInterface userFirebase = new UserFirebase();
    JobFirebaseInterface jobFirebase = new JobFirebase();
    UserControllerInterface userController = new UserController();
    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    ArrayAdapter<String> adapter;
    Calendar calendar = Calendar.getInstance();

    ArrayList<String> reportMthList;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");

    private int selectedMonth = 0;
    private int selectedYear = 0;

    int totalJob=0, passJob=0,total =0, xray=0,labs=0, discharge=0, document=0,transport=0,inpatient=0, daySurgery=0, maternity=0, xrayPass=0, labsPass=0, dischargePass=0,
            documentPass=0, transportPass=0, inpatientPass=0, daySurgeryPass=0, maternityPass=0, totalJob1=0, passJob1=0;
    TextView textViewTotal,textViewXrayTotal,textViewLabTotal,textViewDischargeTotal, textViewDocumentTotal,textViewTransportTotal,
            textViewInpatientTotal, textViewOutpatientTotal, textViewMaternityTotal, textViewXrayKpi, textViewLabKpi, textViewDischargeKpi, textViewDocumentKpi,
            textViewTransportKpi, textViewInpatientKpi, textViewDaySurgeryKpi, textViewMaternityKpi,textViewTotalKPI, textViewNoReport;
    Spinner spinnerMonth;
    ScrollView scrollViewReport;

    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();

    public void init(){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        staffID = sharedpreferences.getString("StaffID", null);
        textViewTotal = findViewById(R.id.textViewTotal);
        textViewXrayTotal= findViewById(R.id.textViewXrayTotal);
        textViewLabTotal= findViewById(R.id.textViewLabTotal);
        textViewDischargeTotal= findViewById(R.id.textViewDischargeTotal);
        textViewDocumentTotal= findViewById(R.id.textViewDocumentTotal);
        textViewTransportTotal= findViewById(R.id.textViewTransportTotal);
        textViewInpatientTotal= findViewById(R.id.textViewInpatientTotal);
        textViewOutpatientTotal= findViewById(R.id.textViewOutpatientTotal);
        textViewMaternityTotal= findViewById(R.id.textViewMaternityTotal);
        textViewXrayKpi = findViewById(R.id.textViewXrayKpi);
        textViewLabKpi = findViewById(R.id.textViewLabKpi);
        textViewDischargeKpi = findViewById(R.id.textViewDischargeKpi);
        textViewDocumentKpi = findViewById(R.id.textViewDocumentKpi);
        textViewTransportKpi = findViewById(R.id.textViewTransportKpi);
        textViewInpatientKpi = findViewById(R.id.textViewInpatientKpi);
        textViewDaySurgeryKpi = findViewById(R.id.textViewDaySurgeryKpi);
        textViewMaternityKpi = findViewById(R.id.textViewMaternityKpi);
        textViewTotalKPI = findViewById(R.id.textViewTotalKPI);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        textViewNoReport = findViewById(R.id.textViewNoReport);
        scrollViewReport = findViewById(R.id.scrollViewReport);
        userFirebase.getAllUser(new UserCallBack() {
            @Override
            public void onCallBack(User value) {

            }

            @Override
            public void onCallBack(List<User> value) {
                userList=value;
            }

            @Override
            public void onCallBack(String value) {

            }

            @Override
            public void onCallBack(Boolean value) {

            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porter_report);
        init();
        // Request permissions if user has denied previously
        if (!util.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        Calendar c = Calendar.getInstance();
        final int todayMonth = c.get(Calendar.MONTH)+1;

        reportMthList = new ArrayList<>();
        populateDateRange();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reportMthList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        //To show the previous month data first
        spinnerMonth.setSelection(0);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                ((TextView)parent.getChildAt(0)).setTextSize(24);
                ((TextView)parent.getChildAt(0)).setTextColor(getColor(R.color.pink));
                ((TextView)parent.getChildAt(0)).setTypeface(null, Typeface.BOLD);
                theMonthReport();
                thePorterReport();
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        //Get Email
        userFirebase.getEmail(staffID, new UserCallBack() {
            @Override
            public void onCallBack(User value) {

            }

            @Override
            public void onCallBack(List<User> value) {

            }

            @Override
            public void onCallBack(String value) {
                email = value;
            }

            @Override
            public void onCallBack(Boolean value) {

            }
        });
    }

    private void populateDateRange(){
        for(int i = 1; i < 13; i++){
            calendar.add(Calendar.MONTH, -1);
            Date date = calendar.getTime();
            reportMthList.add(dateFormat.format(date).toUpperCase());
        }
    }

    public void theMonthReport() {
        final SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
        final String monthOnSpinner = spinnerMonth.getSelectedItem().toString();
        try {
            Date selectedDate = format.parse(monthOnSpinner);
            Calendar cal = Calendar.getInstance();
            calendar.setTime(selectedDate);
            selectedMonth = calendar.get(Calendar.MONTH) + 1;
            selectedYear = calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        scrollViewReport.setVisibility(View.GONE);

        jobFirebase.getAllReportByDate(String.valueOf(selectedMonth), String.valueOf(selectedYear), new JobCallBack() {
            @Override
            public void onCallBack(Job value) {

            }

            @Override
            public void onCallBack(List<Job> value) {
                total =0; xray=0; labs=0; discharge=0; document=0; transport=0;inpatient=0; daySurgery=0; maternity=0; xrayPass=0; labsPass=0; dischargePass=0;
                documentPass=0; transportPass=0; inpatientPass=0; daySurgeryPass=0; maternityPass=0;
                reportList.clear();
                if(value.size()>0){
                    for(Job job: value){
                        scrollViewReport.setVisibility(View.VISIBLE);
                        textViewNoReport.setVisibility(View.GONE);
                        boolean kpi = util.passKpi(job.getCreatedTime(), job.getStartTime());
                        switch (job.getTypeOfJob()) {
                            case "X-Ray":
                                if(kpi){
                                    xrayPass+=1;
                                }
                                xray+=1;
                                break;
                            case "Labs":
                                if(kpi){
                                    labsPass+=1;
                                }
                                labs+=1;
                                break;
                            case "Discharge":
                                if(kpi){
                                    dischargePass+=1;
                                }
                                discharge+=1;
                                break;
                            case "Document":
                                if(kpi){
                                    documentPass+=1;
                                }
                                document+=1;
                                break;
                            case "Transport":
                                if(kpi){
                                    transportPass+=1;
                                }
                                transport+=1;
                                break;
                            case "Inpatient":
                                if(kpi){
                                    inpatientPass+=1;
                                }
                                inpatient+=1;
                                break;
                            case "Day Surgery":
                                if(kpi){
                                    daySurgeryPass+=1;
                                }
                                daySurgery+=1;
                                break;
                            case "Maternity":
                                if(kpi){
                                    maternityPass+=1;
                                }
                                maternity+=1;
                                break;
                        }
                        total +=1;
                    }
                    if (total ==0){
                        textViewNoReport.setVisibility(View.VISIBLE);
                        scrollViewReport.setVisibility(View.GONE);
                    }
                    int totalPass = xrayPass + labsPass + dischargePass + documentPass + transportPass + inpatientPass + daySurgeryPass + maternityPass;
                    if(total != 0){
                        double totalKpi = (Double.valueOf(totalPass)/total) * 100;
                        String totalKpiPercent = String.format("%.2f",totalKpi);
                        textViewTotalKPI.setText(totalKpiPercent);
                    }else{
                        textViewTotalKPI.setText(String.format("%.2f",Double.valueOf(total)));
                    }
                    if(xray != 0){
                        double xrayKpi = ((Double.valueOf(xrayPass)/xray)*100);
                        String xrayKpiPercent = String.format("%.2f",xrayKpi);
                        textViewXrayKpi.setText(xrayKpiPercent);
                    }else{
                        textViewXrayKpi.setText(String.format("%.2f",Double.valueOf(xray)));
                    }
                    if(labs != 0){
                        double labKpi = ((Double.valueOf(labsPass)/labs)*100);
                        String labKpiPercent = String.format("%.2f",labKpi);
                        textViewLabKpi.setText(labKpiPercent);
                    }else{
                        textViewLabKpi.setText(String.format("%.2f",Double.valueOf(labs)));
                    }
                    if(discharge != 0){
                        double dischargeKpi = ((Double.valueOf(dischargePass)/discharge)*100);
                        String dischargeKpiPercent = String.format("%.2f", dischargeKpi);
                        textViewDischargeKpi.setText(dischargeKpiPercent);
                    }else{
                        textViewDischargeKpi.setText(String.format("%.2f",Double.valueOf(discharge)));
                    }
                    if(document != 0){
                        double documentKpi = ((Double.valueOf(documentPass)/document)*100);
                        String documentKpiPercent = String.format("%.2f",documentKpi);
                        textViewDocumentKpi.setText(documentKpiPercent);
                    }else{
                        textViewDocumentKpi.setText(String.format("%.2f",Double.valueOf(document)));
                    }
                    if(transport != 0){
                        double transportKpi = ((Double.valueOf(transportPass)/transport)*100);
                        String transportKpiPercent = String.format("%.2f",transportKpi);
                        textViewTransportKpi.setText(transportKpiPercent);
                    }else{
                        textViewTransportKpi.setText(String.format("%.2f",Double.valueOf(transport)));
                    }
                    if(inpatient != 0){
                        double inpatientKpi = ((Double.valueOf(inpatientPass)/inpatient)*100);
                        String inpatientKpiPercent = String.format("%.2f",inpatientKpi);
                        textViewInpatientKpi.setText(inpatientKpiPercent);
                    }else{
                        textViewInpatientKpi.setText(String.format("%.2f",Double.valueOf(inpatient)));
                    }
                    if(daySurgery != 0){
                        double daySurgeryKpi = ((Double.valueOf(daySurgeryPass)/daySurgery)*100);
                        String daySurgeryKpiPercent = String.format("%.2f",daySurgeryKpi);
                        textViewDaySurgeryKpi.setText(daySurgeryKpiPercent);
                    }else{
                        textViewDaySurgeryKpi.setText(String.format("%.2f",Double.valueOf(daySurgery)));
                    }
                    if(maternity != 0){
                        double maternityKpi = ((Double.valueOf(maternityPass)/maternity)*100);
                        String maternityKpiPercent = String.format("%.2f",maternityKpi);
                        textViewMaternityKpi.setText(maternityKpiPercent);
                    }else{
                        textViewMaternityKpi.setText(String.format("%.2f",Double.valueOf(maternity)));
                    }
                    textViewTotal.setText(String.valueOf(total));
                    textViewXrayTotal.setText(String.valueOf(xray));
                    textViewLabTotal.setText(String.valueOf(labs));
                    textViewDischargeTotal.setText(String.valueOf(discharge));
                    textViewDocumentTotal.setText(String.valueOf(document));
                    textViewTransportTotal.setText(String.valueOf(transport));
                    textViewInpatientTotal.setText(String.valueOf(inpatient));
                    textViewOutpatientTotal.setText(String.valueOf(daySurgery));
                    textViewMaternityTotal.setText(String.valueOf(maternity));
                    Report reportXray = new Report();
                    reportXray.setTypeOfJob("X-Ray");
                    reportXray.setTotal(String.valueOf(xray));
                    reportXray.setKpi(textViewXrayKpi.getText().toString());
                    Report reportLabs = new Report();
                    reportLabs.setTypeOfJob("Labs");
                    reportLabs.setTotal(String.valueOf(labs));
                    reportLabs.setKpi(textViewLabKpi.getText().toString());
                    Report reportDischarge = new Report();
                    reportDischarge.setTypeOfJob("Discharge");
                    reportDischarge.setTotal(String.valueOf(discharge));
                    reportDischarge.setKpi(textViewDischargeKpi.getText().toString());
                    Report reportDocument = new Report();
                    reportDocument.setTypeOfJob("Document");
                    reportDocument.setTotal(String.valueOf(document));
                    reportDocument.setKpi(textViewDocumentKpi.getText().toString());
                    Report reportTransport = new Report();
                    reportTransport.setTypeOfJob("Transport");
                    reportTransport.setTotal(String.valueOf(transport));
                    reportTransport.setKpi(textViewTransportKpi.getText().toString());
                    Report reportInpatient = new Report();
                    reportInpatient.setTypeOfJob("Inpatient");
                    reportInpatient.setTotal(String.valueOf(inpatient));
                    reportInpatient.setKpi(textViewInpatientKpi.getText().toString());
                    Report reportDaySurgery = new Report();
                    reportDaySurgery.setTypeOfJob("Day Surgery");
                    reportDaySurgery.setTotal(String.valueOf(daySurgery));
                    reportDaySurgery.setKpi(textViewDaySurgeryKpi.getText().toString());
                    Report reportMaternity = new Report();
                    reportMaternity.setTypeOfJob("Maternity");
                    reportMaternity.setTotal(String.valueOf(maternity));
                    reportMaternity.setKpi(textViewMaternityKpi.getText().toString());
                    //Add all the report detail into a list
                    reportList.add(reportXray);
                    reportList.add(reportLabs);
                    reportList.add(reportDischarge);
                    reportList.add(reportDocument);
                    reportList.add(reportTransport);
                    reportList.add(reportInpatient);
                    reportList.add(reportDaySurgery);
                    reportList.add(reportMaternity);
                }else{
                    textViewNoReport.setVisibility(View.VISIBLE);
                    scrollViewReport.setVisibility(View.GONE);
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

    //region on click listener for all types of job
    public void document(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(getApplicationContext(), KpiReportActivity.class);
        intent.putExtra("Type", "Document");
        intent.putExtra("Month", String.valueOf(selectedMonth));
        intent.putExtra("Year", String.valueOf(selectedYear));
        startActivity(intent);
    }

    public void xray(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(getApplicationContext(), KpiReportActivity.class);
        intent.putExtra("Type", "X-Ray");
        intent.putExtra("Month", String.valueOf(selectedMonth));
        intent.putExtra("Year", String.valueOf(selectedYear));
        startActivity(intent);
    }

    public void discharge(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(getApplicationContext(), KpiReportActivity.class);
        intent.putExtra("Type", "Discharge");
        intent.putExtra("Month", String.valueOf(selectedMonth));
        intent.putExtra("Year", String.valueOf(selectedYear));
        startActivity(intent);
    }

    public void labs(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(getApplicationContext(), KpiReportActivity.class);
        intent.putExtra("Type", "Labs");
        intent.putExtra("Month", String.valueOf(selectedMonth));
        intent.putExtra("Year", String.valueOf(selectedYear));
        startActivity(intent);
    }

    public void transport(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(getApplicationContext(), KpiReportActivity.class);
        intent.putExtra("Type", "Transport");
        intent.putExtra("Month", String.valueOf(selectedMonth));
        intent.putExtra("Year", String.valueOf(selectedYear));
        startActivity(intent);
    }

    public void inpatient(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(getApplicationContext(), KpiReportActivity.class);
        intent.putExtra("Type", "Inpatient");
        intent.putExtra("Month", String.valueOf(selectedMonth));
        intent.putExtra("Year", String.valueOf(selectedYear));
        startActivity(intent);
    }

    public void daySurgery(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(getApplicationContext(), KpiReportActivity.class);
        intent.putExtra("Type", "Day Surgery");
        intent.putExtra("Month", String.valueOf(selectedMonth));
        intent.putExtra("Year", String.valueOf(selectedYear));
        startActivity(intent);
    }

    public void maternity(View view){
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;
        Intent intent = new Intent(getApplicationContext(), KpiReportActivity.class);
        intent.putExtra("Type", "Maternity");
        intent.putExtra("Month", String.valueOf(selectedMonth));
        intent.putExtra("Year", String.valueOf(selectedYear));
        startActivity(intent);
    }
//endregion on click listener for all types of job

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.export_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void thePorterReport() {
        userFirebase.getAllPorter(new UserCallBack() {
            @Override
            public void onCallBack(User value) { }

            @Override
            public void onCallBack(List<User> value) {
                for(User user: value){
                    totalJob1 =0; passJob1=0;
                    final String name = user.getName();
                    jobFirebase.getAllJobByUser("Porter", user.getStaffID(), new JobCallBack() {
                        @Override
                        public void onCallBack(Job value) {

                        }

                        @Override
                        public void onCallBack(List<Job> value) {
                            for(Job job: value){
                                String[] calend = job.getCreatedOn().split("-");
                                int month = Integer.parseInt(calend[1]);
                                Calendar now = Calendar.getInstance();
                                int year = Integer.parseInt(calend[2]);
                                if (String.valueOf(selectedMonth).equals(String.valueOf(month)) && String.valueOf(year).equals(String.valueOf(now.get(Calendar.YEAR)))) {
                                    if (job.getStatus().equals("Completed")){
                                        totalJob1 += 1;
                                        if(util.passKpi(job.getCreatedTime(), job.getStartTime())){
                                            passJob1 +=1;
                                        }
                                    }
                                }
                            }
                            if (totalJob1 !=0) {
                                double kpipercent = (Double.valueOf(passJob1) / totalJob1) * 100;
                                Report report = new Report();
                                report.setTypeOfJob(name);
                                report.setKpi(String.format("%.2f",kpipercent));
                                report.setTotal(String.valueOf(totalJob1));
                                porterList.add(report);
                                totalJob1=0;
                                passJob1 = 0;
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
            }

            @Override
            public void onCallBack(String value) { }

            @Override
            public void onCallBack(Boolean value) {

            }
        });
    }

    public void exportFunction(MenuItem item) {
        final String monthReport = spinnerMonth.getSelectedItem().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Send Report");
        builder.setMessage("Do you want to send " + monthReport + " Report?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jobFirebase.getAllCompletedJob(new JobCallBack() {
                            @Override
                            public void onCallBack(Job value) {

                            }

                            @Override
                            public void onCallBack(List<Job> value) {
                                scrollViewReport.setVisibility(View.VISIBLE);
                                textViewNoReport.setVisibility(View.GONE);
                                final List<Job> jobList = new ArrayList<>();
                                for (Job job : value) {
                                    String createdDate = job.getCreatedOn();
                                    String[] calend = createdDate.split("-");
                                    int month = Integer.parseInt(calend[1]);
                                    Calendar now = Calendar.getInstance();
                                    int year = Integer.parseInt(calend[2]);
                                    job.setAssigned(userController.getName(userList, job.getAssigned()));
                                    job.setAssignedBy(userController.getName(userList, job.getAssignedBy()));
                                    job.setCreatedBy(userController.getName(userList, job.getCreatedBy()));
                                    if ((String.valueOf(selectedMonth)).equals(String.valueOf(month)) && String.valueOf(year).equals(String.valueOf(selectedYear))) {
                                        jobList.add(job);
                                    }
                                }
                                String filename="";
                                try {
                                    filename = monthReport + "_Report";
                                    filename = filename.replace(" ", "_");
                                    util.generateReport(filename , jobList, reportList, porterList, TAG);
                                    Toast.makeText(getApplication(),
                                            monthReport + R.string.reportDownloaded, Toast.LENGTH_SHORT).show();
                                } catch (WriteException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if(Build.VERSION.SDK_INT>=24){
                                    try{
                                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                                        m.invoke(null);
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                File sdCard = Environment.getExternalStorageDirectory();
                                String PATH=sdCard.toString()+ "/" + filename + ".xls";
                                Intent intent=new Intent(Intent.ACTION_SEND);
                                intent.setType("xls/**");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
                                intent.putExtra(Intent.EXTRA_SUBJECT,monthReport + " Report");
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+PATH));
                                intent.putExtra(Intent.EXTRA_TEXT, "This is the report for " + monthReport + ".");
                                startActivity(Intent.createChooser(intent,""));
                            }

                            @Override
                            public void onCallBack(boolean value) {

                            }

                            @Override
                            public void onCallBack(String value) {

                            }
                        });
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //region permission for file storage
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish();
                startActivity(getIntent());
            } else {
                //permission denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //endregion permission for file storage
}
