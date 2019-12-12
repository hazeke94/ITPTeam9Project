package com.example.portermanagementsystem.Activity.CreateJob;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.portermanagementsystem.R;

public class CreateJob2Activity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    RadioGroup radioGroupAdhoc, radioGroupAdmission;
    TextView admissionTv, adhocTv;
    String selectedId = "";

    private static final long CLICK_TIME_INTERVAL = 300;
    private long mLastClickTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job_2);
        radioGroupAdhoc = findViewById(R.id.radioGroupAdHoc);
        radioGroupAdmission = findViewById(R.id.radioGroupAdmission);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String role = sharedpreferences.getString("Role", null);
        if (role.equals("Porter")){
            radioGroupAdhoc.setVisibility(View.GONE);
            radioGroupAdmission.setVisibility(View.VISIBLE);
            selectedId = selectedId.replace(selectedId, String.valueOf(radioGroupAdmission.getCheckedRadioButtonId()));
        }
        else{
            radioGroupAdmission.setVisibility(View.GONE);
            radioGroupAdhoc.setVisibility(View.VISIBLE);
            selectedId = selectedId.replace(selectedId, String.valueOf(radioGroupAdhoc.getCheckedRadioButtonId()));
        }
    }

    public void nextPage(View view){
        String role = sharedpreferences.getString("Role", null);
        admissionTv = findViewById(R.id.admissionTv);
        adhocTv = findViewById(R.id.adhocTv);
        if (role.equals("Porter")){
            selectedId = selectedId.replace(selectedId, String.valueOf(radioGroupAdmission.getCheckedRadioButtonId()));
        }
        else{
            selectedId = selectedId.replace(selectedId, String.valueOf(radioGroupAdhoc.getCheckedRadioButtonId()));
        }
        RadioButton radioButtonType = findViewById(Integer.parseInt(selectedId));
        if (radioButtonType == null){
            admissionTv.requestFocus();
            admissionTv.setError(getText(R.string.jobNull));
            adhocTv.requestFocus();
            adhocTv.setError(getText(R.string.jobNull));
        }
        else {
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;
            String typeOfJob = radioButtonType.getText().toString();
            Intent nextLastPage = new Intent(getApplicationContext(), CreateJobFinalActivity.class);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("typeOfJob", typeOfJob);
            editor.apply();
            startActivity(nextLastPage);
        }
    }
}
