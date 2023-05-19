package com.example.mycheckin.user;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mycheckin.DelayUtils;
import com.example.mycheckin.HomeEmployeeFragment;
import com.example.mycheckin.R;
import com.example.mycheckin.admin.ListUserFragment;
import com.example.mycheckin.admin.ProfileFragment;
import com.example.mycheckin.base.BaseActivity;
import com.example.mycheckin.databinding.ActivityUserBinding;
import com.example.mycheckin.history_checkin;

public class UserActivity extends BaseActivity {
    ActivityUserBinding binding;
    private HomeEmployeeFragment homeEmployeeFragment;
    private int currentPositon = 1;
    private int page = 1;

    //private ListUserFragment li;
    private history_checkin fragmentManagerCheckin;
    private ListUserFragment fragmentListUser;
    private ProfileFragment profileFragment;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initFragment() {
        homeEmployeeFragment = new HomeEmployeeFragment();
        fragmentManagerCheckin = new history_checkin();
        fragmentListUser = new ListUserFragment();
        profileFragment = new ProfileFragment();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user);
        initFragment();

        replaceFragment(homeEmployeeFragment, homeEmployeeFragment.getTag());
        binding.bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    currentPositon = 1;
                    replaceFragment(homeEmployeeFragment, homeEmployeeFragment.getTag());
                    page = currentPositon;
                    break;
                case R.id.navigation_group:
                    currentPositon = 2;
                    replaceFragment(fragmentListUser, fragmentListUser.getTag());
                    page = currentPositon;
                    break;
                case R.id.navigation_history:
                    currentPositon = 3;
                    replaceFragment(fragmentManagerCheckin, fragmentManagerCheckin.getTag());
                    page = currentPositon;
                    break;
                case R.id.navigation_person:
                    currentPositon = 4;
                    replaceFragment(profileFragment, profileFragment.getTag());
                    page = currentPositon;
                    break;
            }
            return true;
        });
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