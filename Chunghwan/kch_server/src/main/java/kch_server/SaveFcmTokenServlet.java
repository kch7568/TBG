package kch_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import kch_DAO.DAO_User;
import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/SaveFcmToken")
public class SaveFcmTokenServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    response.setContentType("application/json; charset=UTF-8");
	    PrintWriter out = response.getWriter();
	    JSONObject jsonResponse = new JSONObject();

	    try {
	        // 요청 데이터 읽기
	        StringBuilder sb = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }
	        JSONObject requestData = new JSONObject(sb.toString());

	        String sessionId = requestData.optString("sessionId", null);
	        String fcmToken = requestData.optString("fcmToken", null);

	        System.out.println("sessionId: " + sessionId);
	        System.out.println("fcmToken: " + fcmToken);

	        if (sessionId == null || fcmToken == null) {
	            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            jsonResponse.put("success", false);
	            jsonResponse.put("message", "Invalid sessionId or fcmToken.");
	        } else {
	            SessionManager sessionManager = SessionManager.getInstance();
	            Session userSession = sessionManager.getSession(sessionId);

	            if (userSession == null) {
	                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                jsonResponse.put("success", false);
	                jsonResponse.put("message", "Invalid session.");
	            } else {
	                String userId = userSession.getUserId();
	                DAO_User daoUser = new DAO_User();

	                if (daoUser.saveFcmToken(userId, fcmToken)) {
	                    jsonResponse.put("success", true);
	                    jsonResponse.put("message", "FCM 토큰 저장 성공.");
	                } else {
	                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	                    jsonResponse.put("success", false);
	                    jsonResponse.put("message", "FCM 토큰 저장 실패.");
	                }
	            }
	        }
	    } catch (Exception e) {
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        jsonResponse.put("success", false);
	        jsonResponse.put("message", "오류 발생: " + e.getMessage());
	        e.printStackTrace();
	    }

	    out.print(jsonResponse.toString());
	    out.close();
	}

}
