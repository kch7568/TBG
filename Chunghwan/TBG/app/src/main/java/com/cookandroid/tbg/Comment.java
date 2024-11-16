package com.cookandroid.tbg;

public class Comment {
    private String author; // 작성자
    private String content; // 내용
    private String date; // 날짜
    private String profileImageUrl; // 프로필 이미지 URL

    // Constructor
    public Comment(String author, String content, String date, String profileImageUrl) {
        this.author = author;
        this.content = content;
        this.date = date;
        this.profileImageUrl = profileImageUrl;
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
}
