package com.example.portermanagementsystem.Controller;

import com.example.portermanagementsystem.Model.User;

import java.util.List;

public interface UserControllerInterface {
    void updatePorterStatus(String userId, String status);
    void clearFcmToken(String userId);
    void resetAllPorter();
    void setfalsePorterAvailability(String porterID);
    String getName(List<User> userList, String userId);
    String insertUser (User user);
    void deleteUser(String userId);
    void updateStaff(String userId, String role);
}