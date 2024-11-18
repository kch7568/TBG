-- (User)
CREATE TABLE User (
    User_Id VARCHAR(20) NOT NULL,
    Password VARCHAR(16) NOT NULL,
    Nickname VARCHAR(16) NOT NULL,
    PRIMARY KEY (User_Id)
);

-- 게시판 분류 테이블 (Category)
CREATE TABLE Category (
    Category_Code CHAR(1) NOT NULL,
    Category_Name VARCHAR(20) NOT NULL,
    PRIMARY KEY (Category_Code)
);


-- 게시글 테이블 (Post)
CREATE TABLE Post (
    Post_Num INT UNSIGNED AUTO_INCREMENT NOT NULL,
    Category_Code CHAR(1) NOT NULL,
    Post_Title VARCHAR(200) NOT NULL,
    Post_Content TEXT NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    Post_hits INT UNSIGNED NOT NULL DEFAULT 0,
    Post_createDay DATETIME NOT NULL,
    Post_Heart INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (Post_Num),
    FOREIGN KEY (Category_Code) REFERENCES Category(Category_Code),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id) ON DELETE CASCADE
);


-- (Alert)
CREATE TABLE Alert (
    Alert_Num INT UNSIGNED AUTO_INCREMENT NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    Alert_Status BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (Alert_Num),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id) ON DELETE CASCADE
);

-- Favorites 테이블
CREATE TABLE Favorites (
    User_Id VARCHAR(20) NOT NULL,      -- 사용자 ID
    Post_Num INT UNSIGNED NOT NULL,    -- 게시물 번호
    PRIMARY KEY (User_Id, Post_Num),   -- User_Id와 Post_Num의 조합으로 중복 방지
    FOREIGN KEY (User_Id) REFERENCES User(User_Id) ON DELETE CASCADE,  -- 사용자 삭제 시 즐겨찾기도 삭제
    FOREIGN KEY (Post_Num) REFERENCES Post(Post_Num) ON DELETE CASCADE -- 게시물 삭제 시 즐겨찾기도 삭제
);


-- (Comment)
CREATE TABLE Comment (
    Comment_Id INT UNSIGNED AUTO_INCREMENT NOT NULL,
    Comment_Date DATETIME NOT NULL,
    Comment_Contents VARCHAR(2000) NOT NULL,
    Post_Num INT UNSIGNED NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    PRIMARY KEY (Comment_Id),
    FOREIGN KEY (Post_Num) REFERENCES Post(Post_Num) ON DELETE CASCADE,   -- 수정
    FOREIGN KEY (User_Id) REFERENCES User(User_Id) ON DELETE CASCADE
);

-- (MyPost)
CREATE TABLE MyPost (
    My_postId INT UNSIGNED AUTO_INCREMENT NOT NULL,
    Post_Num INT UNSIGNED NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    PRIMARY KEY (My_postId),
    FOREIGN KEY (Post_Num) REFERENCES Post(Post_Num),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id) ON DELETE CASCADE
);

-- 첨부파일 테이블 (File)
CREATE TABLE File (
    File_num INT UNSIGNED AUTO_INCREMENT NOT NULL,
    Post_Num INT UNSIGNED NOT NULL,
    File_Path VARCHAR(200) NOT NULL,
    File_name VARCHAR(200) NOT NULL,
    File_extension VARCHAR(200) NOT NULL,
    File_capacity VARCHAR(16) NOT NULL,
    PRIMARY KEY (File_num),
    FOREIGN KEY (Post_Num) REFERENCES Post(Post_Num) ON DELETE CASCADE
);


-- (MyComment)
CREATE TABLE MyComment (
    My_CommentId INT UNSIGNED AUTO_INCREMENT NOT NULL,
    Comment_Id INT UNSIGNED NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    PRIMARY KEY (My_CommentId),
    FOREIGN KEY (Comment_Id) REFERENCES Comment(Comment_Id),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id) ON DELETE CASCADE
);

-- (MyProfile)
CREATE TABLE MyProfile (
    Profile_num INT UNSIGNED AUTO_INCREMENT NOT NULL,
    User_Id VARCHAR(20) NOT NULL,
    My_postSize VARCHAR(10),
    My_postextension VARCHAR(5),
    My_postURL VARCHAR(300) NOT NULL,
    PRIMARY KEY (Profile_num),
    FOREIGN KEY (User_Id) REFERENCES User(User_Id) ON DELETE CASCADE
);



-- User_Id 참조하는 모든 외래키에 Cascade 설정(삭제하면 다지워지게)
 -- Favorites 구조 수정(한 게시글당 즐겨찾기 중복 방지)


INSERT INTO Category (Category_Code, Category_Name) VALUES
('A', '관광명소'),
('B', '교통수단'),
('C', '호텔'),
('D', '자유게시판');
----------------------------------------------------------------------------------------------------------------------------------


