package com.example.mycheckin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.ActivityListUserEmployeeBinding;

public class list_user_employee extends BaseFragment {
    ActivityListUserEmployeeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_list_user_employee, container, false);

        return binding.getRoot();
    }

}