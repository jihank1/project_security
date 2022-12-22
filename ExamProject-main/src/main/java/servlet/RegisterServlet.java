package servlet;

import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Util;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static Connection conn;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }
    
    public void init() throws ServletException {
    	conn = Util.initDbConnection();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		// The replacement escapes apostrophe special character in order to store it in SQL
		String name = request.getParameter("name").replace("'", "''");
		String surname = request.getParameter("surname").replace("'", "''");
		String email = request.getParameter("email").replace("'", "''");
		String pwd = request.getParameter("password").replace("'", "''");
		
		try (PreparedStatement mystmt = conn.prepareStatement("SELECT * FROM user WHERE email = ?")) {
			mystmt.setString(1, email);
			ResultSet sqlRes = mystmt.executeQuery();
			
			
			if (sqlRes.next()) {
				System.out.println("Email already registered!");
				request.getRequestDispatcher("register.html").forward(request, response);
				
			} else {
				String sql = "insert into user values (?, ?, ?, ?)";
					PreparedStatement prepStat = conn.prepareStatement(sql);
					prepStat.setString(1, name);
					prepStat.setString(2, surname);
					prepStat.setString(3, email);
					prepStat.setString(4, pwd);
					prepStat.executeUpdate();
				
				request.setAttribute("email", email);
				request.setAttribute("password", pwd);
				
				System.out.println("Registration succeeded!");
				request.getRequestDispatcher("home.jsp").forward(request, response);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			request.getRequestDispatcher("register.html").forward(request, response);
		}
	}

}
