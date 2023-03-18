package com.example.mycheckin;

import android.os.Handler;

public class DelayUtils {
    private static DelayUtils instance;

    public static DelayUtils getInstance() {
        if (instance == null) {
            instance = new DelayUtils();
        }
        return instance;
    }

    public void delay(int miliSecond, CallBackDelay callBackDelay) {
        new Handler().postDelayed(callBackDelay::callback, miliSecond);
    }

    public interface CallBackDelay {
        void callback();
    }
}
