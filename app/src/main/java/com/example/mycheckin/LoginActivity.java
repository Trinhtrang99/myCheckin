package com.example.mycheckin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.admin.MainAdmin;
import com.example.mycheckin.databinding.ActivityLoginBinding;
import com.example.mycheckin.utils.SharedUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        binding.btnLogin.setOnClickListener(v -> {
            String email = "annatrangtrang99@gmail.com";
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
                }
            });

        });

    }
}