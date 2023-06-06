package com.example.mycheckin;

import static com.example.mycheckin.model.Common.EMAIL;
import static com.example.mycheckin.model.Common.USER;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.base.BaseActivity;
import com.example.mycheckin.chart.LineView;
import com.example.mycheckin.databinding.ActivityReport2Binding;
import com.example.mycheckin.model.Checkin;
import com.example.mycheckin.utils.SharedUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ReportActivity2 extends BaseActivity {
    ActivityReport2Binding binding;
    int monthSelect;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String email;
// đỏ đi muộn , tím sai địa chỉ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report2);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);

        monthSelect = getIntent().getExtras().getInt("MONTH");
        email = SharedUtils.getString(this, EMAIL, "");
        initLineView(binding.lineView);
        getDataMonth();
    }


    private void initLineView(LineView lineView) {
        YearMonth yearMonthObject = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject = YearMonth.of(2023, monthSelect);
            int daysInMonth = yearMonthObject.lengthOfMonth();
            ArrayList<String> test = new ArrayList<String>();
            for (int i = 0; i <= daysInMonth; i++) {
                test.add(String.valueOf(i + 1));
            }
            lineView.setBottomTextList(test);
            lineView.setColorArray(new int[]{
                    Color.parseColor("#F44336"), Color.parseColor("#9C27B0"),
                    Color.parseColor("#2196F3")
            });
            lineView.setDrawDotLine(true);
            lineView.setShowPopup(LineView.SHOW_POPUPS_NONE);
        }

    }

    int status1 = 0;
    int status2 = 0;
    int status3 = 0;
    List<Integer> list1 = new ArrayList<>();
    List<Integer> list2 = new ArrayList<>();
    List<Integer> list3 = new ArrayList<>();

    private void getDataMonth() {

        YearMonth yearMonthObject = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject = YearMonth.of(2023, monthSelect);
            int daysInMonth = yearMonthObject.lengthOfMonth();


            String month;
            if (monthSelect < 10) {
                month = "0" + monthSelect;
            } else {
                month = monthSelect + "";
            }

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String day;
                    for (int i = 1; i <= daysInMonth; i++) {
                        status1 = 0;
                        status3 = 0;
                        status2 = 0;
                        if (i < 10) {
                            day = "0" + i;
                        } else {
                            day = i + "";
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            String time = day + "-" + month + "-2023";
                            Checkin checkin = snapshot.child(dataSnapshot.getKey()).child("checkIn").child(time).getValue(Checkin.class);
                            if (checkin != null) {
                                if (checkin.getStatus() == 1) {
                                    status1++;
                                }
                                if (!checkin.isWrongAddress()) {
                                    status2++;
                                }
                                if (checkin.getStatus() != 1 && checkin.isWrongAddress()) {
                                    status3++;
                                }
                            }


                        }
                        list1.add(status1);
                        list2.add(status2);
                        list3.add(status3);
                        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
                        dataLists.add((ArrayList<Integer>) list1);
                        dataLists.add((ArrayList<Integer>) list2);
                        dataLists.add((ArrayList<Integer>) list3);

                        binding.lineView.setDataList(dataLists);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }


    }
}