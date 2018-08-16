package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ActivityNewPost extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    Button btnUpload;
    EditText edtDesc;
    ImageView imgUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        initViews();
        setListeners();
    }

    protected void initViews() {
        toolbar = findViewById(R.id.mToolbar);
        btnUpload = findViewById(R.id.btnUpload);
        edtDesc = findViewById(R.id.edtDesc);
        imgUpload = findViewById(R.id.imgUpload);
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
}
