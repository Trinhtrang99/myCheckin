package com.example.mycheckin.admin;

import static com.example.mycheckin.model.Common.USER;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.mycheckin.R;
import com.example.mycheckin.ReportActivity2;
import com.example.mycheckin.chart.PieHelper;
import com.example.mycheckin.chart.PieView;
import com.example.mycheckin.databinding.FragmentManagerCheckinBinding;
import com.example.mycheckin.model.Checkin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ManagerCheckin extends Fragment {


    FragmentManagerCheckinBinding binding;
    String date;
    int monthSelect ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manager_checkin, container, false);
        // set(binding.pieView);
        monthSelect = 6;
        binding.btnReport.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), ReportActivity2.class);
            i.putExtra("MONTH", monthSelect);
            startActivity(i);
        });
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);
        binding.calendar.setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24));
        binding.calendar.stopNestedScroll();
        Calendar c = Calendar.getInstance();
        int day1 = c.get(Calendar.DAY_OF_MONTH);
        int month1 = c.get(Calendar.MONTH);
        int year1 = c.get(Calendar.YEAR);

        String dateTemp = "";
        if (day1 > 10) {
            dateTemp = day1 + "";
        } else {
            dateTemp = "0" + day1;
        }
        if (month1 + 1 > 10) {
            date = dateTemp + "-" + (month1 + 1) + "-" + year1;
        } else {
            date = dateTemp + "-" + "0" + (month1 + 1) + "-" + year1;
        }

        getData(date);

        binding.calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String date1;
            String monthTemp = "";
            String dateTemp1 = "";
            if (month + 1 > 10) {
                monthTemp = month + 1 + "";
            } else {
                monthTemp = "0" + (month + 1);
            }
            if (dayOfMonth > 10) {
                dateTemp1 = dayOfMonth + "";
            } else {
                dateTemp1 = "0" + dayOfMonth;
            }
            monthSelect = month + 1;
            date = dateTemp1 + "-" + monthTemp + "-" + year;


            getData(date);
        });
     //   binding.calendar.setOnMonthChangedListener
        //binding.pieView.selectedPie(3);

        binding.pieView.setOnPieClickListener(index -> {
            if (index != PieView.NO_SELECTED_INDEX) {
                Intent intent = new Intent(getActivity(), ListnVActivity.class);
                intent.putExtra("key", index);
                intent.putExtra("date", date);
                startActivity(intent);
            }

        });

        return binding.getRoot();
    }

    FirebaseDatabase database;
    DatabaseReference myRef;
    List<Checkin> list;
    int status = 0;
    int wrongAddress = 0;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        status = 0;
        wrongAddress = 0;
        list = new ArrayList<>();


    }


    private void getData(String data) {
        status = 0;
        wrongAddress = 0;
        List<Checkin> listCheckin = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list1 = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list1.add(dataSnapshot.getKey());
                }

                for (String i : list1) {
                    Checkin checkin = snapshot.child(i).child("checkIn").child(data).getValue(Checkin.class);
                    if (checkin != null) {
                        if (checkin.getStatus() == 1) {
                            status++;
                        }
                        if (!checkin.isWrongAddress()) {
                            wrongAddress++;
                        }
                        listCheckin.add(checkin);

                    }
                }
                float persen = (float) (status) / list1.size();
                float percentWrong = (float) (wrongAddress) / list1.size();
                float p0 = persen * 100;
                float p1 = percentWrong * 100;
                float p3 = 100 - (p0 + p1);
                ArrayList<PieHelper> pieHelperArrayList = new ArrayList<>();
                pieHelperArrayList.add(new PieHelper(p0, Color.RED));
                pieHelperArrayList.add(new PieHelper(p1, Color.GRAY));
                pieHelperArrayList.add(new PieHelper(p3));
                binding.pieView.setDate(pieHelperArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getDetails());

            }
        });

    }

}