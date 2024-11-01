package kch_server;

import java.io.*;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import kch_DAO.DAO_User;
import kch_java.Session;
import kch_java.SessionManager;
import org.json.JSONObject;

@WebServlet("/UploadProfileImage")
public class UploadProfileImageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        System.out.println("Start processing image upload request");

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            System.out.println("Received request body: " + sb.toString());
            JSONObject jsonRequest = new JSONObject(sb.toString());
            String sessionId = jsonRequest.getString("sessionId");
            String encodedImage = jsonRequest.getString("image");

            // 세션 및 userId 검증
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);

            if (session == null) {
                System.out.println("Invalid session ID: " + sessionId);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Invalid session ID.");
                out.print(jsonResponse.toString());
                return;
            }

            String userId = session.getUserId();
            System.out.println("Valid session. User ID: " + userId);

            // 이미지 파일 저장 경로 생성 및 검증
            File directory = new File("C:/uploads/");
            if (!directory.exists()) {
                boolean dirCreated = directory.mkdirs();
                System.out.println("Directory created: " + dirCreated);
            }

            // 이미지 디코딩 및 저장
            byte[] imageBytes = Base64.getDecoder().decode(encodedImage);
            String imagePath = "C:/uploads/" + userId + "_profile.jpg";
            int postSize = imageBytes.length;
            String postExtension = "jpg";
            System.out.println("Decoded image size: " + postSize);

            try (FileOutputStream fos = new FileOutputStream(imagePath)) {
                fos.write(imageBytes);
                System.out.println("Image written to path: " + imagePath);
            } catch (IOException e) {
                System.out.println("Error saving image to path: " + e.getMessage());
                throw e;
            }

            String imageUrl = "http://localhost:8888/kch_server/images/" + userId + "_profile.jpg";
            System.out.println("Image URL for database: " + imageUrl);

            // 데이터베이스에 URL 업데이트
            DAO_User userDao = new DAO_User();
            boolean updateSuccess = false;
            try {
                System.out.println("Attempting to upsert profile image");
                updateSuccess = userDao.upsertProfileImage(userId, postSize, postExtension, imageUrl);
                System.out.println("Database update successful: " + updateSuccess);
            } catch (Exception e) {
                System.out.println("Error updating database: " + e.getMessage());
            } finally {
                userDao.close();
                System.out.println("Database connection closed.");
            }

            // 응답 생성
            JSONObject jsonResponse = new JSONObject();
            if (updateSuccess) {
                jsonResponse.put("status", "success");
                jsonResponse.put("imageUrl", imageUrl);
                System.out.println("Response: success");
            } else {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Failed to update image URL in the database.");
                System.out.println("Response: failed to update database");
            }
            out.print(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing request: " + e.getMessage());
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "fail");
            jsonResponse.put("message", "Server encountered an error. Please try again later.");
            out.print(jsonResponse.toString());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not supported for this endpoint.");
    }
}
