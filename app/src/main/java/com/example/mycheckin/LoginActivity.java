package com.example.mycheckin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.admin.MainAdmin;
import com.example.mycheckin.base.BaseActivity;
import com.example.mycheckin.databinding.ActivityLoginBinding;
import com.example.mycheckin.utils.SharedUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        binding.btnLogin.setOnClickListener(v -> {
            showProgressDialog(true);
            String email = "tai2@gmail.com";
         //   String email = binding.txtPhone.getText().toString();
            final String password = "123456789";

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        SharedUtils.saveString(getBaseContext(), "EMAIL", email);
                        startActivity(new Intent(LoginActivity.this, MainAdmin.class));
                        finish();
                    }
                    showProgressDialog(false);
                }
            });

        });

    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);

    }
}