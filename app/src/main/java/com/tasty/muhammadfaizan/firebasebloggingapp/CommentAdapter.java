package com.tasty.muhammadfaizan.firebasebloggingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.mHolder> {

    List<CommentDataHolder> myList = new ArrayList<CommentDataHolder>();
    Context context;

    public CommentAdapter(List list, Context context) {
        this.myList = list;
        this.context = context;
    }


    @Override
    public mHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row_design, parent, false);
        return new mHolder(view);
    }

    @Override
    public void onBindViewHolder(mHolder holder, int position) {

        CommentDataHolder obj = myList.get(position);
        holder.txtName.setText(obj.User_Name);
        holder.txtComment.setText(obj.User_Comment);
        Picasso.get().load(obj.User_Image).into(holder.imgProfile);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    class mHolder extends RecyclerView.ViewHolder {

        CircleImageView imgProfile;
        TextView txtName;
        TextView txtComment;

        public mHolder(View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.img_profile_comment);
            txtName = itemView.findViewById(R.id.txtCommentName);
            txtComment = itemView.findViewById(R.id.txtComment);
        }
    }
}
