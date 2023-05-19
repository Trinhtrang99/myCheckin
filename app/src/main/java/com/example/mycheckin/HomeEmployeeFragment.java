package com.example.mycheckin;

import static com.example.mycheckin.model.Common.CHECK_IN;
import static com.example.mycheckin.model.Common.EMAIL;
import static com.example.mycheckin.model.Common.EVALUATE;
import static com.example.mycheckin.model.Common.IS_CHECKOUT;
import static com.example.mycheckin.model.Common.IS_CHECK_IN;
import static com.example.mycheckin.model.Common.USER;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.FragmentHomeEmployeeBinding;
import com.example.mycheckin.model.Checkin;
import com.example.mycheckin.model.Common;
import com.example.mycheckin.user.Commom;
import com.example.mycheckin.utils.SharedUtils;
import com.example.mycheckin.utils.WifiUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class HomeEmployeeFragment extends BaseFragment {

    FragmentHomeEmployeeBinding binding;
    int PERMISSION_ID = 44;
    String ipWifi = "";
    String ipWifiHost = "172.168.10.216";
    String localHost = "Trung Văn";
    private FirebaseFirestore db;
    private String TAG = "  DATA FIREBASE";
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRef2;
    String email = "";
    Date time;
    String day;
    int late = 0;
    int onTime = 0;
    Location locationHost;
    Boolean wrong_address = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_employee, container, false);
        return binding.getRoot();
    }

    FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = SharedUtils.getString(requireContext(), EMAIL, "");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);
        db = FirebaseFirestore.getInstance();
        time = new java.util.Date(System.currentTimeMillis());
        day = new SimpleDateFormat("dd-MM-yyyy").format(time);
        ipWifi = WifiUtils.getWifiIpAddress();
        myRef2 = database.getReference("commom");

        locationHost = new Location("");
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Commom common = snapshot.getValue(Commom.class);
                locationHost.setLatitude(Double.parseDouble(common.getLat()));
                locationHost.setLongitude(Double.parseDouble(common.getLog()));
                localHost = common.getAddress();
                ipWifi = common.getWifi_ip();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        myRef.child(email.replace(".", "")).child(CHECK_IN).child(day).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Checkin usersModel = dataSnapshot.getValue(Checkin.class);
                if (usersModel != null) {
                    if (Objects.equals(usersModel.getDate(), day)) {

                        if (!SharedUtils.getBoolean(requireContext(), IS_CHECK_IN, false)) {
                            binding.tvCheckin.setVisibility(View.GONE);
                            binding.timeCheckin.setVisibility(View.VISIBLE);
                        } else {
                            binding.btnCheckin.setEnabled(false);
                            binding.timeCheckin.setVisibility(View.GONE);
                            binding.tvCheckin.setVisibility(View.VISIBLE);
                            binding.tvCheckin.setText(usersModel.getTimeCheckIn());
                        }
                        if (!SharedUtils.getBoolean(requireContext(), IS_CHECKOUT, false)) {
                            binding.tvCheckout.setVisibility(View.GONE);
                            binding.tvTimeCheckout.setVisibility(View.VISIBLE);
                        } else {
                            binding.btnCheckout.setEnabled(false);
                            binding.tvTimeCheckout.setVisibility(View.GONE);
                            binding.tvCheckout.setVisibility(View.VISIBLE);
                            binding.tvCheckout.setText(usersModel.getTimeCheckout());
                        }


                    } else {
                        binding.btnCheckin.setEnabled(true);
                        binding.btnCheckout.setEnabled(false);
                        binding.tvCheckout.setVisibility(View.GONE);
                        binding.tvTimeCheckout.setVisibility(View.VISIBLE);
                        binding.tvCheckin.setVisibility(View.GONE);
                        binding.timeCheckin.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        binding.btnCheckin.setOnClickListener(view1 -> {

            showDialog(true);

            SharedUtils.saveBoolean(requireContext(), IS_CHECK_IN, true);
            SharedUtils.saveBoolean(requireContext(), IS_CHECKOUT, false);
        });
        binding.btnCheckout.setOnClickListener(view12 -> {
            showDialog(false);

            SharedUtils.saveBoolean(requireContext(), IS_CHECKOUT, true);
            addDataToFirebase(false);
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());


    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    System.out.println(location.getLatitude() + "--" + location.getLongitude());
                    String locations = getCountry(location);

                    if (Objects.equals(ipWifiHost, ipWifi) && locations.contains(localHost)) {
                        wrong_address = true;
                    } else {
                        wrong_address = false;
                    }

                });
            } else {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }




    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private String getCountry(Location location) {
        String country_name = null;
        Geocoder geocoder = new Geocoder(getActivity());
        if (location != null) {
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    country_name = addresses.get(0).getCountryName();
                    return addresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
     //   Toast.makeText(getActivity(), country_name, Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLastLocation();

    }

    int status = 0;
    int type = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addDataToFirebase(Boolean isCheckIn) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String time1 = "08:30";
        String time3 = "17:30";
        String time2 = dtfTime.format(now);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date date1 = format.parse(time1);
            Date date2 = format.parse(time2);
            Date date3 = format.parse(time3);
            long difference = date2.getTime() - date1.getTime();
            long difference2 = date3.getTime() - date2.getTime();
            if (difference > 0 || difference2 > 0) {
                // đi làm muộn || đi về sớm
                status = 1;
                type = 1;// đi làm muộn hoặc về sớm


            } else {

                status = 0;
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Checkin checkin = new Checkin();
        checkin.setDate(dtf.format(now));
        checkin.setEmaill(SharedUtils.getString(requireContext(), EMAIL, ""));
        checkin.setTimeCheckIn(time2);
        checkin.setType(0);
        checkin.setStatus(status);
        checkin.setWrongAddress(wrong_address);


        if (isCheckIn) {
            binding.tvCheckin.setVisibility(View.VISIBLE);
            binding.timeCheckin.setVisibility(View.GONE);
            binding.tvCheckin.setText(time2);

            myRef.child(email.replace(".", ""))
                    .child(CHECK_IN).child(day).setValue(checkin)
                    .addOnFailureListener(e -> System.out.println("FAIL"))
                    .addOnCompleteListener(task -> System.out.println("dONE"));
        } else {
            binding.tvCheckout.setVisibility(View.VISIBLE);
            binding.tvTimeCheckout.setVisibility(View.GONE);
            binding.tvCheckout.setText(time2);
            checkin.setTimeCheckout(time2);

            try {
                Date date2 = format.parse(binding.tvCheckin.getText().toString());
                Date date3 = format.parse(binding.tvCheckout.getText().toString());
                long difference = date3.getTime() - date2.getTime();
                if (difference < 8) {
                    type = 2;// k đủ 8 tiếng
                } else {
                    if (status == 0) {
                        type = 0;
                    }
                }
                String link = "/" + email.replace(".", "") + "/checkIn/" + day;
                myRef.child(link + "/timeCheckout").setValue(time2);
                myRef.child(link + "/type").setValue(type);
                myRef.child(link + "/status").setValue(status);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            if (status == 0 && type == 0) {
                db.collection(EVALUATE).document(email).update("onTime", onTime + 1);
            } else {
                db.collection(EVALUATE).document(email).update("late", late + 1);
            }
        }

        showProgressDialog(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDialog(Boolean isCheckin) {
        Dialog dialog = new Dialog(requireContext());

        dialog.setContentView(R.layout.activity_confirm_checkin);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        String txt = "";
        if (isCheckin) {
            txt = "Bạn có chắc chắc muốn check in?";
        } else {
            txt = "Bạn có chắc chắc muốn check out?";
        }
        TextView tv = dialog.findViewById(R.id.tv_confirm_checkin);
        tv.setText(txt);
        RelativeLayout btnOK, btnCancel;
        btnOK =(RelativeLayout) dialog.findViewById(R.id.panel_checkin);
        btnCancel =(RelativeLayout) dialog.findViewById(R.id.btnCancel);

        btnOK.setOnClickListener(v -> {
            showProgressDialog(true);
            if (isCheckin){
                addDataToFirebase(true);
            }else {
                addDataToFirebase(false);
            }
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(true);
        dialog.show();
    }
}

