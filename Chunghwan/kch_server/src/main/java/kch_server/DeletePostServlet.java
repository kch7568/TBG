package kch_server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kch_DAO.DAO_User;
import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/DeletePost")
public class DeletePostServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 파라미터 검증
            String sessionId = request.getParameter("sessionId");
            String postNumStr = request.getParameter("postNum");

            if (isNullOrEmpty(sessionId) || isNullOrEmpty(postNumStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"message\":\"필수 파라미터가 누락되었습니다.\"}");
                return;
            }
            
            // 게시글 번호 변환
            int postNum;
            try {
                postNum = Integer.parseInt(postNumStr);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"message\":\"유효하지 않은 게시글 번호입니다.\"}");
                return;
            }

            // 세션 유효성 확인
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);
            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false, \"message\":\"세션이 유효하지 않습니다.\"}");
                return;
            }

            String userId = session.getUserId();
            if (isNullOrEmpty(userId)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false, \"message\":\"유효하지 않은 사용자입니다.\"}");
                return;
            }
            

            // 게시글 삭제
            DAO_User daoUser = null;
            try {
                daoUser = new DAO_User();
                boolean deleteSuccess = daoUser.deletePost(postNum, userId);

                if (deleteSuccess) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"success\":true, \"message\":\"게시글이 성공적으로 삭제되었습니다.\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"success\":false, \"message\":\"게시글을 찾을 수 없거나 삭제 권한이 없습니다.\"}");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\":false, \"message\":\"게시글 삭제 중 서버 오류가 발생했습니다.\"}");
                e.printStackTrace();
            } finally {
                if (daoUser != null) {
                    daoUser.close(); // 명시적으로 DAO 객체 닫기
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false, \"message\":\"서버에서 예외가 발생했습니다.\"}");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    /**
     * Null 또는 빈 문자열 확인 메서드
     * 
     * @param value 확인할 문자열
     * @return null 또는 빈 문자열일 경우 true
     */
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
