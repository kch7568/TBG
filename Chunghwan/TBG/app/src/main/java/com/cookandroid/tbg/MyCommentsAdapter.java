package com.cookandroid.tbg;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MyCommentsAdapter extends RecyclerView.Adapter<MyCommentsAdapter.MyCommentsViewHolder> {

    private Context context;
    private List<Comment> comments;
    private boolean isDeleting = false; // 삭제 중복 방지 플래그

    public MyCommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public MyCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_comments_item_layout, parent, false);
        return new MyCommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCommentsViewHolder holder, int position) {
        Comment comment = comments.get(position);

        // 댓글 내용 및 작성일 설정
        holder.commentContent.setText(comment.getContent());
        holder.commentDate.setText(comment.getDate());
        holder.relatedPostTitle.setText(comment.getAuthor()); // 관련 게시글 제목

        // 삭제 버튼 클릭 이벤트 처리
        holder.deleteCommentButton.setOnClickListener(v -> {
            if (isDeleting) {
                Toast.makeText(context, "삭제 작업이 진행 중입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 삭제 확인 다이얼로그 표시
            new AlertDialog.Builder(context)
                    .setTitle("댓글 삭제")
                    .setMessage("정말 삭제하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> deleteComment(comment.getCommentId(), position))
                    .setNegativeButton("아니오", null)
                    .show();
        });

        // 디버깅 로그
        Log.d("MyCommentsAdapter", "댓글 내용: " + comment.getContent());
        Log.d("MyCommentsAdapter", "작성일: " + comment.getDate());
        Log.d("MyCommentsAdapter", "관련 게시글 제목: " + comment.getAuthor());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private void deleteComment(String commentId, int position) {
        isDeleting = true; // 중복 삭제 방지 플래그 설정

        new Thread(() -> {
            try {
                // DELETE 요청을 서버로 전송
                URL url = new URL("http://10.0.2.2:8888/kch_server/DeleteComment?commentId=" + commentId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("MyCommentsAdapter", "댓글 삭제 성공: commentId=" + commentId);
                    // UI 업데이트
                    new Handler(Looper.getMainLooper()).post(() -> {
                        comments.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, comments.size());
                        Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        isDeleting = false; // 플래그 해제
                    });
                } else {
                    Log.e("MyCommentsAdapter", "댓글 삭제 실패. 응답 코드: " + responseCode);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "댓글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        isDeleting = false; // 플래그 해제
                    });
                }
            } catch (Exception e) {
                Log.e("MyCommentsAdapter", "댓글 삭제 중 오류 발생", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "댓글 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    isDeleting = false; // 플래그 해제
                });
            }
        }).start();
    }

    public static class MyCommentsViewHolder extends RecyclerView.ViewHolder {
        TextView commentContent, commentDate, relatedPostTitle;
        ImageButton deleteCommentButton;

        public MyCommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            commentContent = itemView.findViewById(R.id.commentContent);
            commentDate = itemView.findViewById(R.id.commentDate);
            relatedPostTitle = itemView.findViewById(R.id.relatedPostTitle); // 관련 게시글 제목 TextView
            deleteCommentButton = itemView.findViewById(R.id.deleteCommentButton);
        }
    }
}
