package com.example.mycheckin;

import static com.example.mycheckin.model.Common.CHECK_IN;
import static com.example.mycheckin.model.Common.EMAIL;
import static com.example.mycheckin.model.Common.USER;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.ActivityHistoryCheckinBinding;
import com.example.mycheckin.model.Checkin;
import com.example.mycheckin.utils.SharedUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class history_checkin extends BaseFragment {
    private ActivityHistoryCheckinBinding binding;
    private FirebaseFirestore db;
    private String TAG = "  DATA FIREBASE";
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRef2;
    Date time;
    String day;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_history_checkin, container, false);
        email = SharedUtils.getString(requireContext(), EMAIL, "");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);
        db = FirebaseFirestore.getInstance();
        time = new java.util.Date(System.currentTimeMillis());
        day = new SimpleDateFormat("dd-MM-yyyy").format(time);
        myRef = database.getReference(USER);
        binding.calendar.setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24));
        binding.calendar.stopNestedScroll();
        Calendar c = Calendar.getInstance();
        int day1 = c.get(Calendar.DAY_OF_MONTH);
        int month1 = c.get(Calendar.MONTH);
        int year1 = c.get(Calendar.YEAR);
        getData(day);


        Calendar calendar = Calendar.getInstance();
        binding.calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            LocalDate ld2 = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                ld2 = LocalDate.of(year, month + 1, dayOfMonth);
            }
            boolean isWeekend2 = isWeekEnd(ld2);

            calendar.set(year, month + 1, dayOfMonth); // Set ngày tháng năm cần kiểm tra

            if (isWeekend2) {
                showDialog();
            } else {
                String date1;
                if (month + 1 > 10) {
                    date1 = dayOfMonth + "-" + (month + 1) + "-" + year;
                } else {
                    date1 = dayOfMonth + "-" + "0" + (month + 1) + "-" + year;
                }
                getData(date1);
            }


        });
        return binding.getRoot();
    }

    public static boolean isWeekEnd(LocalDate localDate) {
        String dayOfWeek = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dayOfWeek = localDate.getDayOfWeek().toString();
        }
        if ("SATURDAY".equalsIgnoreCase(dayOfWeek) ||
                "SUNDAY".equalsIgnoreCase(dayOfWeek)) {
            return true;
        }
        return false;
    }

    private void getData(String data) {
        showProgressDialog(true);
        myRef.child(email.replace(".", "")).child(CHECK_IN).child(data)
                .addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                                               Checkin usersModel = snapshot.getValue(Checkin.class);
                                               if (snapshot.getValue() == null) {
                                                   binding.txtCheckin.setText("--:--");
                                                   binding.txtCheckout.setText("--:--");
                                               } else {
                                                   if (usersModel != null) {
                                                       binding.txtCheckin.setText(usersModel.getTimeCheckIn());

                                                       binding.txtCheckout.setText(usersModel.getTimeCheckout());
                                                       if (usersModel.getTimeCheckout() == null) {
                                                           binding.txtCheckout.setText("--:--");
                                                       }
                                                       if (usersModel.getTimeCheckIn() == null) {
                                                           binding.txtCheckin.setText("--:--");
                                                       }
                                                   }
                                               }


                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError error) {

                                           }

                                       }
                );
        showProgressDialog(false);

    }

    private void showDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.activity_history_checkin_offdate);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.show();
    }

}