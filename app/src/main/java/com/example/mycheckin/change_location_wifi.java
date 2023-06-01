package com.example.mycheckin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.example.mycheckin.databinding.ActivityChangeLocationWifiBinding;
import com.example.mycheckin.user.Commom;
import com.example.mycheckin.utils.WifiUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class change_location_wifi extends AppCompatActivity {
    ActivityChangeLocationWifiBinding binding;
    private FirebaseFirestore db;
    private String TAG = "  DATA FIREBASE";
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_location_wifi);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("commom");


        db = FirebaseFirestore.getInstance();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Commom common = snapshot.getValue(Commom.class);
                String lat =common.getLat();
                String log = common.getLog();
                String ip = common.getWifi_ip();
                binding.edtlat.setText(lat);
                binding.edtLog.setText(log);
                binding.edtWifi.setText(ip);
                binding.tvdetailLocation.setText(common.getAddress());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        binding.btn.setOnClickListener(v -> {

            myRef.child("/lat").setValue(binding.edtlat.getText().toString());
            myRef.child("/log").setValue(binding.edtLog.getText().toString());
            myRef.child("/address").setValue(binding.tvdetailLocation.getText().toString());
            myRef.child("/wifi_ip").setValue(WifiUtils.getWifiIpAddress());
            Toast.makeText(change_location_wifi.this, "Cập nhật thông tin thành công ", Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.getRoot();
    }


}