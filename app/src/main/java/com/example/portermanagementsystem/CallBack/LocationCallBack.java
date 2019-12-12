package com.example.portermanagementsystem.CallBack;

import com.example.portermanagementsystem.Model.Location;
import java.util.List;

public interface LocationCallBack {
    void onCallBack(List<String> value);
    void onCallBack(boolean result);
}
