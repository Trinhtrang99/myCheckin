package com.example.mycheckin;

import static com.example.mycheckin.model.Common.EMAIL;
import static com.example.mycheckin.model.Common.USER;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.mycheckin.base.BaseActivity;
import com.example.mycheckin.databinding.ActivityUpdateUserBinding;
import com.example.mycheckin.model.UsersModel;
import com.example.mycheckin.utils.SharedUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class update_user extends BaseActivity {
    ActivityUpdateUserBinding binding;
    DatabaseReference myRef;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_user);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        String email = SharedUtils.getString(this, EMAIL, "");
        if (email.equals("admin@gmail.com")) {
            binding.txtName.setText("Admin");
            binding.txtEmail.setText("admin@gmail.com");
        } else {
            myRef = database.getReference(USER).child(email.replace(".", ""));
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UsersModel usersModel = snapshot.getValue(UsersModel.class);
                    binding.txtName.setText(usersModel.getName());
                    binding.txtEmail.setText(usersModel.getEmaul());
                    binding.edtNgaysinh.setText(usersModel.getBirthday());
                    binding.edtSdt.setText(usersModel.getPhone());
                    binding.textPossition.setText(usersModel.getPosition());
                    if (usersModel.getUrl()!=null){
                        Glide.with(update_user.this)
                                .load(usersModel.getUrl())
                                .centerCrop()
                                .into(binding.avatar);
                    }

                    showProgressDialog(false);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showProgressDialog(false);

                }
            });
        }

        binding.btnUpdate.setOnClickListener(v -> {
            updateData();
            Toast.makeText(update_user.this, "Cập nhật thông tin thành công ", Toast.LENGTH_SHORT).show();
            finish();
        });
        binding.avatar.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                }
            } else {
                startGallery();
            }
        });
    }

    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    String url;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri returnUri;
        returnUri = data.getData();

        Glide.with(this)
                .load(returnUri)
                .override(1280, 1280)
                .centerCrop()
                .into(binding.avatar);
        //  uploadImage(returnUri);
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), returnUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();
            UploadTask uploadTask = ref.putBytes(data1);
            uploadTask.addOnFailureListener(exception -> {
               // Toast.makeText(update_user.this, "Failed " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            url = task.getResult().toString();
                        }
                    }
                });
               // Toast.makeText(update_user.this, "done ", Toast.LENGTH_SHORT).show();
              //  url = taskSnapshot.getDownloadUrl().toString();
                // ...
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateData() {

        myRef.child("/phone").setValue(binding.edtSdt.getText().toString());
        myRef.child("/url").setValue(url);
    }


}