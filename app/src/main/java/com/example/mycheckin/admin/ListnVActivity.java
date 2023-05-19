package com.example.mycheckin.admin;

import static com.example.mycheckin.model.Common.USER;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.R;
import com.example.mycheckin.base.BaseActivity;
import com.example.mycheckin.databinding.ActivityListnVactivityBinding;
import com.example.mycheckin.model.Checkin;
import com.example.mycheckin.model.User;
import com.example.mycheckin.model.UsersModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListnVActivity extends BaseActivity implements ListUserAdapter.iClick {
    FirebaseDatabase database;
    DatabaseReference myRef;
    List<User> list = new ArrayList<>();
    private ListUserAdapter adapter;

    ActivityListnVactivityBinding binding;
    int message;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_listn_vactivity);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);
        Intent intent = getIntent();
        message = intent.getIntExtra("key", 0);
        date = intent.getStringExtra("date");
        adapter = new ListUserAdapter(list, this, this);
        binding.rc.setAdapter(adapter);
        getData(date);
    }

    private void getData(String data) {
        showProgressDialog(true);
        List<User> listCheckin = new ArrayList<>();
        List<User> listNameLate = new ArrayList<>();
        List<User> listNameWrongAddress = new ArrayList<>();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list1 = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list1.add(dataSnapshot.getKey());
                }

                for (String i : list1) {
                    Checkin checkin = snapshot.child(i).child("checkIn").child(data).getValue(Checkin.class);
                    String url ;
                    if (snapshot.child(i).child("url").getValue().toString()!=null){
                        url = snapshot.child(i).child("url").getValue().toString();
                    }else {
                        url = null;
                    }
                    if (checkin != null) {
                        if (checkin.getStatus() == 1) {
                            listNameLate.add(new User(snapshot.child(i).child("name").getValue().toString(),i,url));
                        }
                        if (!checkin.isWrongAddress()) {
                            listNameWrongAddress.add(new User(snapshot.child(i).child("name").getValue().toString(),i,url));
                        }
                        if (checkin.getStatus() != 1 && checkin.isWrongAddress()) {
                            listCheckin.add(new User(snapshot.child(i).child("name").getValue().toString(),i,url));
                        }


                    } else {
                        listCheckin.add(new User(snapshot.child(i).child("name").getValue().toString(),i,url));
                    }
                }
                switch (message) {
                    case 0:
                        adapter.updateList(listNameLate);
                    case 1:
                        adapter.updateList(listNameWrongAddress);
                    case 2:
                        adapter.updateList(listCheckin);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getDetails());

            }
        });
        showProgressDialog(false);

    }

    @Override
    public void clickEmployee(User user, int pos) {
        getInfoUser(user.getMail());
    }

    @Override
    public void clickEmployee2(User user, int pos) {

    }

    TextView tvName, tvPhone, tvMail, tvPosition, tvBirthDay;

    private void showDialogInfo(UsersModel user) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_detail_user);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tvName = dialog.findViewById(R.id.tv_name_detail);
        tvPhone = dialog.findViewById(R.id.tv_phone_detail);
        tvMail = dialog.findViewById(R.id.tv_email_detail);
        tvPosition = dialog.findViewById(R.id.tv_positon_detail);
        tvBirthDay = dialog.findViewById(R.id.tv_dob_detail);
        tvName.setText(user.getName());
        tvMail.setText(user.getEmaul());
        tvPosition.setText(user.getPosition());
        tvBirthDay.setText(user.getBirthday());
        tvPhone.setText(user.getPhone());

        tvPhone.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 11);
            }

            if (tvPhone.getText().length() > 0) {
                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                phone_intent.setData(Uri.parse("tel:" + user.getPhone()));
                startActivity(phone_intent);
            }

        });

        dialog.setCancelable(true);
        dialog.show();
    }
    private void getInfoUser(String name) {
        myRef.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersModel usersModel = snapshot.getValue(UsersModel.class);
                System.out.println(snapshot.getChildren());
                showDialogInfo(usersModel);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);

            }
        });

    }
}