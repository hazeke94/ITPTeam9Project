package com.example.portermanagementsystem.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Activity.Dashboard.ReceptionistDashboardActivity;
import com.example.portermanagementsystem.Activity.Scanner.NurseScannerActivity;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Service.JobFirebase;
import com.example.portermanagementsystem.Service.JobFirebaseInterface;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.CallBack.JobCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Util.util;

import java.util.List;

public class RemarkActivity extends AppCompatActivity {
    private static final String TAG = "RemarkActivity";
    ImageView imageViewSign;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    TextView MessageTextView, nurseIDTextView, addRemarks;
    EditText editTextRemark, editTextNurseID;
    Button patientNoButton, nurseNoButton, submitBtn;
    String JobID, staffName, status, nurseID, textRemark, role;
    String finalTextRemark = "";
    String failPass = "";
    JobFirebaseInterface jobFirebase = new JobFirebase();
    UserFirebaseInterface userFirebase = new UserFirebase();
    JobControllerInterface jobController = new JobController();
    UserControllerInterface userController = new UserController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);
        init();
        role = sharedpreferences.getString("Role", null);
        Log.d(TAG, "ROLE" + role);
        String nurseID = getIntent().getStringExtra("CaseID");
        if(nurseID != null){
            editTextNurseID.setText(nurseID);
        }
        jobFirebase.getJobDetail(JobID, new JobCallBack(){

            @Override
            public void onCallBack(Job value) {
                if(status.equals("Completed")){
                    nurseIDTextView.setVisibility(View.VISIBLE);
                    editTextNurseID.setVisibility(View.VISIBLE);
                    addRemarks.setVisibility(View.GONE);
                    patientNoButton.setVisibility(View.GONE);
                    nurseNoButton.setVisibility(View.GONE);
                    try {
                        boolean kpi = util.passKpi(String.valueOf(value.getCreatedTime()), String.valueOf(value.getStartTime()));
                        if (kpi){
                            imageViewSign.setImageResource(R.drawable.ic_star_black_24dp);
                            MessageTextView.setText(R.string.meetKPI);
                            editTextRemark.setHint(R.string.hint_remarks);
                            failPass = failPass.replace(failPass, "pass");
                        }
                        else {
                            imageViewSign.setImageResource(R.drawable.ic_star_half_black_24dp);
                            MessageTextView.setText(R.string.noMeetKPI);
                            editTextRemark.setHint(R.string.hint_must);
                            failPass = failPass.replace(failPass, "fail");
                        }
                    }
                    catch (Exception e) {
                        Log.d(TAG, "Error in completing job");
                    }
                }else if(status.equals("Cancelled")){
                    nurseIDTextView.setVisibility(View.GONE);
                    editTextNurseID.setVisibility(View.GONE);
                    imageViewSign.setImageResource(R.drawable.ic_warning_black_24dp);
                    MessageTextView.setText(R.string.cancelledReason);
                }
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

        editTextNurseID.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (editTextNurseID.getRight() - editTextNurseID.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Intent intent = new Intent(RemarkActivity.this, NurseScannerActivity.class);
                        intent.putExtra("JobID",JobID);
                        Log.d("JobID",JobID);
                        intent.putExtra("staffName",staffName);
                        intent.putExtra("status",status);
                        startActivityForResult(intent, 1);
                    }
                }
                return false;
            }
        });
    }

    //submit button onclick function
    public void updateRemark(View view){
        if (status.equals("Completed")){
            nurseID = editTextNurseID.getText().toString();
            textRemark = editTextRemark.getText().toString();
            StringBuffer sb = new StringBuffer();
            sb.delete(0, sb.length());
            if (!nurseID.equals("")){
                sb.append("Checked by " + nurseID);
                if(failPass.equals("pass")){
                    if (!textRemark.equals("")){
                        finalTextRemark = finalTextRemark.replace(finalTextRemark, textRemark);
                        sb.append(" " + finalTextRemark);
                        String totalRemarks = String.valueOf(sb);
                        updateRemarks(totalRemarks, "Completed");
                    }
                    else {
                        String totalRemarks = String.valueOf(sb);
                        updateRemarks(totalRemarks, "Completed");
                    }
                }
                else if (failPass.equals("fail")){
                    if (!textRemark.equals("")){
                        finalTextRemark = finalTextRemark.replace(finalTextRemark, textRemark);
                        sb.append(" " + finalTextRemark);
                        String totalRemarks = String.valueOf(sb);
                        updateRemarks(totalRemarks, "Completed");

                    }
                    else {
                        editTextRemark.setError(getText(R.string.remarkCompulsory));
                    }
                }
            }
            else {
                editTextNurseID.setError(getText(R.string.remarkCompulsory));
                //Toast.makeText(getApplicationContext(), "Please input nurse ID", Toast.LENGTH_SHORT).show();
            }
        }
        else if (status.equals("Cancelled")){
            textRemark = editTextRemark.getText().toString();

            if (!textRemark.equals("")){
                finalTextRemark = finalTextRemark.replace(finalTextRemark, textRemark);
                updateRemarks(finalTextRemark, "Cancelled");
            }
            else {
                if (!finalTextRemark.equals("")){
                    updateRemarks(finalTextRemark, "Cancelled");
                }
                else {
                    editTextRemark.setError(getText(R.string.remarkCompulsory));
                    //Toast.makeText(getApplicationContext(), "Please input or select remark", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected void init(){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        nurseIDTextView = findViewById(R.id.nurseIDTextView);
        editTextNurseID = findViewById(R.id.editTextNurseID);
        editTextRemark = findViewById(R.id.editTextRemark);
        imageViewSign = findViewById(R.id.imageViewSign);
        MessageTextView = findViewById(R.id.MessageTextView);
        patientNoButton = findViewById(R.id.patientNoButton);
        nurseNoButton = findViewById(R.id.nurseNoButton);
        submitBtn = findViewById(R.id.submitBtn);
        addRemarks = findViewById(R.id.addRemarks);
        Intent intent = getIntent();
        JobID = intent.getStringExtra("JobID");
        staffName = intent.getStringExtra("staffName");
        status = intent.getStringExtra("status");
    }

    private void updateRemarks(final String editTextRemark,final String status) {
        jobFirebase.getJobDetail(JobID, new JobCallBack() {
            @Override
            public void onCallBack(Job value) {
                String porterID = value.getAssigned();
                if(status.equals("Completed")){
                    jobController.updateJobStatus(value);
                }
                if (staffName == null){
                    String remarks = "R000" + ": " + editTextRemark;
                    ProgressDialog mDialog = new ProgressDialog(RemarkActivity.this);
                    mDialog.setMessage("Updating job status. Please hold.");
                    mDialog.setIndeterminate(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setCancelable(false);
                    mDialog.show();
                    jobController.updateRemarks(JobID, remarks, status);
                    userController.updatePorterStatus(porterID, "online");
                    if(role.equals("Porter")){
                        Intent intent = new Intent(RemarkActivity.this, PorterDashboardActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Log.d(TAG, "Cancelling " + value.getCaseID());
                        util.cancelWork(value.getCaseID());
                        Intent intent = new Intent(RemarkActivity.this, ReceptionistDashboardActivity.class);
                        startActivity(intent);
                    }
                }
                else
                {
                    String remarks = staffName + ": " + editTextRemark;
                    ProgressDialog mDialog = new ProgressDialog(RemarkActivity.this);
                    mDialog.setMessage("Updating job status. Please hold.");
                    mDialog.setIndeterminate(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setCancelable(false);
                    mDialog.show();
                    jobController.updateRemarks(JobID, remarks, status);
                    userController.updatePorterStatus(porterID, "online");
                    if(role.equals("Porter")){
                        Intent intent = new Intent(RemarkActivity.this, PorterDashboardActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Log.d(TAG, "Cancelling " + value.getCaseID());
                        util.cancelWork(value.getCaseID());
                        Intent intent = new Intent(RemarkActivity.this, ReceptionistDashboardActivity.class);
                        startActivity(intent);
                    }
                }

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

    public void updatePatientNotReady(View view) {
        if (finalTextRemark.equals(patientNoButton.getText().toString())){
            finalTextRemark = finalTextRemark.replace(finalTextRemark, "");
            editTextRemark.setText("");
            patientNoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        else {
            finalTextRemark = finalTextRemark.replace(finalTextRemark, patientNoButton.getText().toString());
            patientNoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_white_24dp, 0);
            nurseNoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            editTextRemark.setText("");
        }
    }

    public void updateNurseNotReady(View view) {
        if (finalTextRemark.equals(nurseNoButton.getText().toString())){
            nurseNoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            editTextRemark.setText("");
            finalTextRemark = finalTextRemark.replace(finalTextRemark, "");
        }
        else {
            finalTextRemark = finalTextRemark.replace(finalTextRemark, nurseNoButton.getText().toString());
            patientNoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            nurseNoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_white_24dp, 0);
            editTextRemark.setText("");
        }
    }

    public void removeCheck(View view) {
        patientNoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        nurseNoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
 }
