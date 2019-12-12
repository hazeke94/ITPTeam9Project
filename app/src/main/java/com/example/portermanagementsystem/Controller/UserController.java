package com.example.portermanagementsystem.Controller;

import android.util.Log;

import com.example.portermanagementsystem.Model.Job;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.Model.User;

import java.util.List;

public class UserController implements UserControllerInterface{
    private static final String TAG = "UserController";
    UserFirebaseInterface userFirebase = new UserFirebase();

    @Override
    public void updatePorterStatus(String userId, String status){
        if(!userId.isEmpty()) {
            userFirebase.updateStatus(userId, status);
        }else{
            throw new NullPointerException("Staff ID is empty!");
        }
    }

    @Override
    public void clearFcmToken(String userId) {
        if(userId !=null) {
            userFirebase.clearFcmtoken(userId);
        }else{
            throw new NullPointerException("Staff ID is empty!");
        }
    }

    @Override
    public void resetAllPorter(){
        try {
            userFirebase.resetAllPorter();
        } catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void setfalsePorterAvailability(String porterID) {
        userFirebase.falsePorterAvailability(porterID);
    }

    public String getName(List<User> userList,String userId){
        for(User user: userList){
            if(user.getStaffID().equals(userId)){
                return user.name;
            }
        }
        return "-";
    }

    @Override
    public String insertUser(User user) {
        return userFirebase.insertUser(user);
    }

    @Override
    public void deleteUser(String userId) {
        userFirebase.deleteUser(userId);
    }

    @Override
    public void updateStaff(String userId, String role) {
        userFirebase.updateStaff(userId,role);
    }

}
