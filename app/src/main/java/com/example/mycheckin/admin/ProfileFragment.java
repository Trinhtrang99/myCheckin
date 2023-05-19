package com.example.mycheckin.admin;

import static com.example.mycheckin.model.Common.EMAIL;
import static com.example.mycheckin.model.Common.USER;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.anilokcun.uwmediapicker.UwMediaPicker;
import com.bumptech.glide.Glide;
import com.example.mycheckin.ChangePasswordActivity;
import com.example.mycheckin.LoginActivity;
import com.example.mycheckin.R;
import com.example.mycheckin.change_location_wifi;
import com.example.mycheckin.databinding.FragmentProfileBinding;
import com.example.mycheckin.model.UsersModel;
import com.example.mycheckin.update_user;
import com.example.mycheckin.utils.SharedUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;

    DatabaseReference myRef;
    FirebaseDatabase database;

    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    private ActivityResultLauncher<String[]> activityResultLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        binding.rootChangePass.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        String email = SharedUtils.getString(requireContext(), EMAIL, "");

        if (email.equals("admin@gmail.com")) {
            binding.rootChangeIP.setVisibility(View.VISIBLE);
            binding.rootInfo.setVisibility(View.GONE);
            binding.rootChangePass.setVisibility(View.GONE);
        } else {
            binding.rootChangeIP.setVisibility(View.GONE);
            getInfoUser(email.replace(".", ""));
        }
        binding.rootChangeIP.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), change_location_wifi.class);
            startActivity(i);
        });
        binding.rootLogout.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        });
        binding.rootInfo.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), update_user.class);
            startActivity(i);
        });




        return binding.getRoot();
    }


    private void getInfoUser(String name) {
        myRef.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersModel usersModel = snapshot.getValue(UsersModel.class);
                binding.txtAdmin.setText(usersModel.getName());
                binding.txtEmail.setText(usersModel.getEmaul());
                if (usersModel.getUrl()!=null){
                    Glide.with(requireContext())
                            .load(usersModel.getUrl())
                            .centerCrop()
                            .into(binding.avatarAdmin);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }

}