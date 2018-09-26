package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView recyclerView;
    ImageView imgUser;
    Toolbar toolbar;
    List<DataHolder> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initViews();
        popCredentials();
        popList();
    }

    private void popCredentials() {
        collapsingToolbarLayout.setTitle((String) getIntent().getExtras().get("name"));
        Picasso.get().load((String) getIntent().getExtras().get("url")).into(imgUser);
        toolbar.setNavigationIcon(R.drawable.ic_go_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                ProfileActivity.this.finish();
            }
        });
    }

    private void popList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
        final profileAdapter adapter = new profileAdapter(ProfileActivity.this, list, recyclerView, (String) getIntent().getExtras().get("url"));
        recyclerView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference("Posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataHolder obj = dataSnapshot.getValue(DataHolder.class);
                list.add(obj);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        collapsingToolbarLayout = findViewById(R.id.myCollapse);
        recyclerView = findViewById(R.id.userProfilePostList);
        imgUser = findViewById(R.id.userProfileImageView);
        toolbar = findViewById(R.id.myToolbar);
        list = new ArrayList<DataHolder>();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        ProfileActivity.this.finish();
    }
}
