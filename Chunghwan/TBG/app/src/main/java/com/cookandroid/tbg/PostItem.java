// PostItem.java
package com.cookandroid.tbg;

public class PostItem {
    private String title;
    private String nickname;  // 닉네임 필드 추가
    private String date;
    private String profileImageUrl; // 작성자의 프로필 이미지 URL
    private String postImageUrl; // 게시글의 첨부 이미지 URL
    private int views; // 조회수
    private int likes; // 좋아요 갯수

    public PostItem(String title, String nickname, String date, String profileImageUrl, String postImageUrl, int views, int likes) {
        this.title = title;
        this.nickname = nickname;
        this.date = date;
        this.profileImageUrl = profileImageUrl;
        this.postImageUrl = postImageUrl;
        this.views = views;
        this.likes = likes;
    }

    // Getter 및 Setter 메서드
    public String getTitle() {
        return title;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDate() {
        return date;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public int getViews() {
        return views;
    }

    public int getLikes() {
        return likes;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
