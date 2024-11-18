package com.cookandroid.tbg;

public class Comment {
    private String author; // 작성자
    private String content; // 내용
    private String date; // 날짜
    private String profileImageUrl; // 프로필 이미지 URL
    private String authorId; // 작성자 ID
    private String commentId; // 댓글 ID

    // Constructor
    public Comment(String author, String content, String date, String profileImageUrl, String authorId, String commentId) {
        this.author = author;
        this.content = content;
        this.date = date;
        this.profileImageUrl = profileImageUrl;
        this.authorId = authorId;
        this.commentId = commentId;
    }

    // Getter methods
    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getCommentId() {
        return commentId;
    }
}
