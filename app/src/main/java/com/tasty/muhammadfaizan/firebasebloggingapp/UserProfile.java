package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    CircleImageView imageView;
    EditText edtName;
    TextView txtName;
    Button btnSaveProfile;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    ProgressBar progressBar;
    Toolbar toolbar;
    Uri uriImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initViews();
        getProfileImage();
        toolbar.setTitle("User Profile");
        toolbar.setNavigationIcon(R.drawable.ic_go_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()) == null || String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).equals("")) {
                    UserProfile.this.finish();
                } else {
                    startActivity(new Intent(UserProfile.this, MainActivity.class));
                    UserProfile.this.finish();
                }
            }
        });
    }

    private void initViews() {
        imageView = findViewById(R.id.imgUser);
        edtName = findViewById(R.id.userProfile);
        txtName = findViewById(R.id.textName);
        btnSaveProfile = findViewById(R.id.btnSaveProf);
        progressBar = findViewById(R.id.pBarP);
        toolbar = findViewById(R.id.mToolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("/Profile_Images/" + firebaseAuth.getCurrentUser().getUid() + ".jpg");
    }

    public void getProfileImage() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 6);
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = edtName.getText().toString().trim();
                if (name.equals("")) {
                    edtName.setError("Cannot be empty!");
                    edtName.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    storageReference.putFile(uriImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String imgUri = String.valueOf(taskSnapshot.getDownloadUrl());
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(Uri.parse(imgUri)).build();
                            firebaseAuth.getCurrentUser().updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UserProfile.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                                    Map<String, String> mMap = new HashMap<>();
                                    mMap.put("Name", firebaseAuth.getCurrentUser().getDisplayName());
                                    mMap.put("Image_URL", String.valueOf(firebaseAuth.getCurrentUser().getPhotoUrl()));
                                    firebaseDatabase.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).push().setValue(mMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            edtName.setVisibility(View.INVISIBLE);
                                            edtName.setText("");
                                            txtName.setText(firebaseAuth.getCurrentUser().getDisplayName());
                                            txtName.setVisibility(View.VISIBLE);
                                            btnSaveProfile.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(UserProfile.this, MainActivity.class));
                                            UserProfile.this.finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UserProfile.this, "Insertion Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            });
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6 && resultCode == RESULT_OK) {
            uriImg = data.getData();
            edtName.setVisibility(View.VISIBLE);
            imageView.setImageURI(uriImg);
            edtName.setVisibility(View.VISIBLE);
            btnSaveProfile.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.INVISIBLE);
        if (firebaseAuth.getCurrentUser().getDisplayName() != null) {
            txtName.setText(firebaseAuth.getCurrentUser().getDisplayName());
            txtName.setVisibility(View.VISIBLE);
            Picasso.get().load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(imageView);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UserProfile.this, MainActivity.class));
        UserProfile.this.finish();
    }
}
