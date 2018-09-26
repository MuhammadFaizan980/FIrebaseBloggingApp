package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileAdapter extends RecyclerView.Adapter<profileAdapter.mHolder> {

    List<DataHolder> mList = new ArrayList<>();
    Context context;
    RecyclerView recyclerView;
    String url;

    public profileAdapter(Context context, List mList, RecyclerView recyclerView, String url) {
        this.mList = mList;
        this.context = context;
        this.recyclerView = recyclerView;
        this.url = url;
    }

    @Override
    public mHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_row_design, parent, false);

        return new mHolder(view);
    }

    @Override
    public void onBindViewHolder(final mHolder holder, int position) {
        final DataHolder obj = mList.get(position);
        String name = obj.Posted_By;
        String desc = obj.Description;
        String post_url = obj.post_url;
        String profile_img = obj.User_Image;
        holder.txtName.setText(name);
        holder.txtDesc.setText(desc);
        Picasso.get().load(profile_img).into(holder.imgProfile);
        Picasso.get().load(post_url).into(holder.imgPost);

        if (!url.equals(obj.User_Image)) {
            holder.constraintLayout.setMaxHeight(0);
        }

        if (url.equals(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()) )) {
            holder.imgDelete.setVisibility(View.VISIBLE);
        }




                holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Warning!");
                        alertDialog.setMessage("You are about to delete this post.\nIt will no longer be available to you or anyone else.\nAre you sure?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference("Posts").child(obj.Reference).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Likes").child(obj.Reference).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "The post was deleted", Toast.LENGTH_SHORT).show();
                                                        holder.constraintLayout.setMaxHeight(0);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }
                });
                holder.imgLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerView.getRecycledViewPool().clear();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Likes");
                        try {
                            if (holder.isLiked == false) {
                                databaseReference.child(obj.Reference).setValue("True").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseDatabase.getInstance().getReference("Posts").child(obj.Reference).child("Likes").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue("Liked").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    holder.imgLike.setImageResource(R.drawable.liked);
                                                    holder.isLiked = true;
                                                    Toast.makeText(context, "Added to likes", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }


                                });

                            } else {
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Likes").child(obj.Reference).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.imgLike.setImageResource(R.drawable.unliked);
                                        FirebaseDatabase.getInstance().getReference("Posts").child(obj.Reference).child("Likes").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Removed from likes", Toast.LENGTH_SHORT).show();
                                                holder.isLiked = false;
                                            }
                                        });
                                    }
                                });
                            }

                        } catch (Exception e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });


                final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Likes");
                try {
                    mDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(obj.Reference).exists()) {
                                holder.imgLike.setImageResource(R.drawable.liked);
                                holder.isLiked = true;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    FirebaseDatabase.getInstance().getReference("Posts").child(obj.Reference).child("Likes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            holder.txtLike.setText(String.valueOf((int) dataSnapshot.getChildrenCount()));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } catch (Exception e) {
                }


                holder.imgShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("Post share");
                        dialog.setMessage("Are you sure you want to share this post?");
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String shareKey = FirebaseDatabase.getInstance().getReference("Posts").push().getKey();
                                Map<String, String> mMap = new HashMap<String, String>();
                                mMap.put("Reference", shareKey);// to be done
                                mMap.put("post_url", obj.post_url);
                                mMap.put("Description", obj.Description);
                                mMap.put("Posted_By", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                mMap.put("User_Image", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));

                                FirebaseDatabase.getInstance().getReference("Posts").child(shareKey).setValue(mMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "You shared this post", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        dialog.setCancelable(false);

                        dialog.create();
                        dialog.show();
                    }
                });

                holder.imgComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
                        View mView = LayoutInflater.from(context).inflate(R.layout.comment_layout, null);
                        mDialog.setView(mView);
                        final AlertDialog alertDialog = mDialog.create();
                        alertDialog.setCancelable(true);

                        RecyclerView recyclerView = mView.findViewById(R.id.commentRecycler);
                        final EditText edtComment = mView.findViewById(R.id.edtComment);
                        ImageView imgSend = mView.findViewById(R.id.imgSave);
                        final List<CommentDataHolder> myList = new ArrayList<CommentDataHolder>();
                        final TextView txtNoComment = mView.findViewById(R.id.txtNoComment);

                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        CommentAdapter commentAdapter = new CommentAdapter(myList, context);
                        recyclerView.setAdapter(commentAdapter);

                        DatabaseReference commentReference = FirebaseDatabase.getInstance().getReference("Posts").child(obj.Reference).child("Comments");

                        commentReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                CommentDataHolder myHolder = dataSnapshot.getValue(CommentDataHolder.class);
                                myList.add(myHolder);
                                notifyDataSetChanged();
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
                        commentReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() == 0) {
                                    txtNoComment.setVisibility(View.VISIBLE);
                                } else {
                                    txtNoComment.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        imgSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (edtComment.getText().toString().trim().equals("")) {
                                    edtComment.setError("Cannot be empty!");
                                } else {
                                    String comment = edtComment.getText().toString().trim();
                                    String profileImage = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                                    String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                    DatabaseReference cReference = FirebaseDatabase.getInstance().getReference("Posts").child(obj.Reference).child("Comments");
                                    Map<String, String> myMap = new HashMap<>();
                                    myMap.put("User_Image", profileImage);
                                    myMap.put("User_Name", userName);
                                    myMap.put("User_Comment", comment);
                                    cReference.push().setValue(myMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "You commented on this post", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            } else {
                                                Toast.makeText(context, "Error posting comment, please try again later", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            }
                        });


                        alertDialog.show();
                    }
                });

                holder.imgPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ActivityImageDisplay.class);
                        intent.putExtra("url", obj.post_url);
                        context.startActivity(intent);
                    }
                });

            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }


            @Override
            public int getItemCount() {
                return mList.size();
            }

            class mHolder extends RecyclerView.ViewHolder {

                ImageView imgPost;
                ImageView imgLike;
                ImageView imgShare;
                ImageView imgComment;
                ImageView imgDelete;
                CircleImageView imgProfile;
                TextView txtName;
                TextView txtDesc;
                TextView txtLike;
                ConstraintLayout constraintLayout;
                Boolean isLiked = false;

                public mHolder(View itemView) {
                    super(itemView);

                    imgDelete = itemView.findViewById(R.id.imgDelete);
                    imgPost = itemView.findViewById(R.id.img_post);
                    imgLike = itemView.findViewById(R.id.imgLike);
                    imgShare = itemView.findViewById(R.id.imgShare);
                    imgComment = itemView.findViewById(R.id.imgComment);
                    imgProfile = itemView.findViewById(R.id.img_profile);
                    txtName = itemView.findViewById(R.id.txt_user_name);
                    constraintLayout = itemView.findViewById(R.id.constLayout);
                    txtDesc = itemView.findViewById(R.id.txt_desc);
                    txtLike = itemView.findViewById(R.id.txtLike);


                }

            }
}
