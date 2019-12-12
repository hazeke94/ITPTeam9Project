package com.example.portermanagementsystem.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Model.User;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


public class PorterListAdapter extends RecyclerView.Adapter<PorterListAdapter.PorterViewHolder> {
    private Context context;
    List<User> mUsers;
    private DatabaseReference mDatabase;
    UserFirebaseInterface userFirebase = new UserFirebase();
    UserControllerInterface userController = new UserController();

    public PorterListAdapter(List<User> userList, Context context) {
        this.mUsers = userList;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    private final LayoutInflater mInflater;

    @Override
    public PorterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.porter_list_item, parent, false);
        return new PorterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PorterViewHolder porterViewHolder,int position) {
        if (mUsers != null) {
            User user = mUsers.get(position);

            porterViewHolder.textViewEmpName.setText(user.getName());
            porterViewHolder.textViewEmpGender.setText(user.getGender());
            if("online".equals(user.getStatus())){
                porterViewHolder.switchAvailability.setChecked(true);
                porterViewHolder.imageViewFace.setImageResource(R.drawable.ic_sentiment_satisfied_black_24dp);
                porterViewHolder.textViewStatus.setTextColor(Color.parseColor("#00B894"));
                porterViewHolder.textViewStatus.setText(user.getStatus());
            }
            else if ("busy".equals(user.getStatus())){
                porterViewHolder.switchAvailability.setChecked(true);
                porterViewHolder.imageViewFace.setImageResource(R.drawable.ic_sentiment_satisfied_busy_24dp);
                porterViewHolder.textViewStatus.setTextColor(Color.parseColor("#ff7675"));
                porterViewHolder.textViewStatus.setText(user.getStatus());
            }
            else{
                porterViewHolder.switchAvailability.setChecked(false);
                porterViewHolder.imageViewFace.setImageResource(R.drawable.ic_sentiment_neutral_black_24dp);
                porterViewHolder.textViewStatus.setText(user.getStatus());
            }

            porterViewHolder.switchAvailability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    User user = mUsers.get(porterViewHolder.getAdapterPosition());
                    Log.d("PorterListAdapter", "Hello" + user.getStaffID());
                    String porterName = user.getName();
                    if (isChecked) {
                        userFirebase.updateStatus(user.getStaffID(), "online");
                    } else {
                        if (user.getStatus().equals("busy")){
                            porterViewHolder.switchAvailability.setChecked(true);
                            Toast.makeText(context, "Hold On! Job is in progress.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d("PorterListAdapter", "Hello" + porterName);
                            userController.setfalsePorterAvailability(porterName);
                        }
                    }
                }
            });

        } else {
            // Covers the case of data not being ready yet.
            porterViewHolder.textViewEmpName.setText("No User yet");
        }
    }

    @Override
    public int getItemCount() {
        if (mUsers != null){
            return mUsers.size();
        }
        else {
            return 0;
        }
    }

    public class PorterViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewEmpName, textViewEmpGender, textViewStatus;
        private final Switch switchAvailability;
        private final ImageView imageViewFace;

        private PorterViewHolder(View itemView) {
            super(itemView);
            textViewEmpName = itemView.findViewById(R.id.textViewEmpName);
            textViewEmpGender = itemView.findViewById(R.id.textViewEmpGender);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            switchAvailability = itemView.findViewById(R.id.switchAvailability);
            imageViewFace = itemView.findViewById(R.id.imageViewFace);
        }
    }
}



