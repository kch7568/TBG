package com.cookandroid.tbg;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private final Context context;
    private final List<PostItem> favoritePosts;
    private final OnRemoveFavoriteListener removeFavoriteListener;

    // Constructor
    public FavoritesAdapter(Context context, List<PostItem> favoritePosts, OnRemoveFavoriteListener removeFavoriteListener) {
        this.context = context;
        this.favoritePosts = favoritePosts;
        this.removeFavoriteListener = removeFavoriteListener;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.favorites_item_layout, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        PostItem post = favoritePosts.get(position);

        // Set text content
        holder.postTitle.setText(post.getTitle());
        holder.postAuthor.setText("작성자: " + post.getNickname());
        holder.postDate.setText(post.getDate());
        holder.postViews.setText("조회수: " + post.getViews());
        holder.postLikes.setText("좋아요: " + post.getLikes());

        // Load main image (priority: postImageUrl > videoUrl > default image)
        if (post.getPostImageUrl() != null && !post.getPostImageUrl().isEmpty()) {
            if (post.getPostImageUrl().endsWith(".gif")) {
                // GIF 파일일 경우 첫 프레임 로드
                Glide.with(context)
                        .asBitmap() // GIF의 첫 프레임만 로드
                        .load(post.getPostImageUrl())
                        .placeholder(R.drawable.tbg_icon) // Default image
                        .into(holder.postImage);
            } else {
                // 일반 이미지일 경우
                Glide.with(context)
                        .load(post.getPostImageUrl())
                        .placeholder(R.drawable.tbg_icon) // Default image
                        .into(holder.postImage);
            }
        } else if (post.getVideoUrl() != null && !post.getVideoUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getVideoUrl())
                    .placeholder(R.drawable.tbg_icon) // Default image
                    .into(holder.postImage);
        } else {
            holder.postImage.setImageResource(R.drawable.tbg_icon); // Default image
        }

        // Load profile image
        if (post.getProfileImageUrl() != null && !post.getProfileImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getProfileImageUrl())
                    .placeholder(R.drawable.tbg_icon) // Default image
                    .into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.tbg_icon); // Default image
        }


        // 아이템 클릭 리스너 추가
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            // 게시글 번호를 String으로 변환하여 전달
            intent.putExtra("postNum", String.valueOf(post.getPostNum()));

            // 제목, 작성자, 날짜, 내용 등 기본 정보 전달
            intent.putExtra("title", post.getTitle());
            intent.putExtra("author", post.getNickname()); // 작성자 이름 전달
            intent.putExtra("date", post.getDate());
            intent.putExtra("content", post.getContent()); // 본문 내용 전달

            // 이미지와 비디오 URL 및 프로필 이미지 URL 전달
            if(post.getPostImageUrl() != null) {
                intent.putExtra("postImageUrl", post.getPostImageUrl()); // 게시글 이미지 URL 전달
            }if(post.getProfileImageUrl() != null) {
                intent.putExtra("profileImageUrl", post.getProfileImageUrl()); // 프로필 이미지 URL 전달
            }if( post.getVideoUrl() != null) {
                intent.putExtra("postVideoUrl", post.getVideoUrl());
            }
            // 조회수와 좋아요 개수 추가 전달
            intent.putExtra("views", post.getViews()); //이게 널일듯
            intent.putExtra("likes", post.getLikes());

            // 작성자 ID 전달
            intent.putExtra("authorId", post.getAuthor());

            context.startActivity(intent);
        });



        // Delete button click event with confirmation dialog
        holder.deleteButton.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("삭제 확인")
                    .setMessage("정말 삭제하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> {
                        // Call remove listener
                        removeFavoriteListener.onRemove(post.getPostNum());
                    })
                    .setNegativeButton("아니오", null) // Do nothing on cancel
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return favoritePosts.size();
    }

    // ViewHolder class
    public static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postAuthor, postDate, postViews, postLikes;
        ImageView profileImage, postImage;
        ImageButton deleteButton;

        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postAuthor = itemView.findViewById(R.id.postAuthor);
            postDate = itemView.findViewById(R.id.postCreateDay);
            postViews = itemView.findViewById(R.id.postViews);
            postLikes = itemView.findViewById(R.id.postLikes);
            profileImage = itemView.findViewById(R.id.postAuthorProfileImage);
            postImage = itemView.findViewById(R.id.postImage);
            deleteButton = itemView.findViewById(R.id.deleteFavoriteButton);
        }
    }

    // Interface for delete action
    public interface OnRemoveFavoriteListener {
        void onRemove(int postNum);
    }
}
