package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    CircleImageView imageView;
    EditText edtName;
    TextView txtName;
    Button btnSaveProfile;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    ProgressBar progressBar;
    Uri uriImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initViews();
        getProfileImage();
    }

    private void initViews() {
        imageView = findViewById(R.id.imgUser);
        edtName = findViewById(R.id.userProfile);
        txtName = findViewById(R.id.textName);
        btnSaveProfile = findViewById(R.id.btnSaveProf);
        progressBar = findViewById(R.id.pBarP);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("/Profile_Images/" + System.currentTimeMillis() + ".jpg");
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
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(UserProfile.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                                    edtName.setVisibility(View.INVISIBLE);
                                    edtName.setText("");
                                    txtName.setText(firebaseAuth.getCurrentUser().getDisplayName());
                                    txtName.setVisibility(View.VISIBLE);
                                    btnSaveProfile.setVisibility(View.INVISIBLE);
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
        if (!firebaseAuth.getCurrentUser().getDisplayName().equals("")) {
            txtName.setText(firebaseAuth.getCurrentUser().getDisplayName());
            txtName.setVisibility(View.VISIBLE);
            Picasso.get().load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(imageView);
        }
    }
}
