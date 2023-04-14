package com.example.mycheckin.admin;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mycheckin.DelayUtils;
import com.example.mycheckin.HomeEmployeeFragment;
import com.example.mycheckin.R;
import com.example.mycheckin.databinding.ActivityMainAdminBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainAdmin extends AppCompatActivity {
    ActivityMainAdminBinding binding;
    private HomeEmployeeFragment fragmentListUser;
    //private ListUserFragment li;
    private ManagerCheckin fragmentManagerCheckin;
    private ProfileFragment profileFragment;
    public BottomNavigationView navigation;
    private int currentPositon = 1;
    private int page = 1;

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
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initFragment() {
        fragmentListUser = new HomeEmployeeFragment();
        fragmentManagerCheckin = new ManagerCheckin();
        profileFragment = new ProfileFragment();
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

}