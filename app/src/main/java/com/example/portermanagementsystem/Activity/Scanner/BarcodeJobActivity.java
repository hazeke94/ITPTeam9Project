package com.example.portermanagementsystem.Activity.Scanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.portermanagementsystem.Activity.CreateJob.CreateJob1Activity;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Util.util;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class BarcodeJobActivity extends AppCompatActivity {

    private static final String TAG = "ScannerJobActivity";
    private SurfaceView cameraPreview;
    private boolean qrFlag;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String role;
    JobControllerInterface jobController = new JobController();

    //Ask for permission to use the camera
    String[] PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_job);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        role = sharedpreferences.getString("Role", null);
        initUI();
        createCameraSource();

        // Request permissions if user has denied previously
        if (!util.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
    }

    /**
     * Initialise UI elements from XML
     */
    private void initUI() {
        cameraPreview = findViewById(R.id.textureCamera);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish();
                startActivity(getIntent());
            } else {
                //permission denied
            }
        }
    }

    // create the camera texture on the UI
    private void createCameraSource() {
        BarcodeDetector bd = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, bd)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920, 1080)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                // check permissions first, but should have been grandted
                if (ActivityCompat.checkSelfPermission(BarcodeJobActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        bd.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodeArray = detections.getDetectedItems();
                if (barcodeArray.size()> 0 && !qrFlag) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            //Prevent another scan detection
                            qrFlag = true;
                            final String barcodeValue = barcodeArray.valueAt(0).displayValue;
                            boolean validCase;
                            //If qr code is in db
                            Log.d(TAG, barcodeValue);
                            if (!barcodeValue.equals("")) {
                                System.out.println("Value is " + barcodeValue);

                                //Implement Logic to validate qrcode
                                validCase = jobController.validateCaseID(barcodeValue, role);
                                if(validCase) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(BarcodeJobActivity.this, "Valid CaseID : " + barcodeValue, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(BarcodeJobActivity.this, CreateJob1Activity.class);
                                            intent.putExtra("CaseID",barcodeValue);
                                            startActivityForResult(intent, 1);
                                            finish();
                                        }
                                    });
                                }
                                else{
                                    qrFlag = false;
                                    System.out.println("Invalid Case ID");
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(BarcodeJobActivity.this, "Invalid Case ID", Toast.LENGTH_SHORT).show();
                                            //Bring back to job creation page
                                            Intent intent = new Intent(BarcodeJobActivity.this, CreateJob1Activity.class);
                                            startActivityForResult(intent, 1);
                                            finish();
                                        }
                                    });
                                }
                            }
                        }
                    };
                    thread.start();
                }
            }
        });
    }
}
