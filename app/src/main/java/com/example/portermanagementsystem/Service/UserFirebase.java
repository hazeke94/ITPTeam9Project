package com.example.portermanagementsystem.Service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.Notification.NotificationService;
import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static android.support.constraint.Constraints.TAG;

public class UserFirebase implements UserFirebaseInterface {
    private static final String TAG ="userFirebase" ;
    private DatabaseReference mDatabaseUser = FirebaseDatabase.getInstance().getReference("User");
    NotificationService ns = new NotificationService();


    public String insertUser(User user){
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("User");
        final String userID = mDatabaseUser.child("User").push().getKey();
        Log.d(TAG, "User ID: " +  user.getStaffID());
        mDatabaseUser.child(user.getStaffID()).setValue(user);
        return userID;
    }

    //Individual User Detail
    @Override
    public void getUserDetail(final String userId, final UserCallBack userCallBack){
        mDatabaseUser.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userCallBack.onCallBack(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Update Fcmtoken when user login
    @Override
    public void updateFcmToken(String userId) {
        Query usernameQuery = mDatabaseUser.orderByChild("staffID").equalTo(userId);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String token = ns.onTokenRefreshed();
                    //clear the token when user logout, validate to see token exist. if exist, clear the fcmtoken.
                    //then generate a new one.
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getFCMToken().equals(null)) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("fcmtoken", "");
                        updates.put("fcmtoken", token);
                        mDatabaseUser.child(dataSnapshot.getKey()).updateChildren(updates);
                    } else {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("fcmtoken", token);
                        mDatabaseUser.child(dataSnapshot.getKey()).updateChildren(updates);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Clear Fcmtoken
    @Override
    public void clearFcmtoken(final String userId){
        Query userQuery = mDatabaseUser.orderByChild("staffID").equalTo(userId);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().child("fcmtoken").setValue("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Get all online porter
    @Override
    public void getAllOnlinePorter(final UserCallBack userCallBack){
        Query usernameQuery = mDatabaseUser.orderByChild("role").equalTo("Porter");
        usernameQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("status").getValue().equals("online") || snapshot.child("status").getValue().equals("busy")) {
                        User user = snapshot.getValue(User.class);
                        userList.add(user);
                    }
                }
                userCallBack.onCallBack(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Get all porter
    @Override
    public void getAllPorter(final UserCallBack userCallBack){
        Query usernameQuery = mDatabaseUser.orderByChild("role").equalTo("Porter");
        usernameQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }
                userCallBack.onCallBack(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Get staff name
    @Override
    public void getName(final String userId,final UserCallBack userCallBack){
        if(userId !=null



        ) {
            mDatabaseUser.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User staff = dataSnapshot.getValue(User.class);
//                    if (staff.getStaffID().equals(userId)) {
//                        String name = staff.getName();
                    userCallBack.onCallBack(staff);
//                        break;
//                    }
//                }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    //Get staff email
    @Override
    public void getEmail(final String userId,final UserCallBack userCallBack){
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User staff = data.getValue(User.class);
                    if (staff.getStaffID().equals(userId)) {
                        String email = staff.getEmail();
                        userCallBack.onCallBack(email);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Update porter status
    @Override
    public void updateStatus(final String userId, final String status){
        Query porterQuery = mDatabaseUser.orderByChild("staffID").equalTo(userId);
        porterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DatabaseReference porterRef = dataSnapshot.getRef().child("status");
                    porterRef.setValue(status);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void falsePorterAvailability(String porterName) {
        Log.d("userFirebase",porterName);
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Job");
        Query porterQuery = mDatabaseUser.orderByChild("name").equalTo(porterName);
        porterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    final String userID = user.getStaffID();
                    Log.d("userFirebase",userID);
                    if (userID != null){
                        Query porterQueryJob = mDatabase.orderByChild("assigned").equalTo(userID);
                        porterQueryJob.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshotJob) {
                                //check if name exist in jobs
                                boolean exist = false;
                                for (DataSnapshot SnapshotJob : dataSnapshotJob.getChildren()) {
                                    if (userID.equals(SnapshotJob.child("assigned").getValue())) {
                                        exist = true;
                                        //exist then check job status
                                        if ("Assigned".equals(SnapshotJob.child("status").getValue())) {
                                            DatabaseReference porterRef = SnapshotJob.getRef().child("assigned");
                                            DatabaseReference porterRefJobStatus = SnapshotJob.getRef().child("status");
                                            porterRef.setValue("");
                                            porterRefJobStatus.setValue("Pending");
                                            DatabaseReference porterStatusRef = dataSnapshot.getRef().child("status");
                                            porterStatusRef.setValue("offline");
                                            Log.d(TAG,userID + "assigned offline");
                                        }
                                        else
                                        {
                                            Log.d(TAG,userID + "offline");
                                            DatabaseReference porterStatusRef = dataSnapshot.getRef().child("status");
                                            porterStatusRef.setValue("offline");
                                        }
                                    }
                                }
                                if(!exist){
                                    Log.d(TAG,userID + "offline");
                                    DatabaseReference porterStatusRef = dataSnapshot.getRef().child("status");
                                    porterStatusRef.setValue("offline");
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Reset all porter to offline
    @Override
    public void resetAllPorter(){
        Query porterQuery = mDatabaseUser;
        porterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getStatus().equals("busy")) {
                        DatabaseReference porterRef = dataSnapshot.getRef().child("status");
                        porterRef.setValue("offline");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void isValidQR(final String qrcode, final UserCallBack callBack){
        final TaskCompletionSource<Boolean> tcs = new TaskCompletionSource();
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean valid = false;
                String staffID;

                for (DataSnapshot qrObject : dataSnapshot.getChildren()) {
                    staffID = qrObject.child("staffID").getValue().toString();

                    if(staffID.equals(qrcode)){
                        valid = true;
                        callBack.onCallBack(valid);
                        System.out.println("Result is " + valid);
                        break;
                    }
                }
                callBack.onCallBack(valid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void getAllUser(final UserCallBack userCallBack) {
        Query usernameQuery = mDatabaseUser;
        usernameQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }
                userCallBack.onCallBack(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void deleteUser(String userId) {
//        DatabaseReference userRef = mDatabaseUser.child("User");
        mDatabaseUser.child(userId).removeValue();

    }

    @Override
    public void updateStaff(String userId, final String role) {
        Query porterQuery = mDatabaseUser.orderByChild("staffID").equalTo(userId);
        porterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DatabaseReference porterRef = dataSnapshot.getRef().child("role");
                    porterRef.setValue(role);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
