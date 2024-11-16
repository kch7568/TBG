package kch_java;

public class PostItem {
    private int postNum;
    private String title;
    private String author;
    private String createDate;
    private String content;
    private String postImageUrl;
    private String postVideoUrl; // 동영상 URL 필드 추가
    private String profileImageUrl;
    private int views;
    private int likes;
    private String nickname;

    // 생성자 수정 (postVideoUrl 추가)
    public PostItem(int postNum, String title, String author, String createDate, String content, 
                    String postImageUrl, String postVideoUrl, String profileImageUrl, int views, int likes, String nickname) {
        this.postNum = postNum;
        this.title = title;
        this.author = author;
        this.createDate = createDate;
        this.content = content;
        this.postImageUrl = postImageUrl;
        this.postVideoUrl = postVideoUrl; // 동영상 URL 저장
        this.profileImageUrl = profileImageUrl;
        this.views = views;
        this.likes = likes;
        this.nickname = nickname;
    }

    // Getter 메서드들
    public int getPostNum() { return postNum; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCreateDate() { return createDate; }
    public String getContent() { return content; }
    public String getPostImageUrl() { return postImageUrl; }
    public String getPostVideoUrl() { return postVideoUrl; } // 새로 추가된 getter
    public String getProfileImageUrl() { return profileImageUrl; }
    public int getViews() { return views; }
    public int getLikes() { return likes; }
    public String getNickname() { return nickname; }
}
