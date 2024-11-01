CREATE DATABASE TBG;
USE TBG;

-- 회원 테이블(User)
CREATE TABLE User (
    User_Id VARCHAR(20) NOT NULL,
    Password VARCHAR(16) NOT NULL,
    Nickname VARCHAR(16) NOT NULL,
    PRIMARY KEY (User_Id)
);

-- 게시판 분류 테이블(Category)
CREATE TABLE Category (
    Category_Code CHAR(1) NOT NULL,
    Category_Name VARCHAR(20) NOT NULL,
    PRIMARY KEY (Category_Code)
);

-- 게시글 테이블(Post)
CREATE TABLE Post (
    Post_Num INT UNSIGNED AUTO_INCREMENT NOT NULL,
    Category_Code CHAR(1) NOT NULL,
    Post_Title VARCHAR(200) NOT NULL,
    Post_Content TEXT NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    Post_createDay DATETIME NOT NULL,
    Post_Heart INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (Post_Num),
    FOREIGN KEY (Category_Code) REFERENCES Category(Category_Code),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 공지 및 푸시 알림 테이블(Alert)
CREATE TABLE Alert (
    Alert_Num INT UNSIGNED AUTO_INCREMENT NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    Alert_Status BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (Alert_Num),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 즐겨찾기 테이블(Favorites)
CREATE TABLE Favorites (
    User_Id VARCHAR(20) NOT NULL,
    Fav_Id INT UNSIGNED AUTO_INCREMENT NOT NULL,
    Post_Num INT UNSIGNED NOT NULL,
    PRIMARY KEY (Fav_Id),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id),
    FOREIGN KEY (Post_Num) REFERENCES Post(Post_Num)
);

-- 댓글 테이블(Comment)
CREATE TABLE Comment (
    Comment_Id INT UNSIGNED AUTO_INCREMENT NOT NULL,
    Comment_Date DATETIME NOT NULL,
    Comment_Contents VARCHAR(2000) NOT NULL,
    Post_Num INT UNSIGNED NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    PRIMARY KEY (Comment_Id),
    FOREIGN KEY (Post_Num) REFERENCES Post(Post_Num),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 사용자 쓴 글 테이블(MyPost)
CREATE TABLE MyPost (
    My_postId INT UNSIGNED AUTO_INCREMENT NOT NULL,
    Post_Num INT UNSIGNED NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    PRIMARY KEY (My_postId),
    FOREIGN KEY (Post_Num) REFERENCES Post(Post_Num),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 첨부파일 테이블(File)
CREATE TABLE File (
    File_num INT UNSIGNED AUTO_INCREMENT NOT NULL,
    File_name VARCHAR(200) NOT NULL,
    File_extension VARCHAR(10) NOT NULL,
    File_capacity VARCHAR(16) NOT NULL,
    Post_Num INT UNSIGNED NOT NULL,
    PRIMARY KEY (File_num),
    FOREIGN KEY (Post_Num) REFERENCES Post(Post_Num)
);

-- 사용자 작성한 댓글 테이블(MyComment)
CREATE TABLE MyComment (
    My_CommentId INT UNSIGNED AUTO_INCREMENT NOT NULL,
    Comment_Id INT UNSIGNED NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    PRIMARY KEY (My_CommentId),
    FOREIGN KEY (Comment_Id) REFERENCES Comment(Comment_Id),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 나의 프로필사진 테이블(MyProfile)
CREATE TABLE MyProfile (
    Profile_num INT UNSIGNED AUTO_INCREMENT NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    My_postSize VARCHAR(10),
    My_postextension VARCHAR(5),
    My_postURL VARCHAR(300) NOT NULL,
    PRIMARY KEY (Profile_num),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);
