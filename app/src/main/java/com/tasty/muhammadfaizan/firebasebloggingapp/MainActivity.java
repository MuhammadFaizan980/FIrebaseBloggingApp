package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton btnAdd;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        inflateMenu();
        addPost();
    }

    private void addPost() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ActivityNewPost.class));
                MainActivity.this.finish();
            }
        });
    }

    private void inflateMenu() {
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.item_settings: {
                        startActivity(new Intent(MainActivity.this, UserProfile.class));
                        break;
                    }
                    case R.id.item_logout: {
                        firebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        MainActivity.this.finish();
                    }
                }

                return false;
            }
        });
    }

    private void initViews() {
        btnAdd = findViewById(R.id.btnAddPost);
        toolbar = findViewById(R.id.mToolbar);
        firebaseAuth = FirebaseAuth.getInstance();
    }
}
