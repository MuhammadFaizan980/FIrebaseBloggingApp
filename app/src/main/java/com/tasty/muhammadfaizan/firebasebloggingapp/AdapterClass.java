package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.mHolder>{

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
    public void onBindViewHolder(mHolder holder, int position) {
        DataHolder obj = mList.get(position);
        String name = obj.Posted_By;
        String desc = obj.Description;
        String post_url = obj.post_url;
        String profile_img = obj.User_Image;
        holder.txtName.setText(name);
        holder.txtDesc.setText(desc);
        Picasso.get().load(profile_img).into(holder.imgProfile);
        Picasso.get().load(post_url).into(holder.imgPost);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class mHolder extends RecyclerView.ViewHolder{

        ImageView imgPost;
        CircleImageView imgProfile;
        TextView txtName;
        TextView txtDesc;
        public mHolder(View itemView) {
            super(itemView);

            imgPost = itemView.findViewById(R.id.img_post);
            imgProfile = itemView.findViewById(R.id.img_profile);
            txtName = itemView.findViewById(R.id.txt_user_name);
            txtDesc = itemView.findViewById(R.id.txt_desc);


        }
    }
}
