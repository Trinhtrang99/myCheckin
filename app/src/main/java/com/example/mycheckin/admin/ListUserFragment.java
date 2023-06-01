package com.example.mycheckin.admin;

import static com.example.mycheckin.model.Common.EMAIL;
import static com.example.mycheckin.model.Common.USER;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.mycheckin.AddUserFragment;
import com.example.mycheckin.ButtomSheet;
import com.example.mycheckin.R;
import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.FragmentListUserBinding;
import com.example.mycheckin.model.User;
import com.example.mycheckin.model.UsersModel;
import com.example.mycheckin.utils.SharedUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ListUserFragment extends BaseFragment implements ButtomSheet.IClickEmployee, ListUserAdapter.iClick {


    public ListUserFragment() {
        // Required empty public constructor
    }

    AlertDialog.Builder builder;

    private FragmentListUserBinding binding;
    private AddUserFragment fragment;
    private ListUserAdapter adapter;
    private List<User> userList;
    private List<User> userList2;
    private User user;
    private int pos;
    private ButtomSheet buttomSheet;
    private FixEmployeeFragment fixEmployeeFragment;
    DatabaseReference myRef;
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_user, container, false);
        binding.btnAdd.setOnClickListener(v -> {
            replaceFragment(fragment, fragment.getTag());
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment = new AddUserFragment();
        userList = new ArrayList<>();
        userList2 = new ArrayList<>();
        fixEmployeeFragment = new FixEmployeeFragment();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);
        if (SharedUtils.getString(requireContext(), EMAIL, "").equals("admin@gmail.com")) {
            binding.btnAdd.setVisibility(View.VISIBLE);
        } else {
            binding.btnAdd.setVisibility(View.GONE);

        }

        adapter = new ListUserAdapter(userList, this, requireContext());
        binding.rc.setAdapter(adapter);
        showProgressDialog(true);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                System.out.println(snapshot.getChildren());
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    userList.add(new User("", key, ""));
                    Log.d("TAG", "Value is : " + key);
                }
                for (User i : userList) {
                    if (snapshot.child(i.getMail()).child("url").getValue() != null) {
                        userList2.add(new User(snapshot.child(i.getMail()).child("name").getValue().toString(), i.getMail(), snapshot.child(i.getMail()).child("url").getValue().toString()));

                    } else {
                        userList2.add(new User(snapshot.child(i.getMail()).child("name").getValue().toString(), i.getMail(), null));

                    }
                }
                adapter.updateList(userList2);
                showProgressDialog(false);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);

            }
        });

        builder = new AlertDialog.Builder(getContext());
        dialogConfirm();
        binding.edtSearchEmp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userInput = binding.edtSearchEmp.getText().toString().toLowerCase();
                List<User> newList = new ArrayList<>();
                for (User customersList : userList2) {
                    if (customersList.getName().toLowerCase().contains(userInput)) {
                        newList.add(customersList);
                    }
                }
                adapter.updateList2(newList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClickFixEmployee() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("myData", userList.get(pos).getMail());
        fixEmployeeFragment.setArguments(bundle);
        replaceFragment(fixEmployeeFragment, "");
        buttomSheet.dismiss();
    }

    @Override
    public void onClickDelete() {
        AlertDialog alert = builder.create();
        alert.setTitle("Xóa nhân viên");
        alert.show();

    }


    private void dialogConfirm() {
        builder.setMessage("Bạn có chắc chắn muốn xóa nhân viên này ?")
                .setCancelable(false)
                .setPositiveButton("Có", (dialog, id) -> {
                    myRef.child(userList.get(pos).getName()).removeValue();
                    userList.remove(pos);
                    adapter.updateList(userList);
                    buttomSheet.dismiss();

                })
                .setNegativeButton("Không", (dialog, id) -> {
                    dialog.cancel();
                    buttomSheet.dismiss();
                });
        //Creating dialog box

    }

    private void getInfoUser(String name) {
        myRef.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersModel usersModel = snapshot.getValue(UsersModel.class);
                System.out.println(snapshot.getChildren());
                showDialogInfo(usersModel);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);

            }
        });

    }


    @Override
    public void clickEmployee(User user, int pos) {
        this.user = user;
        this.pos = pos;
        buttomSheet = new ButtomSheet(getContext(), this);
        buttomSheet.show(getActivity().getSupportFragmentManager(), "");
        buttomSheet.setCancelable(false);

    }

    @Override
    public void clickEmployee2(User user, int pos) {
        getInfoUser(user.getMail());
    }

    TextView tvName, tvPhone, tvMail, tvPosition, tvBirthDay;
    ImageView imvAvart;

    private void showDialogInfo(UsersModel user) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.activity_detail_user);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tvName = dialog.findViewById(R.id.tv_name_detail);
        tvPhone = dialog.findViewById(R.id.tv_phone_detail);
        tvMail = dialog.findViewById(R.id.tv_email_detail);
        tvPosition = dialog.findViewById(R.id.tv_positon_detail);
        tvBirthDay = dialog.findViewById(R.id.tv_dob_detail);
        imvAvart = dialog.findViewById(R.id.avatar_detail);

        tvName.setText(user.getName());
        tvMail.setText(user.getEmaul());
        tvPosition.setText(user.getPosition());
        tvBirthDay.setText(user.getBirthday());
        tvPhone.setText(user.getPhone());

        if (user.getUrl() != null) {
            Glide.with(requireContext())
                    .load(user.getUrl())
                    .centerCrop()
                    .into(imvAvart);
        }
        tvPhone.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.CALL_PHONE}, 11);
            }

            if (tvPhone.getText().length() > 0) {
                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                phone_intent.setData(Uri.parse("tel:" + user.getPhone()));
                startActivity(phone_intent);
            }

        });

        dialog.setCancelable(true);
        dialog.show();
    }

}