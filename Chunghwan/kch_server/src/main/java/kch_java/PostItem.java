package kch_java;

public class PostItem {
    private int postId;
    private String title;
    private String author;
    private String createDate;
    private String content;
    private String postImageUrl;
    private String profileImageUrl;  // 작성자의 프로필 이미지 URL 추가
    private int views;               // 조회수 추가
    private int likes;               // 좋아요 갯수 추가
    private String nickname;  // 닉네임 추가

    // 통합된 생성자
    public PostItem(int postId, String title, String author, String createDate, String content, String postImageUrl, String profileImageUrl, int views, int likes,  String nickname) {
        this.postId = postId;
        this.title = title;
        this.author = author;
        this.createDate = createDate;
        this.content = content;
        this.postImageUrl = postImageUrl;
        this.profileImageUrl = profileImageUrl;
        this.views = views;
        this.likes = likes;
        this.nickname = nickname;  // 닉네임 저장
    }

    // Getter 메서드들
    public int getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreateDate() {
        return createDate;
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
