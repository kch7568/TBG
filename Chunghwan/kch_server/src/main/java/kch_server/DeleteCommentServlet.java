package kch_server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import kch_DAO.DAO_User;

@WebServlet("/DeleteComment")
public class DeleteCommentServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String commentId = request.getParameter("commentId");
        if (commentId == null || commentId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"fail\", \"message\":\"Missing commentId\"}");
            return;
        }

        DAO_User daoUser;
        try {
            daoUser = new DAO_User();
            boolean isDeleted = daoUser.deleteComment(commentId);

            if (isDeleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\", \"message\":\"Comment deleted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"status\":\"fail\", \"message\":\"Failed to delete comment\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"fail\", \"message\":\"An error occurred while deleting comment\"}");
        } finally {
            out.close();
        }
    }
}
