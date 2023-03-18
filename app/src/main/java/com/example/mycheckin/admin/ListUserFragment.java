package com.example.mycheckin.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.AddUserFragment;
import com.example.mycheckin.ButtomSheet;
import com.example.mycheckin.R;
import com.example.mycheckin.base.BaseFragment;
import com.example.mycheckin.databinding.FragmentListUserBinding;
import com.example.mycheckin.model.User;

import java.util.ArrayList;
import java.util.List;


public class ListUserFragment extends BaseFragment implements ButtomSheet.IClickEmployee {


    public ListUserFragment() {
        // Required empty public constructor
    }

    AlertDialog.Builder builder;

    private FragmentListUserBinding binding;
    private AddUserFragment fragment;
    private ListUserAdapter adapter;
    private List<User> userList;
    private User user;
    private int pos;
    private ButtomSheet buttomSheet;
    private FixEmployeeFragment fixEmployeeFragment;

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
        fixEmployeeFragment = new FixEmployeeFragment();
        userList.add(new User("Bùi Thị Thu Hương"));
        userList.add(new User("Bùi Thị Thu Hương2"));
        userList.add(new User("Bùi Thị Thu Hương3"));
        builder = new AlertDialog.Builder(getContext());
        dialogConfirm();
        adapter = new ListUserAdapter(userList, (user, pos) -> {
            this.user = user;
            this.pos = pos;
            buttomSheet = new ButtomSheet(getContext(), this);
            buttomSheet.show(getActivity().getSupportFragmentManager(), "");
            buttomSheet.setCancelable(false);
        });
        binding.rc.setAdapter(adapter);
    }

    @Override
    public void onClickFixEmployee() {

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
}