package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ActivityImageDisplay extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        imageView = findViewById(R.id.imgDisplayImage);

        Picasso.get().load(String.valueOf(getIntent().getExtras().get("url"))).into(imageView);
    }
}
