package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ActivityNewPost extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    Button btnUpload;
    EditText edtDesc;
    ImageView imgUpload;
    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    Uri imgUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        initViews();
        setListeners();
        uploadTask();
    }

    private void uploadTask() {
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 6);
                btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String postDesc = edtDesc.getText().toString().trim();
                        if (!postDesc.equals("")) {
                            progressBar.setVisibility(View.VISIBLE);
                            storageReference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                                    if (task.isSuccessful()) {
                                        String imageURL = String.valueOf(task.getResult().getDownloadUrl());
                                        String key = databaseReference.push().getKey();
                                        Map<String, String> mMap = new HashMap<>();
                                        mMap.put("Reference", key);
                                        mMap.put("post_url", imageURL);
                                        mMap.put("Description", postDesc);
                                        mMap.put("Posted_By", firebaseAuth.getCurrentUser().getDisplayName());
                                        mMap.put("User_Image", String.valueOf(firebaseAuth.getCurrentUser().getPhotoUrl()));
                                        databaseReference.child(key).setValue(mMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ActivityNewPost.this, "Post successful", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(ActivityNewPost.this, MainActivity.class));
                                                    ActivityNewPost.this.finish();
                                                } else {
                                                    Toast.makeText(ActivityNewPost.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }

                                }
                            });
                        } else {
                            edtDesc.setError("Cannot be empty");
                            edtDesc.requestFocus();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6 && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            imgUpload.setImageURI(imgUri);
            btnUpload.setVisibility(View.VISIBLE);
            edtDesc.setVisibility(View.VISIBLE);
        }
    }

    protected void initViews() {
        toolbar = findViewById(R.id.mToolbar);
        btnUpload = findViewById(R.id.btnUpload);
        edtDesc = findViewById(R.id.edtDesc);
        imgUpload = findViewById(R.id.imgUpload);
        progressBar = findViewById(R.id.pBarNew);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("/Post_Images/" + System.currentTimeMillis() + ".jpg");
    }

    protected void setListeners() {
        toolbar.setTitle("Add New Post");
        toolbar.setNavigationIcon(R.drawable.ic_go_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityNewPost.this, MainActivity.class));
                ActivityNewPost.this.finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ActivityNewPost.this, MainActivity.class));
        ActivityNewPost.this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.INVISIBLE);
    }
}
