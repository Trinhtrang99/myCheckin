package com.example.mycheckin.admin;

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
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mycheckin.DelayUtils;
import com.example.mycheckin.R;
import com.example.mycheckin.databinding.ActivityMainAdminBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;

public class MainAdmin extends AppCompatActivity {
    ActivityMainAdminBinding binding;
    private ListUserFragment fragmentListUser;
    int PERMISSION_ID = 44;

    //private ListUserFragment li;
    private ManagerCheckin fragmentManagerCheckin;
    private ProfileFragment profileFragment;
    public BottomNavigationView navigation;
    private int currentPositon = 1;
    private int page = 1;

    private FirebaseFirestore db;
    private String TAG = "  DATA FIREBASE";
    FirebaseDatabase database;
    DatabaseReference myRef;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initFragment() {
        fragmentListUser = new ListUserFragment();
        fragmentManagerCheckin = new ManagerCheckin();
        profileFragment = new ProfileFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_admin);
        initFragment();
        replaceFragment(fragmentListUser, fragmentListUser.getTag());
        binding.bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.navigation_ai_scope:
                    currentPositon = 1;
                    replaceFragment(fragmentListUser, fragmentListUser.getTag());
                    page = currentPositon;
                    break;
                case R.id.navigation_inspection_results_top:
                    currentPositon = 2;
                    replaceFragment(fragmentManagerCheckin, fragmentManagerCheckin.getTag());
                    page = currentPositon;
                    break;
                case R.id.navigation_diary:
                    currentPositon = 3;
                    replaceFragment(profileFragment, profileFragment.getTag());
                    page = currentPositon;
                    break;
            }
            return true;
        });

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("commom");


        db = FirebaseFirestore.getInstance();

        getLastLocation();
    }


    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (currentPositon < page) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_from_right);
        }
        if (currentPositon > page) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_out_from_left);
        }
        DelayUtils.getInstance().delay(300, () -> {
            fragmentTransaction.replace(R.id.frame_container, fragment, tag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

    }

    FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    System.out.println(location.getLatitude() + "--" + location.getLongitude());
                    String locations = getCountry(location);

                    myRef.child("/address").setValue(locations);
                });
            } else {
                // Toast.makeText(requireContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private String getCountry(Location location) {
        String country_name = null;
        Geocoder geocoder = new Geocoder(this);
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

}