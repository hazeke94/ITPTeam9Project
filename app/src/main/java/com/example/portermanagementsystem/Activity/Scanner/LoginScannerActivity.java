package com.example.portermanagementsystem.Activity.Scanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.portermanagementsystem.Activity.LoginActivity;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Util.util;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.List;

public class LoginScannerActivity extends AppCompatActivity {

    private SurfaceView cameraPreview;
    private boolean qrFlag;
    UserFirebaseInterface userFirebase = new UserFirebase();

    String[] PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_job);

        // Initialize UI
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
                // check permissions first, but should have been granted
                if (ActivityCompat.checkSelfPermission(LoginScannerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                //fetchLocation();
                if (barcodeArray.size()> 0 && !qrFlag) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            //Prevent another scan detection
                            qrFlag = true;
                            final String barcodeValue = barcodeArray.valueAt(0).displayValue;
                            if (!barcodeValue.equals("")) {
                                System.out.println("Value is " + barcodeValue);
                                //Implement Logic to check if ID exist in database

                                //Implement Logic to validate qrcode
                                userFirebase.isValidQR(barcodeValue, new UserCallBack() {
                                    @Override
                                    public void onCallBack(User value) {

                                    }

                                    @Override
                                    public void onCallBack(List<User> value) {

                                    }

                                    @Override
                                    public void onCallBack(String value) {

                                    }

                                    @Override
                                    public void onCallBack(Boolean value) {
                                        if(value) {
                                            //intent to completeJob Activity
                                            Intent intent = new Intent(LoginScannerActivity.this, LoginActivity.class);
                                            intent.putExtra("barcode_id", barcodeValue);
                                            startActivityForResult(intent, 1);
                                            finish();
                                        }
                                        else{
                                            qrFlag = false;
                                            System.out.println("Failed : Invalid User");
                                        }
                                    }
                                });


                            }
                            else {
                                //to allow more qr scanning since qrInvalid
                                qrFlag = false;
                            }
                        }
                    };
                    thread.start();
                }
            }
        });
    }
}
