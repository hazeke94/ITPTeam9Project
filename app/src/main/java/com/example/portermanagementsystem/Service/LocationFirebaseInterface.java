package com.example.portermanagementsystem.Service;

import com.example.portermanagementsystem.CallBack.LocationCallBack;
import com.example.portermanagementsystem.Model.Job;

public interface LocationFirebaseInterface {
    void getLocationBytype(final String type, final LocationCallBack callback);
    void locationValidate(final String location_id, final Job cjob, final LocationCallBack callBack);
}
