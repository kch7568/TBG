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
    
    public boolean checkNickName(String nickname) {
        return daoUser.isNickNameExists(nickname);  // 이미 불린값을 반환하므로 바로 반환
    }

    // 회원가입 처리
    public boolean register(User user) {
    	if (checkID(user.getID()) 
    		    && user.getID().length() >= 5 
    		    && user.getID().length() <= 20 
    		    && user.getPW().length() >= 5 
    		    && user.getPW().length() <= 16
    		    && user.getNickName().length() >= 3
    		    && user.getNickName().length() <=16
    		    && user.getID().matches("[a-zA-Z0-9]+" )  //a~z, A~Z, 0~9만 포함
    		    && user.getPW().matches("[a-zA-Z0-9!@#$]+")
    		    && user.getPW().equals(user.getPW_re())// 비밀번호와 비밀번호 확인란이 일치한가?
    		    && user.getNickName().matches("^[a-zA-Z0-9가-힣]+$")//닉네임이 특수문자x, 결합된 한글인가?
    			&& checkNickName(user.getNickName())) { 
    		

            return daoUser.addUser(user);  // DAO를 통해 사용자를 데이터베이스에 추가
        } else {
        	System.out.println("입력한 닉네임: " + user.getNickName());
            return false;  // ID 중복 및 유효성 검사 다를 시 회원가입 불가
        }
    }
}
