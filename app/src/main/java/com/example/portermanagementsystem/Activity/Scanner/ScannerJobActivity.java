package com.example.portermanagementsystem.Activity.Scanner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.Controller.JobController;
import com.example.portermanagementsystem.Controller.JobControllerInterface;
import com.example.portermanagementsystem.Service.LocationFirebase;
import com.example.portermanagementsystem.Service.LocationFirebaseInterface;
import com.example.portermanagementsystem.CallBack.LocationCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Util.util;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.List;

public class ScannerJobActivity extends AppCompatActivity {
    private static final String TAG = "ScannerJobActivity";
    private SurfaceView cameraPreview;
    private boolean qrFlag;
    Job cjob;
    JobControllerInterface jobController = new JobController();
    LocationFirebaseInterface locationFirebase = new LocationFirebase();

    //Ask for permission to use the camera
    String[] PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_job);
        Intent intent = getIntent();
        cjob = intent.getParcelableExtra("CurrentJob");
        // Initialize UI
        initUI();
        if (cjob.getStatus().equals("Acknowledged")){
            setTitle(cjob.getFromLocation());
        }else if(cjob.getStatus().equals("Pick Up")) {
            setTitle(cjob.getToLocation());
        }
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
                if (ActivityCompat.checkSelfPermission(ScannerJobActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                            boolean validDestination;
                            //If qr code is in db
                            Log.d(TAG, barcodeValue);
                            if (!barcodeValue.equals("")) {
                                //Implement Logic to validate qrcode
                                //validDestination = validateQR(barcodeValue);
                                locationFirebase.locationValidate(barcodeValue,cjob ,new LocationCallBack() {
                                    @Override
                                    public void onCallBack(List<String> value) {

                                    }

                                    @Override
                                    public void onCallBack(boolean result) {
                                        //validDestination = result;
                                        Log.d(TAG, "Callback result location validator: "+ result);
                                        if(result) {
                                            //dialog to verify job completion
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ScannerJobActivity.this);
                                                    builder.setCancelable(true);
                                                    switch(cjob.getStatus()){
                                                        case "Acknowledged":
                                                            builder.setTitle("Ready Confirmation");
                                                            builder.setMessage("Are you ready?");
                                                            break;
                                                        case "Pick Up":
                                                            builder.setTitle("Arrive Confirmation");
                                                            builder.setMessage("I have arrive.");
                                                            break;
                                                    }
                                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //intent to completeJob Activity
                                                            Log.d(TAG,"scan okay");
                                                            dialog.dismiss();
                                                            jobController.updateJobStatus(cjob);
                                                            Intent intent = new Intent(ScannerJobActivity.this, PorterDashboardActivity.class);
                                                            startActivityForResult(intent, 1);
                                                            finish();
                                                        }
                                                    });
                                                    builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            Log.d(TAG,"cancel scan");
                                                            finishAfterTransition();
                                                            overridePendingTransition(0, 0);
                                                            qrFlag = false;

                                                        }
                                                    });

                                                    AlertDialog dialog = builder.create();
                                                    dialog.setCanceledOnTouchOutside (false);
                                                    dialog.show();
                                                }
                                            });
                                        }
                                        else{
                                            System.out.println("Failed : Invalid Destination, are you at the correct destination?");
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ScannerJobActivity.this);
                                                    builder.setMessage("Invalid Destination, are you at the correct destination?");
                                                    // Add the buttons
                                                    builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // User clicked OK button
                                                            dialog.dismiss();
                                                            qrFlag = false;
                                                            Log.d(TAG,"Invalid Destination");
                                                            finishAfterTransition();
                                                            overridePendingTransition(0, 0);
                                                        }
                                                    });
                                                    // Create the AlertDialog
                                                    try {
                                                        AlertDialog dialog = builder.create();
                                                        dialog.setCanceledOnTouchOutside (false);
                                                        dialog.show();
                                                    }
                                                    catch (WindowManager.BadTokenException e) {
                                                        Log.d(TAG,"Invalid Destination");
                                                    }



                                                }
                                            });

                                        }
                                    }
                                });
                            }
                        }
                    };
                    thread.start();
                }
            }
        });
    }
}
