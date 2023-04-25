package com.example.mycheckin;

import static com.example.mycheckin.model.Common.CHECK_IN;
import static com.example.mycheckin.model.Common.EMAIL;
import static com.example.mycheckin.model.Common.EVALUATE;
import static com.example.mycheckin.model.Common.IS_CHECKOUT;
import static com.example.mycheckin.model.Common.IS_CHECK_IN;
import static com.example.mycheckin.model.Common.USER;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.FragmentHomeEmployeeBinding;
import com.example.mycheckin.model.Checkin;
import com.example.mycheckin.utils.SharedUtils;
import com.example.mycheckin.utils.WifiUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
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
    String email = "";
    Date time;
    String day;
    int late = 0;
    int onTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_employee, container, false);
        return binding.getRoot();
    }

    FusedLocationProviderClient mFusedLocationClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        ipWifi = WifiUtils.getWifiIpAddress();
        email = SharedUtils.getString(requireContext(), EMAIL, "");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);

        time = new java.util.Date(System.currentTimeMillis());
        day = new SimpleDateFormat("dd-MM-yyyy").format(time);



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
                            binding.timeCheckin.setVisibility(View.GONE);
                            binding.tvCheckin.setVisibility(View.VISIBLE);
                            binding.tvCheckin.setText(usersModel.getTimeCheckout());
                        }
                        if (!SharedUtils.getBoolean(requireContext(), IS_CHECKOUT, false)) {
                            binding.tvCheckout.setVisibility(View.GONE);
                            binding.tvTimeCheckout.setVisibility(View.VISIBLE);
                        } else {

                            binding.tvTimeCheckout.setVisibility(View.GONE);
                            binding.tvCheckout.setVisibility(View.VISIBLE);
                            binding.tvCheckout.setText(usersModel.getTimeCheckIn());
                        }


                    } else {
                        SharedUtils.saveBoolean(requireContext(), IS_CHECK_IN, false);
                        SharedUtils.saveBoolean(requireContext(), IS_CHECKOUT, false);
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
            showProgressDialog(true);
            addDataToFirebase(true);
            SharedUtils.saveBoolean(requireContext(), IS_CHECK_IN, true);
        });
        binding.btnCheckout.setOnClickListener(view12 -> {
            showProgressDialog(true);
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
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        String locations = getCountry(location);
                        if (Objects.equals(ipWifiHost, ipWifi) && locations.contains(localHost)) {
                            Toast.makeText(requireContext(), "Đúng giờ", Toast.LENGTH_SHORT).show();

                        } else {

                        }

                    }
                });
            } else {
                Toast.makeText(requireContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            //latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            //longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
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
        Toast.makeText(getActivity(), country_name, Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
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
}

