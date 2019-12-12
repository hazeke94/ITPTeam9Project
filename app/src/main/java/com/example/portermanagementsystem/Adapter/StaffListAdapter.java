package com.example.portermanagementsystem.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.portermanagementsystem.Activity.StaffDetailActivity;
import com.example.portermanagementsystem.Controller.UserController;
import com.example.portermanagementsystem.Controller.UserControllerInterface;
import com.example.portermanagementsystem.Model.User;
import com.example.portermanagementsystem.R;
import com.example.portermanagementsystem.Service.UserFirebase;
import com.example.portermanagementsystem.Service.UserFirebaseInterface;

import java.util.List;

public class StaffListAdapter extends RecyclerView.Adapter<StaffListAdapter.ViewHolder> {
    private Context context;
    List<User> mUsers;

    UserFirebaseInterface userFirebase = new UserFirebase();
    UserControllerInterface userController = new UserController();
    private int position = -1;

    public StaffListAdapter(List<User> userList, Context context) {
        this.mUsers = userList;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    private final LayoutInflater mInflater;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.staff_list_item, viewGroup, false);
        return new StaffListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (mUsers != null) {
            User user = mUsers.get(position);

            viewHolder.textViewEmpName.setText(user.getName());
            viewHolder.textViewRole.setText(user.getRole());
            viewHolder.switchAvailability.setVisibility(View.INVISIBLE);

        } else {
            // Covers the case of data not being ready yet.
            viewHolder.textViewEmpName.setText("No User yet");
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewEmpName, textViewRole;
        private final Switch switchAvailability;
        private final CardView cardViewStaff;

        private static final long CLICK_TIME_INTERVAL = 300;
        private long mLastClickTime = System.currentTimeMillis();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEmpName = itemView.findViewById(R.id.textViewEmpName);
            textViewRole = itemView.findViewById(R.id.textViewStatus);
            switchAvailability = itemView.findViewById(R.id.switchAvailability);
            cardViewStaff = itemView.findViewById(R.id.cardViewStaffItem);
//            textViewStaffID = itemView.findViewById(R.id.textViewEmpName);

            cardViewStaff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //bring into staff detail page
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTime = now;
                    position = getAdapterPosition();
                    final User staff = mUsers.get(position);
                    Intent intent = new Intent(context, StaffDetailActivity.class);
                    intent.putExtra("Staff", staff.getStaffID());
//                    intent.putExtra("Page", page);
                    context.startActivity(intent);
                }
            });
        }
    }
}
