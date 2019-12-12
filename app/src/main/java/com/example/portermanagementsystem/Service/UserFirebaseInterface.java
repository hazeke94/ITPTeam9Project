package com.example.portermanagementsystem.Service;

import com.example.portermanagementsystem.CallBack.UserCallBack;
import com.example.portermanagementsystem.Model.User;

public interface UserFirebaseInterface {
    void getUserDetail(final String userId,final UserCallBack userCallBack);
    void updateFcmToken(String userId);
    void clearFcmtoken(final String userId);
    void getAllOnlinePorter(final UserCallBack userCallBack);
    void getAllPorter(final UserCallBack userCallBack);
    void getName(final String userId,final UserCallBack userCallBack);
    void getEmail(final String userId,final UserCallBack userCallBack);
    void updateStatus(final String userId, final String status);
    void falsePorterAvailability(String porterName);
    void resetAllPorter();
    void isValidQR(final String qrcode, final UserCallBack callBack);
    void getAllUser(final UserCallBack callBack);
    void deleteUser(final String userId);
    void updateStaff(final String userId, String role);
    String insertUser(User user);
}
