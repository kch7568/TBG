package com.cookandroid.tbg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<PostItem> postItemList;
    private Context context;

    public PostsAdapter(Context context, List<PostItem> postItemList) {
        this.context = context;
        this.postItemList = postItemList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem postItem = postItemList.get(position);
        holder.titleTextView.setText(postItem.getTitle());
        holder.authorTextView.setText(postItem.getNickname()); // 작성자 닉네임으로 수정
        holder.postCreateDayTextView.setText(postItem.getDate());
        holder.postViewsTextView.setText("조회수: " + postItem.getViews());
        holder.postLikesTextView.setText("좋아요: " + postItem.getLikes());

        // 작성자의 프로필 이미지 로드
        String profileImageUrl = postItem.getProfileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            // localhost URL을 Android 에뮬레이터용으로 변경
            if (profileImageUrl.contains("localhost")) {
                profileImageUrl = profileImageUrl.replace("localhost", "10.0.2.2");
            }
            Log.d("PostsAdapter", "Profile Image URL: " + profileImageUrl); // 로그 추가
            Glide.with(context)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.default_profile_image) // 로딩 중일 때 보여줄 기본 프로필 이미지
                    .error(R.drawable.error_image) // 로딩 실패 시 보여줄 이미지
                    .into(holder.postAuthorProfileImage);
            holder.postAuthorProfileImage.setVisibility(View.VISIBLE);
        } else {
            holder.postAuthorProfileImage.setVisibility(View.GONE); // 프로필 이미지가 없으면 숨김
        }

        // 게시글 이미지 로드
        String postImageUrl = postItem.getPostImageUrl();
        if (postImageUrl != null && !postImageUrl.isEmpty()) {
            // localhost URL을 Android 에뮬레이터용으로 변경
            if (postImageUrl.contains("localhost")) {
                postImageUrl = postImageUrl.replace("localhost", "10.0.2.2");
            }
            Log.d("PostsAdapter", "Post Image URL: " + postImageUrl); // 로그 추가
            Glide.with(context)
                    .load(postImageUrl)
                    .placeholder(R.drawable.tbg_icon) // 로딩 중일 때 보여줄 기본 게시글 이미지
                    .error(R.drawable.error_image) // 로딩 실패 시 보여줄 이미지
                    .into(holder.postImage);
            holder.postImage.setVisibility(View.VISIBLE);
        } else {
            holder.postImage.setVisibility(View.GONE); // 게시글 이미지가 없으면 숨김
        }
    }

    @Override
    public int getItemCount() {
        return postItemList != null ? postItemList.size() : 0; // postItemList가 null인지 확인
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView authorTextView;
        TextView postCreateDayTextView;
        TextView postViewsTextView;
        TextView postLikesTextView;
        ImageView postAuthorProfileImage;
        ImageView postImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.postTitle);
            authorTextView = itemView.findViewById(R.id.postAuthor);
            postCreateDayTextView = itemView.findViewById(R.id.postCreateDay);
            postViewsTextView = itemView.findViewById(R.id.postViews);
            postLikesTextView = itemView.findViewById(R.id.postLikes);
            postAuthorProfileImage = itemView.findViewById(R.id.postAuthorProfileImage);
            postImage = itemView.findViewById(R.id.postImage); // 게시글 첨부 이미지를 위한 ImageView
        }
    }
}
