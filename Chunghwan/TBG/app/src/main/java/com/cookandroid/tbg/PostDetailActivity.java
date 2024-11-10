package com.cookandroid.tbg;

import android.os.Bundle;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PostDetailActivity extends AppCompatActivity {

    private boolean isFavorited = false; // 즐겨찾기 여부 상태를 저장하는 변수
    private int likeCount = 0; // 좋아요 회수
    private ImageButton likeButton, starButton, commentCancelButton, commentSubmitButton;
    private EditText commentInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // XML 레이아웃 파일을 이 액티비티에 연결
        setContentView(R.layout.activity_postdetail);

        likeButton = findViewById(R.id.customLikeButton);
        starButton = findViewById(R.id.customStarButton);
        commentCancelButton = findViewById(R.id.commentCancel);
        commentSubmitButton = findViewById(R.id.commentSubmit);
        commentInput = findViewById(R.id.commentInput);

        // 즐겨찾기 버튼 클릭 이벤트 처리
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorited = !isFavorited; // 즐겨찾기 상태 토글

                // 애니메이션 효과
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        0.9f, 1.1f, 0.9f, 1.1f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                );
                scaleAnimation.setDuration(150); // 애니메이션 지속 시간
                v.startAnimation(scaleAnimation);

                // 즐겨찾기 상태에 따라 아이콘 변경
                if (isFavorited) {
                    starButton.setImageResource(R.drawable.ic_star_fill); // 즐겨찾기 활성화 상태 이미지
                    Toast.makeText(PostDetailActivity.this, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    starButton.setImageResource(R.drawable.ic_star_unfill); // 즐겨찾기 비활성화 상태 이미지
                    Toast.makeText(PostDetailActivity.this, "즐겨찾기에서 제거되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 좋아요 버튼 클릭 이벤트 처리
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeCount++; // 좋아요 회수 증가

                // 애니메이션 효과
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        0.9f, 1.1f, 0.9f, 1.1f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                );
                scaleAnimation.setDuration(150); // 애니메이션 지속 시간
                v.startAnimation(scaleAnimation);

                // 좋아요 버튼 클릭 시 토스트 메시지
                Toast.makeText(PostDetailActivity.this, "좋아요 " + likeCount + "회", Toast.LENGTH_SHORT).show();
            }
        });

        // 댓글 작성 취소 버튼 클릭 이벤트 처리
        commentCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 애니메이션 효과
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        0.9f, 1.1f, 0.9f, 1.1f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                );
                scaleAnimation.setDuration(150); // 애니메이션 지속 시간
                v.startAnimation(scaleAnimation);

                // 댓글 입력 필드를 초기화
                commentInput.setText("");
            }
        });

        // 댓글 작성 버튼 클릭 이벤트 처리 (구현 예정)
        commentSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 애니메이션 효과
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        0.9f, 1.1f, 0.9f, 1.1f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                );
                scaleAnimation.setDuration(150); // 애니메이션 지속 시간
                v.startAnimation(scaleAnimation);

                // 댓글 작성 로직 구현 예정
                Toast.makeText(PostDetailActivity.this, "댓글 작성 기능은 구현 중.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}