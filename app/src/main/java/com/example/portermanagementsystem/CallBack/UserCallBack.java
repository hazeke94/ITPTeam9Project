package com.example.portermanagementsystem.CallBack;

import com.example.portermanagementsystem.Model.User;

import java.util.List;

import jxl.write.WriteException;

public interface UserCallBack {
    void onCallBack(User value);
    void onCallBack(List<User> value);
    void onCallBack(String value);
    void onCallBack(Boolean value);
}
