package com.example.mycheckin.admin;

import static com.example.mycheckin.model.Common.USER;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.example.mycheckin.R;
import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.FragmentFixEmployeeBinding;
import com.example.mycheckin.model.UsersModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class FixEmployeeFragment extends BaseFragment {
    private FragmentFixEmployeeBinding binding;
    DatabaseReference myRef;
    FirebaseDatabase database;
    String[] courses = {"Kế toán ", "Nhân sự",
            "Nhân viên"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fix_employee, container, false);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        binding.edtNgaysinh.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (datePicker, year1, month1, day) -> {
                        binding.edtNgaysinh.setText(day + "-" + (month1 + 1) + "-" + year1);
                    }, year, month, dayOfMonth);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        ArrayAdapter ad
                = new ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                courses);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        binding.spinner1.setAdapter(ad);
        binding.btnUpdate.setOnClickListener(v -> {
            showProgressDialog(true);
            updateData();
            showProgressDialog(false);
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
        });
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();

        Bundle bundle = getArguments();
        String myObject = bundle.getString("myData");
        showProgressDialog(true);
        myRef = database.getReference(USER).child(myObject);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersModel usersModel = snapshot.getValue(UsersModel.class);
                binding.txtName.setText(usersModel.getName());
                binding.txtEmail.setText(usersModel.getEmaul());
                binding.edtNgaysinh.setText(usersModel.getBirthday());
                binding.edtSdt.setText(usersModel.getPhone());
                String position = usersModel.getPosition();
                for (int i = 0; i < courses.length; i++) {
                    if (position.equals(courses[i])) {
                        binding.spinner1.setSelection(i);
                    }
                }

                showProgressDialog(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);

            }
        });

    }

    private void updateData() {
        myRef.child("/birthday").setValue(binding.edtNgaysinh.getText().toString());
        myRef.child("/phone").setValue(binding.edtSdt.getText().toString());
        myRef.child("/position").setValue(binding.spinner1.getSelectedItem().toString());
        System.out.println(binding.spinner1.getSelectedItem().toString());
    }
}
