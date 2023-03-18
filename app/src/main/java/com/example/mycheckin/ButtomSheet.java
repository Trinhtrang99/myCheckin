package com.example.mycheckin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.databinding.BtnSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ButtomSheet extends BottomSheetDialogFragment {

    private BtnSheetBinding binding;
    private Context context;
    private IClickEmployee iClickEmployee;


    public ButtomSheet(Context context, IClickEmployee iClickEmployee) {
        this.context = context;
        this.iClickEmployee = iClickEmployee;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.btn_sheet, container, false);

        binding.rootFix.setOnClickListener(v -> {
            iClickEmployee.onClickFixEmployee();
        });
        binding.rootDelete.setOnClickListener(v -> {
            iClickEmployee.onClickDelete();
        });
        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setPeekHeight(2300);
            bottomSheetBehavior.setDraggable(false);
            //  ((View) binding.getRoot().getParent()).setBackground(getResources().getDrawable(R.drawable.bottom_sheet_rounded, getContext().getTheme()));
        });
    }

    public interface IClickEmployee {
        void onClickFixEmployee();

        void onClickDelete();

    }
}
