// PostItem.java
package com.cookandroid.tbg;
public class PostItem {
    private int postNum;
    private String title;
    private String nickname;
    private String date;
    private String content;
    private String postImageUrl;
    private String profileImageUrl;
    private String videoUrl; // 동영상 URL 필드 추가
    private int views;
    private int likes;
    // 통합된 생성자
    // 새로운 필드를 포함한 생성자
    public PostItem(int postNum, String title, String nickname, String date, String content,
                    String postImageUrl, String profileImageUrl, String videoUrl, int views, int likes) {
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
}

