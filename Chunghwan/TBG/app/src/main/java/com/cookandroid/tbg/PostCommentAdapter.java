package com.cookandroid.tbg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList; // 올바른 Comment 클래스 사용

    public PostCommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_comment_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        com.cookandroid.tbg.Comment comment = commentList.get(position);
        holder.commentAuthor.setText(comment.getAuthor());
        holder.commentContent.setText(comment.getContent());
        holder.commentDate.setText(comment.getDate());

        String profileImageUrl = comment.getProfileImageUrl();
        Log.e("CommentProfileImage", "Loading profile image URL: " + profileImageUrl);

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            // localhost에서 10.0.2.2로 변경
            profileImageUrl = profileImageUrl.replace("localhost", "10.0.2.2");

            // Glide를 사용해 이미지 로드, 캐시 무효화 옵션 추가
            Glide.with(context)
                    .load(profileImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 캐시 무효화
                    .skipMemoryCache(true)
                    .into(holder.postAuthorProfileImage);
        } else {
            // 기본 이미지 설정
            holder.postAuthorProfileImage.setImageResource(R.drawable.default_profile_image);
            Log.e("CommentProfileImage", "Profile image URL is null or empty, using default image.");
        }
    }




    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentAuthor, commentContent, commentDate;
        ImageView postAuthorProfileImage;
        ImageButton editCommentButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            postAuthorProfileImage = itemView.findViewById(R.id.postAuthorProfileImage);
            commentAuthor = itemView.findViewById(R.id.postAuthor);
            commentContent = itemView.findViewById(R.id.commentContent);
            commentDate = itemView.findViewById(R.id.postCreateDay);
            editCommentButton = itemView.findViewById(R.id.editCommentButton);
        }
    }
}
