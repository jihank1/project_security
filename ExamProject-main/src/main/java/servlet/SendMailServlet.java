package servlet;

import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Util;

/**
 * Servlet implementation class SendMailServlet
 */
@WebServlet("/SendMailServlet")
public class SendMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendMailServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
    	conn = Util.initDbConnection();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		String sender = request.getParameter("email").replace("'", "''");
		String receiver = request.getParameter("receiver").replace("'", "''");
		String subject = request.getParameter("subject").replace("'", "''");
		String body = request.getParameter("body").replace("'", "''");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String timestamp = format.format(new Date(System.currentTimeMillis()));
		
		try (PreparedStatement st = conn.prepareStatement("INSERT INTO mail VALUES (?, ?, ?, ?, ?")) {
			st.setString(1, sender);
			st.setString(2, receiver);
			st.setString(3, subject);
			st.setString(4, body);
			st.setString(5, timestamp);
			st.executeUpdate();
			/*st.execute(
				"INSERT INTO mail ( sender, receiver, subject, body, time ) "
				+ "VALUES ( '" + sender + "', '" + receiver + "', '" + subject + "', '" + body + "', '" + timestamp + "' )"
			);*/
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("email", sender);
		request.getRequestDispatcher("home.jsp").forward(request, response);
	}

}
