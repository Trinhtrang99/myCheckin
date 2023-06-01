package com.example.mycheckin;

import static com.example.mycheckin.model.Common.USER;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.mycheckin.admin.MainAdmin;
import com.example.mycheckin.base.BaseActivity;
import com.example.mycheckin.databinding.ActivityLoginBinding;
import com.example.mycheckin.user.UserActivity;
import com.example.mycheckin.utils.SharedUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;
    private FirebaseAuth auth;
    String androidId;
    private DatabaseReference myRef;

    FirebaseDatabase database;
    static Boolean isLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);

        binding.btnLogin.setOnClickListener(v -> {
            isLogin = true;

            //String email = "tai@gmail.com";
            String email = binding.txtPhone.getText().toString();
            final String password = "123456789";
//            SharedUtils.saveString(getBaseContext(), "EMAIL", "admin@gmail.com");
//            startActivity(new Intent(LoginActivity.this, MainAdmin.class));
//            finish();
            if (binding.txtPhone.getText().toString().equals("admin@gmail.com") && binding.txtPass.getText().toString().equals("123456789")) {
                SharedUtils.saveString(getBaseContext(), "EMAIL", "admin@gmail.com");
                startActivity(new Intent(LoginActivity.this, MainAdmin.class));
                Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if (email.equals("") || password.equals("")) {
                    Toast.makeText(this, "Nhập mật khẩu hoặc Email", Toast.LENGTH_SHORT).show();
                } else {
                    addDataToFirebase2(email, androidId, password);
                }
            }


        });

    }

    String id2;


    private void addDataToFirebase2(String email, String id, String password) {
        showProgressDialog(true);
        myRef.child(email.replace(".", "")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (snapshot.child("idAndroid").getValue() != null) {
                        id2 = snapshot.child("idAndroid").getValue().toString();
                        if (id.equals(id2)) {
                            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                                if (isLogin){
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không chính xác." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        isLogin = false;
                                        finish();
                                        SharedUtils.saveString(getBaseContext(), "EMAIL", email);
                                        startActivity(new Intent(LoginActivity.this, UserActivity.class));
                                        Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                    }
                                    showProgressDialog(false);
                                }



                            });

                        } else {
                            Toast.makeText(LoginActivity.this, "Sai thiết bị", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        myRef.child(email.replace(".", "")).child("/idAndroid").setValue(id);
                        Toast.makeText(LoginActivity.this, "Thêm mới thiết bị thành công , mời đăng nhập lại", Toast.LENGTH_SHORT).show();
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                            if (isLogin){
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không chính xác." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    isLogin = false;

                                    finish();
                                    SharedUtils.saveString(getBaseContext(), "EMAIL", email);
                                    startActivity(new Intent(LoginActivity.this, UserActivity.class));
                                    Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                }
                                showProgressDialog(false);
                            }



                        });
                    }
                }

                showProgressDialog(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);

    }
}