package kch_java;

import java.sql.SQLException;

import kch_DAO.DAO_User; 

public class Registration {
    private DAO_User daoUser;

    public Registration() throws ClassNotFoundException, SQLException {
        this.daoUser = new DAO_User();
    }

    // ID 중복 확인
    public boolean checkID(String userId) {
        User user = daoUser.getUserById(userId);
        return user == null;  // null이면 사용 가능한 ID
    }

    // 회원가입 처리
    public boolean register(User user) {
        if (checkID(user.getID())) {
            return daoUser.addUser(user);  // DAO를 통해 사용자를 데이터베이스에 추가
        } else {
            return false;  // ID 중복 시 회원가입 불가
        }
    }
}
