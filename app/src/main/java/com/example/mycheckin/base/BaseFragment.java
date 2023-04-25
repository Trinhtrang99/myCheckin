package com.example.mycheckin.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mycheckin.DelayUtils;
import com.example.mycheckin.R;

public class BaseFragment extends Fragment {
    private final FragmentBaseDialog customProgressDialogFragment = new FragmentBaseDialog();

    public void showProgressDialog(boolean show) {
        try {
            if (show) {
                if (!customProgressDialogFragment.isShowing()) {
                    customProgressDialogFragment.setShowing(true);
                    if (!customProgressDialogFragment.isAdded()) {
                        customProgressDialogFragment.show(getChildFragmentManager(), customProgressDialogFragment.getTag());
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
    public void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_out_from_left);
        DelayUtils.getInstance().delay(300, () -> {
            fragmentTransaction.replace(R.id.frame_container, fragment, tag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

    }
}
