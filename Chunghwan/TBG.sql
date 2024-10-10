-- 1. User 테이블 생성 (외래 키 참조 없음)
CREATE TABLE User (
    User_Id VARCHAR(20) NOT NULL,
    Password VARCHAR(16) NOT NULL,
    Nickname VARCHAR(16) NOT NULL,
    PRIMARY KEY (User_Id)
);

-- 2. Category 테이블 생성 (외래 키 참조 없음)
CREATE TABLE Category (
    Category_Code CHAR(1) NOT NULL,
    Category_Name VARCHAR(20) NOT NULL,
    PRIMARY KEY (Category_Code)
);

-- 3. MyPost 테이블 생성 (외래 키 참조 User)
CREATE TABLE MyPost (
    My_postId INT UNSIGNED AUTO_INCREMENT,
    Post_Numb INT UNSIGNED NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    PRIMARY KEY (My_postId),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 4. Post 테이블 생성 (외래 키 참조 User, Category)
CREATE TABLE Post (
    Post_Numb INT UNSIGNED AUTO_INCREMENT,
    Category_Code CHAR(1) NOT NULL,
    Post_Title VARCHAR(200) NOT NULL,
    Post_Content TEXT NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    File_Id INT UNSIGNED,
    Post_createDay DATETIME NOT NULL,
    PRIMARY KEY (Post_Numb),
    FOREIGN KEY (Category_Code) REFERENCES Category(Category_Code),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 5. File 테이블 생성 (외래 키 참조 MyPost)
CREATE TABLE File (
    File_num INT UNSIGNED AUTO_INCREMENT,
    File_Path VARCHAR(200) NOT NULL,
    File_Name VARCHAR(200) NOT NULL,
    File_extension VARCHAR(10) NOT NULL,
    File_capacity VARCHAR(16) NOT NULL,
    My_postId INT UNSIGNED NOT NULL,
    PRIMARY KEY (File_num),
    FOREIGN KEY (My_postId) REFERENCES MyPost(My_postId)
);

-- 6. Alert 테이블 생성 (외래 키 참조 User)
CREATE TABLE Alert (
    User_Id VARCHAR(20) NOT NULL,
    Alert_Id INT UNSIGNED AUTO_INCREMENT,
    Alert_Status BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (Alert_Id),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 7. Favorites 테이블 생성 (외래 키 참조 User)
CREATE TABLE Favorites (
    User_Id VARCHAR(20) NOT NULL,
    Fav_Id INT UNSIGNED NOT NULL,
    Post_Numb INT UNSIGNED NOT NULL,
    PRIMARY KEY (User_Id, Fav_Id, Post_Numb),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 8. MyComment 테이블 생성 (외래 키 참조 User)
CREATE TABLE MyComment (
    My_CommentId INT UNSIGNED NOT NULL,
    Comment_Id INT UNSIGNED NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    PRIMARY KEY (My_CommentId, Comment_Id, User_Id),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 9. Comment 테이블 생성 (외래 키 참조 User)
CREATE TABLE Comment (
    Comment_Id INT UNSIGNED AUTO_INCREMENT,
    Comment_Date DATETIME NOT NULL,
    Comment_Contents VARCHAR(200) NOT NULL,
    Post_Numb INT UNSIGNED NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    PRIMARY KEY (Comment_Id),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id)
);

-- 10. MyProfile 테이블 생성 (외래 키 참조 User, MyPost)
CREATE TABLE MyProfile (
    My_postId INT UNSIGNED NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    My_postSize INT UNSIGNED NOT NULL,
    My_postextension VARCHAR(10) NOT NULL,
    My_postURL VARCHAR(300) NOT NULL,
    PRIMARY KEY (My_postId, User_Id),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id),
    FOREIGN KEY (My_postId) REFERENCES MyPost(My_postId)
);
