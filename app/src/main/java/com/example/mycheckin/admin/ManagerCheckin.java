package com.example.mycheckin.admin;

import static com.example.mycheckin.model.Common.USER;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.mycheckin.R;
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
import java.util.Objects;


public class ManagerCheckin extends Fragment {


    FragmentManagerCheckinBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manager_checkin, container, false);
        // set(binding.pieView);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);
        binding.calendar.setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24));
        binding.calendar.stopNestedScroll();
        Calendar c = Calendar.getInstance();
        int day1 = c.get(Calendar.DAY_OF_MONTH);
        int month1 = c.get(Calendar.MONTH);
        int year1 = c.get(Calendar.YEAR);
        String date = day1 + "-" + (month1 + 1) + "-" + year1;
        getData(date);
        binding.calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String date1 = dayOfMonth + "-" + (month + 1) + "-" + year;
            getData(date1);
        });
        return binding.getRoot();
    }

    FirebaseDatabase database;
    DatabaseReference myRef;
    List<Checkin> list;
    int status = 0;
    ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();
        //   DatabaseReference reference = FirebaseDatabase.getInstance().getReference();



    }

    private void randomSet(PieView pieView) {
        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
        ArrayList<Integer> intList = new ArrayList<Integer>();
        int totalNum = (int) (5 * Math.random()) + 5;

        int totalInt = 0;
        for (int i = 0; i < totalNum; i++) {
            int ranInt = (int) (Math.random() * 10) + 1;
            intList.add(ranInt);
            totalInt += ranInt;
        }
        for (int i = 0; i < totalNum; i++) {
            pieHelperArrayList.add(new PieHelper(100f * intList.get(i) / totalInt));
        }

        pieView.selectedPie(PieView.NO_SELECTED_INDEX);
        pieView.showPercentLabel(true);
        pieView.setDate(pieHelperArrayList);
    }

    private void set(PieView pieView) {

        pieHelperArrayList.add(new PieHelper(20, Color.BLACK));
        pieHelperArrayList.add(new PieHelper(6));
        pieHelperArrayList.add(new PieHelper(30));
        pieHelperArrayList.add(new PieHelper(12));
        pieHelperArrayList.add(new PieHelper(32));


    }

    private void getData(String data) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    myRef.child(key).child("checkIn").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {

                            for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                                Checkin usersModel = dataSnapshot1.getValue(Checkin.class);
                                list.add(new Checkin().filterDate(usersModel, data));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    list.removeIf(Objects::isNull);
                                }
                                if (usersModel.getStatus() == 1) {
                                    status++;
                                }
                            }
                            System.out.println(list.toString());
                            //
                            float persen = (status) / 10;
                            pieHelperArrayList.add(new PieHelper(20));
                            pieHelperArrayList.add(new PieHelper(100 - 20));
                            binding.pieView.setDate(pieHelperArrayList);
                            binding.pieView.setOnPieClickListener(index -> {
                                if (index != PieView.NO_SELECTED_INDEX) {
                                    //    textView.setText(index + " selected");
                                } else {
                                    //      textView.setText("No selected pie");
                                }
                            });
                            binding.pieView.selectedPie(2);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("s" + error.getDetails());
                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getDetails());

            }
        });
    }
}