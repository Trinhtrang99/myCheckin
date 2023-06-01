package com.example.mycheckin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.Toast;

import com.example.mycheckin.databinding.ActivityChangeLocationWifiBinding;
import com.example.mycheckin.databinding.ActivityChangePasswordBinding;
import com.example.mycheckin.utils.WifiUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends AppCompatActivity {
    ActivityChangePasswordBinding binding;
    private FirebaseFirestore db;
    private String TAG = "  DATA FIREBASE";
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("commom");


        db = FirebaseFirestore.getInstance();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.btn.setOnClickListener(v -> {

            myRef.child("/mật khẩu hiện tại").setValue(binding.passCurr.getText().toString());
            myRef.child("/mật khẩu mới").setValue(binding.edtPasswordNew.getText().toString());
            myRef.child("/xác nhận mật khẩu").setValue(binding.edtPasswordConf.getText().toString());

            Toast.makeText(ChangePasswordActivity.this, "Cập nhật mật khẩu thành công ", Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.getRoot();
    }
}