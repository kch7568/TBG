package kch_server;

import java.io.*;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONObject;
import kch_DAO.DAO_User;
import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/UpdatePost")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class UpdatePostServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "C:/uploads/";

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String updatedImageUrl = null;
        String updatedVideoUrl = null;
        String fileName = null;
        String fileExtension = null;
        String fileCapacity = null;

        try {
            request.setCharacterEncoding("UTF-8");

            // 파라미터 가져오기
            String sessionId = request.getParameter("sessionId");
            String postNumStr = request.getParameter("postNum");
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String category = request.getParameter("category");
            String existingImageUrl = request.getParameter("existingImageUrl");
            String existingVideoUrl = request.getParameter("existingVideoUrl");

            // 필수 파라미터 검증
            if (sessionId == null || postNumStr == null || title == null || content == null || category == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(createErrorResponse("Missing required parameters."));
                return;
            }

            int postNum = Integer.parseInt(postNumStr);

            // 세션 검증
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);
            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(createErrorResponse("Invalid session ID."));
                return;
            }

            String userId = session.getUserId();
            DAO_User userDao = new DAO_User();

            // 게시글 업데이트
            boolean updateSuccess = userDao.updatePost(postNum, userId, title, content, category);

            // 첨부파일 처리
            boolean mediaUpdated = false;
            for (Part part : request.getParts()) {
                if (part.getName().equals("media") && part.getSize() > 0) {
                    String mimeType = part.getContentType();
                    fileExtension = getFileExtension(mimeType);

                    if (fileExtension == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(createErrorResponse("Invalid file type."));
                        return;
                    }

                    // 파일명 및 경로 생성
                    fileName = userId + "_post_" + UUID.randomUUID().toString() + "." + fileExtension;
                    String savePath = UPLOAD_DIR + fileName;

                    // 디렉토리 생성
                    File uploadDir = new File(UPLOAD_DIR);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    // 파일 저장
                    try (InputStream inputStream = part.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(savePath)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }

                    // 파일 크기 설정
                    long fileSizeInKB = part.getSize() / 1024;
                    fileCapacity = fileSizeInKB + "KB";

                    // File_Path를 URL 경로로 변환
                    String fileUrl = "http://localhost:8888/kch_server/images/" + fileName;

                    // URL 구분 저장
                    if (mimeType.startsWith("image")) {
                        updatedImageUrl = fileUrl;
                        updatedVideoUrl = null; // 이미지 업로드 시 비디오 초기화
                    } else if (mimeType.startsWith("video")) {
                        updatedVideoUrl = fileUrl;
                        updatedImageUrl = null; // 비디오 업로드 시 이미지 초기화
                    }

                    mediaUpdated = true;
                }
            }

            // 미디어가 업데이트되지 않은 경우 기존 URL 유지
            if (!mediaUpdated) {
                updatedImageUrl = existingImageUrl;
                updatedVideoUrl = existingVideoUrl;
            }

            // DAO를 통해 파일 URL 업데이트
            if (updatedImageUrl != null) {
                userDao.updateFile(postNum, updatedImageUrl, fileName, fileExtension, fileCapacity);
            } else if (updatedVideoUrl != null) {
                userDao.updateFile(postNum, updatedVideoUrl, fileName, fileExtension, fileCapacity);
            }

            if (updateSuccess) {
                out.print(createSuccessResponse("Post updated successfully.", updatedImageUrl, updatedVideoUrl));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(createErrorResponse("Failed to update post."));
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(createErrorResponse("Invalid post number format."));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(createErrorResponse("Error occurred while updating the post."));
        } finally {
            out.close();
        }
    }

    private String getFileExtension(String mimeType) {
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

    private String createErrorResponse(String message) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "fail");
        jsonResponse.put("message", message);
        return jsonResponse.toString();
    }

    private String createSuccessResponse(String message, String imageUrl, String videoUrl) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "success");
        jsonResponse.put("message", message);
        jsonResponse.put("postImageUrl", imageUrl != null ? imageUrl : "");
        jsonResponse.put("postVideoUrl", videoUrl != null ? videoUrl : "");
        return jsonResponse.toString();
    }
}
