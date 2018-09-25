package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton btnAdd;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerView;
    TextView txtNoContent;
    List<DataHolder> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        inflateMenu();
        addNewPost();

        inflateList();
        toolbar.setTitle("Home");

    }

    private void inflateList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        final AdapterClass mAdapter = new AdapterClass(MainActivity.this, mList, recyclerView);
        recyclerView.setAdapter(mAdapter);

        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    txtNoContent.setVisibility(View.VISIBLE);
                } else {
                    txtNoContent.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try {
            firebaseDatabase.getReference("Posts").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    DataHolder obj = dataSnapshot.getValue(DataHolder.class);
                    mList.add(obj);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    private void addNewPost() {
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
                        MainActivity.this.finish();
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
        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.MainList);
        txtNoContent = findViewById(R.id.txtNoContent);
        mList = new ArrayList<DataHolder>();
    }

}
