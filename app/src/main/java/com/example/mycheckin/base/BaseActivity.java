package com.example.mycheckin.base;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity  extends AppCompatActivity {
    private final FragmentBaseDialog customProgressDialogFragment = new FragmentBaseDialog();

    public void showProgressDialog(boolean show) {
        try {
            if (show) {
                if (!customProgressDialogFragment.isShowing()) {
                    customProgressDialogFragment.setShowing(true);
                    if (!customProgressDialogFragment.isAdded()) {
                        customProgressDialogFragment.show(getSupportFragmentManager(), customProgressDialogFragment.getTag());
                    }
                }
            } else {
                if (customProgressDialogFragment.isShowing()) {
                    customProgressDialogFragment.dismiss();
                    customProgressDialogFragment.setShowing(false);
                }
            }
        } catch (Exception ex) {

        }
    }
}
