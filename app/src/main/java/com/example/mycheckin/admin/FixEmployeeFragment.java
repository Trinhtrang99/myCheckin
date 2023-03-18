package com.example.mycheckin.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.R;
import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.FragmentFixEmployeeBinding;

public class FixEmployeeFragment extends BaseFragment {
    private FragmentFixEmployeeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fix_employee, container, false);
        return binding.getRoot();
    }
}
