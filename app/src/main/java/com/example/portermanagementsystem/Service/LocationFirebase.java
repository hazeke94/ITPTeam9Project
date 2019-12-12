package com.example.portermanagementsystem.Service;

import android.support.annotation.NonNull;

import com.example.portermanagementsystem.CallBack.LocationCallBack;
import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Model.Location;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LocationFirebase implements LocationFirebaseInterface {
    private DatabaseReference mDatabaseLocation;
    @Override
    public void getLocationBytype(final String type, final LocationCallBack callback){

        mDatabaseLocation = FirebaseDatabase.getInstance().getReference("Location");
        mDatabaseLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> locationList = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.child(type).getValue().equals(true)) {
                        Location location = data.getValue(Location.class);
                        String level = location.getLevel();
                        String locationName = location.getLocationName();
                        String stn = location.getStn();
                        if (stn.equals("")) {
                            String combined = level + ", " + locationName;
                            if (combined != null) {
                                locationList.add(combined);
                            }
                        } else {
                            String combined = level + ", " + locationName + ", " + stn;
                            if (combined != null) {
                                locationList.add(combined);
                            }
                        }
                    }
                }
                callback.onCallBack(locationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void locationValidate(String location_id, final Job cjob, final LocationCallBack callBack) {
        mDatabaseLocation = FirebaseDatabase.getInstance().getReference("Location").child(location_id);
        mDatabaseLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String locationName, station;
                if(dataSnapshot.exists()) {
                    locationName = dataSnapshot.child("locationName").getValue().toString();
                    station = dataSnapshot.child("stn").getValue().toString();
                    if (cjob.getStatus().equals("Acknowledged")) {
                        String[] locationsplit = cjob.getFromLocation().split(",");
                        if (locationsplit.length == 2) {
                            String cLocation = locationsplit[1].substring(1);
//                        Log.d(TAG, "From Location " + cLocation);
                            if (cLocation.equals(locationName)) {
                                callBack.onCallBack(true);
                            } else {
                                callBack.onCallBack(false);
                            }
                        } else {
                            String cLocation = locationsplit[1].substring(1);
//                        Log.d(TAG, "From Location " + cLocation);
                            String cStation = locationsplit[2].substring(1);
//                        Log.d(TAG, "From Station " + cStation);
                            if (cLocation.equals(locationName) && cStation.equals(station)) {
                                callBack.onCallBack(true);
                            } else {
                                callBack.onCallBack(false);
                            }
                        }
                    } else {
                        String[] locationsplit = cjob.getToLocation().split(",");
                        if (locationsplit.length == 2) {
                            locationsplit = cjob.getToLocation().split(",");
                            String cLocation = locationsplit[1].substring(1);
//                        Log.d(TAG, "To Location ack " + cLocation);
                            if (cLocation.equals(locationName)) {
                                callBack.onCallBack(true);
                            } else {
                                callBack.onCallBack(false);
                            }
                        } else {
                            String cLocation = locationsplit[1].substring(1);
//                        Log.d(TAG, "To Location " + cLocation);
                            String cStation = locationsplit[2].substring(1);
//                        Log.d(TAG, "To Station " + cStation);
                            if (cLocation.equals(locationName) && cStation.equals(station)) {
                                callBack.onCallBack(true);
                            } else {
                                callBack.onCallBack(false);
                            }
                        }
                    }
                }
                else{
                    callBack.onCallBack(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
