package com.cookandroid.tbg;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private Context context;
    private List<PostItem> postItemList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(PostItem post);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public PostsAdapter(Context context, List<PostItem> postItemList) {
        this.context = context;
        this.postItemList = postItemList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item_layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem post = postItemList.get(position);
        Log.d("PostsAdapter", "onBindViewHolder called for position: " + position);

        // 기본 데이터 설정
        holder.titleTextView.setText(post.getTitle());
        holder.nicknameTextView.setText(post.getNickname());
        holder.dateTextView.setText(post.getDate());
        holder.viewsTextView.setText("조회수: " + post.getViews());
        holder.likesTextView.setText("좋아요: " + post.getLikes());

        // URL에서 localhost를 10.0.2.2로 대체
        String profileImageUrl = post.getProfileImageUrl().replace("localhost", "10.0.2.2");
        String postImageUrl = post.getPostImageUrl().replace("localhost", "10.0.2.2");
        String postVideoUrl = post.getVideoUrl().replace("localhost", "10.0.2.2");

        // URL 로그 출력
        Log.d("PostsAdapter", "Modified profile image URL: " + profileImageUrl);
        Log.d("PostsAdapter", "Modified post image URL: " + postImageUrl);
        Log.d("PostsAdapter", "Modified post video URL: " + postVideoUrl);

        // 프로필 이미지 로드
        Glide.with(context).load(profileImageUrl).into(holder.profileImageView);

        // 동영상, GIF, 또는 이미지 썸네일 표시
        if (!postVideoUrl.isEmpty()) { // 동영상 파일일 경우
            try {
                Bitmap thumbnail = getVideoThumbnail(postVideoUrl);
                if (thumbnail != null) {
                    holder.postImageView.setImageBitmap(thumbnail);
                } else {
                    holder.postImageView.setImageResource(R.drawable.tbg_icon); // 기본 이미지 설정
                }
            } catch (IOException e) {
                e.printStackTrace();
                holder.postImageView.setImageResource(R.drawable.tbg_icon); // 예외 발생 시 기본 이미지 설정
            }
        } else if (postImageUrl.endsWith(".gif")) { // GIF 파일일 경우
            Glide.with(context)
                    .asBitmap() // GIF의 첫 프레임만 로드
                    .load(postImageUrl)
                    .into(holder.postImageView);

        } else if (!postImageUrl.isEmpty()) { // 일반 이미지 파일일 경우
            Glide.with(context).load(postImageUrl).into(holder.postImageView);
        } else {
            holder.postImageView.setImageResource(R.drawable.tbg_icon); // 이미지나 동영상이 없을 경우 기본 이미지 설정
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(post);
            }
        });
    }








    @Override
    public int getItemCount() {
        return postItemList.size();
    }

    // 동영상 썸네일 생성 메서드
    private Bitmap getVideoThumbnail(String videoPath) throws IOException {
        Log.d("PostsAdapter", "getVideoThumbnail called with path: " + videoPath);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath, new HashMap<>());
            Bitmap bitmap = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (bitmap != null) {
                Log.d("PostsAdapter", "Thumbnail successfully retrieved");
            } else {
                Log.d("PostsAdapter", "Thumbnail retrieval failed");
            }
            return bitmap;
        } catch (Exception e) {
            Log.e("PostsAdapter", "Error in getVideoThumbnail", e);
            return null;
        } finally {
            retriever.release();
        }
    }





    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, nicknameTextView, dateTextView, viewsTextView, likesTextView;
        ImageView profileImageView, postImageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.postTitle);
            nicknameTextView = itemView.findViewById(R.id.postAuthor);
            dateTextView = itemView.findViewById(R.id.postCreateDay);
            viewsTextView = itemView.findViewById(R.id.postViews);
            likesTextView = itemView.findViewById(R.id.postLikes);
            profileImageView = itemView.findViewById(R.id.postAuthorProfileImage);
            postImageView = itemView.findViewById(R.id.postImage);
        }
    }
}
