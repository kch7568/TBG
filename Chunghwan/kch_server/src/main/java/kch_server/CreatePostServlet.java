package kch_server;

import java.io.*;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import kch_DAO.DAO_User;
import kch_java.Session;
import kch_java.SessionManager;
import org.json.JSONObject;

@WebServlet("/CreatePost")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class CreatePostServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "C:/uploads/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            request.setCharacterEncoding("UTF-8");
            String sessionId = request.getParameter("sessionId");
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String category = request.getParameter("category");

            if (sessionId == null || title == null || content == null || category == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Missing required parameters.");
                out.print(jsonResponse.toString());
                return;
            }

            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);
            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Invalid session ID.");
                out.print(jsonResponse.toString());
                return;
            }

            String userId = session.getUserId();

            DAO_User userDao = new DAO_User();
            int postNum = userDao.savePost(userId, title, content, category);

            if (postNum == -1) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Failed to create post.");
                out.print(jsonResponse.toString());
                return;
            }

            for (Part part : request.getParts()) {
                if (part.getName().equals("media") && part.getSize() > 0) {
                    String mimeType = part.getContentType();
                    String fileExtension = determineFileExtension(mimeType);

                    if (fileExtension == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("status", "fail");
                        jsonResponse.put("message", "Invalid file type.");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    String fileName = userId + "_post_" + UUID.randomUUID().toString() + "." + fileExtension;
                    String savePath = UPLOAD_DIR + fileName;
                    
                    
                    File uploadDir = new File(UPLOAD_DIR);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    try (InputStream inputStream = part.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(savePath)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("status", "fail");
                        jsonResponse.put("message", "Error occurred while saving the media file.");
                        out.print(jsonResponse.toString());
                        return;
                    }

                    // 파일 경로를 올바르게 설정하여 중복 제거
                    String filePath = "http://localhost:8888/kch_server/images/" + fileName;
                    userDao.saveFile(postNum, filePath, fileName, fileExtension, String.valueOf(part.getSize() / 1024) + "KB");

                }
            }

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Post created successfully.");
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "fail");
            jsonResponse.put("message", "Error occurred while creating the post.");
            out.print(jsonResponse.toString());
        } finally {
            out.close();
        }
    }

    private String determineFileExtension(String mimeType) {
        switch (mimeType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "video/mp4":
                return "mp4";
            default:
                return null;
        }
    }
}
