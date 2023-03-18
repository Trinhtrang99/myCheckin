package com.example.mycheckin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.FragmentAddUserBinding;


public class AddUserFragment extends BaseFragment {

    FragmentAddUserBinding binding;

    public AddUserFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_user, container, false);
        return binding.getRoot();
    }
}