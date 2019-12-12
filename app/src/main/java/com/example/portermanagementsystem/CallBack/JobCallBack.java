package com.example.portermanagementsystem.CallBack;
import com.example.portermanagementsystem.Model.Job;

import java.util.List;

public interface JobCallBack {
    void onCallBack(Job value);
    void onCallBack(List<Job> value);
    void onCallBack(boolean value);
    void onCallBack(String value);
}
