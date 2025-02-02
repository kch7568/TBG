package com.cookandroid.tbg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.MyPostsViewHolder> {

    private Context context;
    private List<PostItem> posts;

    public MyPostsAdapter(Context context, List<PostItem> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public MyPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_posts_item_layout, parent, false);
        return new MyPostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostsViewHolder holder, int position) {
        PostItem post = posts.get(position);

        // 텍스트 데이터 설정
        holder.postTitle.setText(post.getTitle() != null ? post.getTitle() : "제목 없음");
        holder.postCreateDay.setText(post.getDate() != null ? post.getDate() : "날짜 정보 없음");
        holder.postViews.setText("조회수: " + post.getViews());
        holder.postLikes.setText("좋아요: " + post.getLikes());

        // 이미지 및 동영상 처리
        String postImageUrl = post.getPostImageUrl();
        String postVideoUrl = post.getVideoUrl();

        if (postImageUrl != null && !postImageUrl.isEmpty()) {
            if (postImageUrl.endsWith(".gif")) {
                // GIF 파일일 경우 첫 프레임 로드
                Glide.with(context)
                        .asBitmap() // GIF의 첫 프레임만 로드
                        .load(postImageUrl)
                        .placeholder(R.drawable.tbg_icon)
                        .error(R.drawable.tbg_icon)
                        .into(holder.postImage);
            } else {
                // 일반 이미지 파일 로드
                Glide.with(context)
                        .load(postImageUrl)
                        .placeholder(R.drawable.tbg_icon)
                        .error(R.drawable.tbg_icon)
                        .into(holder.postImage);
            }
        } else if (postVideoUrl != null && !postVideoUrl.isEmpty()) {
            try {
                // 동영상 썸네일 생성
                Bitmap thumbnail = getVideoThumbnail(postVideoUrl);
                if (thumbnail != null) {
                    holder.postImage.setImageBitmap(thumbnail);
                } else {
                    holder.postImage.setImageResource(R.drawable.tbg_icon); // 기본 이미지
                }
            } catch (Exception e) {
                Log.e("MyPostsAdapter", "Error creating video thumbnail", e);
                holder.postImage.setImageResource(R.drawable.tbg_icon); // 예외 발생 시 기본 이미지
            }
        } else {
            holder.postImage.setImageResource(R.drawable.tbg_icon); // 기본 이미지 설정
        }

        // 상세 페이지로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);

            // 게시글 데이터 전달
            intent.putExtra("postNum", String.valueOf(post.getPostNum()));
            intent.putExtra("title", post.getTitle() != null ? post.getTitle() : "제목 없음");
            intent.putExtra("author", post.getNickname() != null ? post.getNickname() : "작성자 없음");
            intent.putExtra("date", post.getDate() != null ? post.getDate() : "날짜 없음");
            intent.putExtra("content", post.getContent() != null ? post.getContent() : "내용 없음");
            intent.putExtra("postImageUrl", post.getPostImageUrl() != null ? post.getPostImageUrl() : "");
            intent.putExtra("profileImageUrl", post.getProfileImageUrl() != null ? post.getProfileImageUrl() : "");
            intent.putExtra("postVideoUrl", post.getVideoUrl() != null ? post.getVideoUrl() : "");
            intent.putExtra("views", post.getViews());
            intent.putExtra("likes", post.getLikes());
            intent.putExtra("authorId", post.getAuthor() != null ? post.getAuthor() : "작성자 없음");

            context.startActivity(intent);
        });

        // 삭제 버튼 처리
        holder.deletePostButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("게시글 삭제")
                    .setMessage("정말 게시글을 삭제하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> deletePost(post.getPostNum()))
                    .setNegativeButton("아니오", null)
                    .show();
        });
    }

    // 동영상 썸네일 생성 메서드
    private Bitmap getVideoThumbnail(String videoPath) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath, new HashMap<>());
            return retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            Log.e("MyPostsAdapter", "Error in getVideoThumbnail", e);
            return null;
        } finally {
            retriever.release();
        }
    }






    @Override
    public int getItemCount() {
        return posts.size();
    }

    // 게시글 삭제
    private void deletePost(int postNum) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // SharedPreferences에서 sessionId 로드
                SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                String sessionId = sharedPreferences.getString("sessionId", null);

                if (sessionId == null) {
                    showToast("로그인이 필요합니다.");
                    return;
                }

                // 서버로 DELETE 요청
                URL url = new URL("http://43.201.243.50:8080/kch_server/DeletePost?postNum=" + postNum + "&sessionId=" + sessionId);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 성공 시 UI 갱신
                    updateUIAfterDeletion(postNum);

                } else {
                    showToast("게시글 삭제 실패. 응답 코드: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("MyPostsAdapter", "게시글 삭제 중 오류 발생", e);
                showToast("게시글 삭제 중 오류 발생");
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // 삭제 후 UI 갱신
    private void updateUIAfterDeletion(int postNum) {
        new Handler(Looper.getMainLooper()).post(() -> {
            int position = getPostPositionById(postNum);
            if (position != -1) {
                posts.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, posts.size());
                showToast("게시글이 삭제되었습니다.");

                // 댓글 새로고침 로직 추가
                if (context instanceof MyPostsActivity) {
                    ((MyPostsActivity) context).fetchMyComments();
                }
            } else {
                Log.w("MyPostsAdapter", "삭제된 게시글을 찾을 수 없습니다. postNum=" + postNum);
            }
        });
    }


    // postNum으로 position 찾기
    private int getPostPositionById(int postNum) {
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getPostNum() == postNum) {
                return i;
            }
        }
        return -1;
    }

    // Toast 메시지
    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }

    public static class MyPostsViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postCreateDay, postViews, postLikes;
        ImageView postImage;
        ImageButton deletePostButton;

        public MyPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postCreateDay = itemView.findViewById(R.id.postCreateDay);
            postViews = itemView.findViewById(R.id.postViews);
            postLikes = itemView.findViewById(R.id.postLikes);
            postImage = itemView.findViewById(R.id.postImage);
            deletePostButton = itemView.findViewById(R.id.deletePostButton);
        }
    }
}
