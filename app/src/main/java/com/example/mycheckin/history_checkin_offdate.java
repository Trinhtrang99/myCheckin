package com.example.mycheckin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.ActivityHistoryCheckinOffdateBinding;
import com.example.mycheckin.databinding.FragmentListUserBinding;

public class history_checkin_offdate extends BaseFragment   {
    private ActivityHistoryCheckinOffdateBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_history_checkin_offdate, container, false);

        return binding.getRoot();
    }
}