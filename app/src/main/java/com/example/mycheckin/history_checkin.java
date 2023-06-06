package com.example.mycheckin;

import static com.example.mycheckin.model.Common.CHECK_IN;
import static com.example.mycheckin.model.Common.EMAIL;
import static com.example.mycheckin.model.Common.USER;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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
import java.time.YearMonth;
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
    int times = 0;
    int monthSelect = 6;

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
        times = 0;
        getData(day);
        getDataMonth();
        scrollToBottom();
        Calendar calendar = Calendar.getInstance();
        binding.calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            monthSelect = month + 1;
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

            getDataMonth();
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

    String mess1 = "";
    String mess2 = "";

    private void getDataMonth() {
        mess1 = "Số lần đi muộn trong tháng " + monthSelect + " : ";
        YearMonth yearMonthObject = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject = YearMonth.of(2023, monthSelect);
            int daysInMonth = yearMonthObject.lengthOfMonth();

            String day;
            String month;
            if (monthSelect < 10) {
                month = "0" + monthSelect;
            } else {
                month = monthSelect + "";
            }
            for (int i = 1; i <= daysInMonth; i++) {
                if (i < 10) {
                    day = "0" + i;
                } else {
                    day = i + "";
                }
                String time = day + "-" + month + "-2023";
                myRef.child(email.replace(".", "")).child(CHECK_IN).child(time).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Checkin usersModel = snapshot.getValue(Checkin.class);
                        if (usersModel != null) {
                            if (usersModel.getStatus() == 1) {
                                times++;
                                mess2 += "\n" + time + " checkIn : " + usersModel.getTimeCheckIn() + " ,Checkout " + usersModel.getTimeCheckout() + "\n";
                                binding.tvLateNumber.setText(mess1 + times + mess2);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }


        Log.d("SO lam", "" + times);
    }

    private void scrollToBottom() {
        binding.SCROLLERID.post(new Runnable() {
            public void run() {
                binding.SCROLLERID.smoothScrollTo(0, binding.tvLateNumber.getBottom());
            }
        });
    }
}