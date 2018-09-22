package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.mHolder> {

    List<DataHolder> mList = new ArrayList<>();
    Context context;

    public AdapterClass(Context context, List mList) {
        this.mList = mList;
        this.context = context;
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

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Likes");
                try {
                    if (holder.isLiked == false) {
                        databaseReference.child(obj.Reference).setValue("True").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Added to likes", Toast.LENGTH_SHORT).show();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(obj.Reference).child("Likes");
                                reference.child(obj.Posted_By).setValue("Liked");
                                holder.imgLike.setImageResource(R.drawable.liked);
                                holder.isLiked = true;
                            }


                        });

                    } else {
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Likes").child(obj.Reference).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                holder.imgLike.setImageResource(R.drawable.unliked);
                                FirebaseDatabase.getInstance().getReference("Posts").child(obj.Reference).child("Likes").child(obj.Posted_By).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class mHolder extends RecyclerView.ViewHolder {

        ImageView imgPost;
        ImageView imgLike;
        ImageView imgShare;
        CircleImageView imgProfile;
        TextView txtName;
        TextView txtDesc;
        TextView txtLike;
        Boolean isLiked = false;

        public mHolder(View itemView) {
            super(itemView);

            imgPost = itemView.findViewById(R.id.img_post);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgShare = itemView.findViewById(R.id.imgShare);
            imgProfile = itemView.findViewById(R.id.img_profile);
            txtName = itemView.findViewById(R.id.txt_user_name);
            txtDesc = itemView.findViewById(R.id.txt_desc);
            txtLike = itemView.findViewById(R.id.txtLike);


        }

        public void setListener(final DataHolder obj, final mHolder myHolder) {
            imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }
}
