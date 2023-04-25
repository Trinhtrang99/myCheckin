package com.example.mycheckin;

import static com.example.mycheckin.model.Common.USER;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.FragmentAddUserBinding;
import com.example.mycheckin.model.Checkin;
import com.example.mycheckin.model.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddUserFragment extends BaseFragment {

    FragmentAddUserBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DatabaseReference myRef;

    FirebaseDatabase database;


    public AddUserFragment() {
        // Required empty public constructor
    }

    String[] courses = {"Kế toán ", "Nhân sự",
            "Nhân viên"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_user, container, false);
        db = FirebaseFirestore.getInstance();

        ArrayAdapter ad
                = new ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                courses);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        binding.spinner1.setAdapter(ad);

        binding.btnadd.setOnClickListener(view -> {
          //  showProgressDialog(true);
            String email = binding.edtHolderName.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            addDataToFirebase();

            auth.createUserWithEmailAndPassword(email, "123456789").addOnCompleteListener(requireActivity(), task -> {
                        if (!task.isSuccessful()) {

                        } else {

                        }
                    })
                    .addOnFailureListener(requireActivity(), e -> {
                        System.out.println("fail" + e.getCause());
                    });
        });

        return binding.getRoot();
    }



    private void addDataToFirebase() {

        UsersModel user = new UsersModel();
        user.setBirthday(binding.edtBirthday.getText().toString());
        user.setEmaul(binding.edtHolderName.getText().toString());
        user.setName(binding.edtName.getText().toString());
        user.setPhone(binding.edtST.getText().toString());
        user.setPosition("kế toán");
        //   user.setCheckin(new Checkin());
        String email = binding.edtHolderName.getText().toString().replace(".", "");
        myRef.child(email).setValue(user).addOnCompleteListener(task -> {
                    System.out.println("Done");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Fail");

                });

        Map<String, Integer> numberMapping = new HashMap<>();
        numberMapping.put("late", 0);
        numberMapping.put("onTime", 0);
        db.collection("evaluate").document(email).set(numberMapping);

    //    showProgressDialog(false);


    }

}