package com.cookandroid.tbg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private String currentUserId;

    public PostCommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        fetchCurrentUserId();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_comment_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
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

        // 댓글 작성자인 경우 삭제 메뉴를 표시
        if (comment.getAuthorId() != null && comment.getAuthorId().equals(currentUserId)) {
            holder.editCommentButton.setVisibility(View.VISIBLE);
            holder.editCommentButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.editCommentButton);
                popupMenu.inflate(R.menu.comment_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.delete_comment) {
                        showDeleteConfirmationDialog(comment.getCommentId());
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
            Log.d("AuthorCheck", "Author ID: " + comment.getAuthorId() + ", Current User ID: " + currentUserId);
        } else {
            Log.d("AuthorCheck", "Author ID: " + comment.getAuthorId() + ", Current User ID: " + currentUserId);
            holder.editCommentButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private void showDeleteConfirmationDialog(String commentId) {
        new AlertDialog.Builder(context)
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> deleteComment(commentId))
                .setNegativeButton("아니오", null)
                .show();
    }

    private void deleteComment(String commentId) {
        // 댓글 삭제 로직
        new Thread(() -> {
            try {
                // 서버에 댓글 삭제 요청
                // 예제 URL, 실제 서버 주소로 변경 필요
                URL url = new URL("http://10.0.2.2:8888/kch_server/DeleteComment?commentId=" + commentId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 삭제 성공 시 UI 업데이트
                    new Handler(Looper.getMainLooper()).post(() -> {
                        int position = getCommentPositionById(commentId);
                        if (position != -1) {
                            commentList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("DeleteComment", "Failed to delete comment. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("DeleteComment", "Error occurred while deleting comment", e);
            }
        }).start();
    }

    private int getCommentPositionById(String commentId) {
        for (int i = 0; i < commentList.size(); i++) {
            if (commentList.get(i).getCommentId().equals(commentId)) {
                return i;
            }
        }
        return -1;
    }

    private void fetchCurrentUserId() {
        new Thread(() -> {
            try {
                // 서버에 현재 사용자 정보 요청
                URL url = new URL("http://10.0.2.2:8888/kch_server/GetCurrentUser?sessionId=" + getSessionId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject userJson = new JSONObject(response.toString());
                    currentUserId = userJson.getString("userId");
                    Log.d("FetchCurrentUser", "Current User ID: " + currentUserId);
                } else {
                    Log.e("FetchCurrentUser", "Failed to fetch current user. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("FetchCurrentUser", "Error occurred while fetching current user", e);
            }
        }).start();
    }

    private String getSessionId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("sessionId", "");
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
            editCommentButton = itemView.findViewById(R.id.commentOptionsMenu);  //이름바꿈
        }
    }
}
