package com.example.mycheckin.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.mycheckin.ChangePasswordActivity;
import com.example.mycheckin.R;
import com.example.mycheckin.databinding.FragmentProfileBinding;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        binding.rootChangePass.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        binding.rootLogout.setOnClickListener(v -> {
            requireActivity().finish();
        });
        return binding.getRoot();
    }
}