package com.agmohammed.smarttaxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DriverSettingsActivity extends AppCompatActivity {

    private EditText mDriverNameField, mDriverPhoneField;

    private Button mBack, mConfirm;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;

    private static String TAG= DriverMapActivity.class.getSimpleName();
    private String userID;
    private String mName;
    private String mPhone;
    private String mProfileImageUrl;

    private SharedPreferences sharedPreferences;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);

        mDriverNameField = (EditText) findViewById(R.id.driverName);
        mDriverPhoneField = (EditText) findViewById(R.id.driverPhone);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);


        sharedPreferences = getSharedPreferences("SmartTaxi", Context.MODE_PRIVATE);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

       mBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               finish();
               return;
           }
       });
    }

    private void getUserInfo(){

        if(sharedPreferences.contains("name")){
            mDriverNameField.setText(sharedPreferences.getString("name","null"));
            mDriverPhoneField.setText(sharedPreferences.getString("phone","null"));
        }
        else{
            mDriverDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("name")!=null){
                            mName = map.get("name").toString();
                            mDriverNameField.setText(mName);
                            editor.putString("name",mName);
                        }
                        if (map.get("phone")!=null){
                            mPhone = map.get("phone").toString();
                            mDriverPhoneField.setText(mPhone);
                            editor.putString("phone",mPhone);
                        }
                        if (map.get("profileImageUrl")!=null){
                            mProfileImageUrl = map.get("profileImageUrl").toString();
                            Log.wtf(TAG,"profile iamge url : "+mProfileImageUrl);
                            Glide.with(getApplicationContext()).load(mProfileImageUrl).into(mProfileImage);
                        }
                        editor.apply();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }




    }

    private void saveUserInformation() {
        mName = mDriverNameField.getText().toString();
        mPhone = mDriverPhoneField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        mDriverDatabase.updateChildren(userInfo);

        if(resultUri != null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DriverSettingsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    //Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                    String downloadUrl = filePath.getDownloadUrl().toString();

                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", downloadUrl);
                    mDriverDatabase.updateChildren(newImage);

                    Toast.makeText(DriverSettingsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}
