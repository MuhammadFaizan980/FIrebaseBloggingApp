package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
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
                        final String des = edtDesc.getText().toString().trim();
                        if (des.equals("")) {
                            edtDesc.setError("Cannot be empty!");
                            edtDesc.requestFocus();
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                            storageReference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String uri = task.getResult().getDownloadUrl().toString();
                                        Map<String, Object> mMap = new HashMap<>();
                                        mMap.put("Image_URL", uri);
                                        mMap.put("Description", des);
                                        mMap.put("User_ID", FirebaseAuth.getInstance().getUid());
                                        firebaseFirestore.collection("Posts").add(mMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()){
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    btnUpload.setVisibility(View.INVISIBLE);
                                                    edtDesc.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(ActivityNewPost.this, "New post added", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(ActivityNewPost.this, MainActivity.class));
                                                    ActivityNewPost.this.finish();
                                                } else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(ActivityNewPost.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(ActivityNewPost.this, "Image Upload Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
        firebaseFirestore = FirebaseFirestore.getInstance();
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
        progressBar.setVisibility(View.INVISIBLE);;
    }
}
