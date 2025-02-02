// PostItem.java
package com.cookandroid.tbg;

import com.google.gson.annotations.SerializedName;

public class PostItem {
    private int postNum;
    private String title;
    private String nickname;
    private String date;
    private String content;
    @SerializedName("postImageUrl") // 서버 JSON 키와 매핑
    private String postImageUrl;
    private String profileImageUrl;
    @SerializedName("postVideoUrl") // 서버 JSON 키와 매핑
    private String videoUrl; // 동영상 URL 필드 추가
    private String authorid;
    private int views;
    private int likes;
    // 통합된 생성자
    // 새로운 필드를 포함한 생성자
    public PostItem(int postNum, String title, String nickname, String date, String content,
                    String postImageUrl, String profileImageUrl, String videoUrl, int views, int likes, String authorid) {
        this.postNum = postNum;
        this.title = title;
        this.nickname = nickname;
        this.date = date;
        this.content = content;
        this.postImageUrl = postImageUrl;
        this.profileImageUrl = profileImageUrl;
        this.videoUrl = videoUrl; // 필드 초기화
        this.views = views;
        this.likes = likes;
        this.authorid = authorid;
    }

    // Getter 메서드들
    public int getPostNum() {
        return postNum;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return nickname;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // Getter 메서드 추가
    public String getVideoUrl() {
        return videoUrl;
    }

    public int getViews() {
        return views;
    }

    public int getLikes() {
        return likes;
    }

    public String getNickname() {
        return nickname;
    }
    // Setter 메서드들 추가
    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setAuthorId(String authorid) {
        this.authorid = authorid;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setAuthor(String author) {
        this.nickname = author;
    }

}

