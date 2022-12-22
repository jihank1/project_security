package servlet;

import jakarta.servlet.http.HttpServlet;
import java.security.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.owasp.encoder.Encode;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Util;

/**
 * Servlet implementation class HelloWorldServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static Connection conn;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }
    
    public void init() throws ServletException {
    	conn = Util.initDbConnection();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		//Encode.forHtml("prova");  va fatto solo nel search inbox e send
		String email = request.getParameter("email");
		String pwd = request.getParameter("password");
		String gRecaptchaResponse = request
				.getParameter("g-recaptcha-response");
		System.out.println(gRecaptchaResponse);
		boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
		
		try (PreparedStatement mystmt = conn.prepareStatement("SELECT * FROM user WHERE email = ? AND password = ?")){

			mystmt.setString(1, email);
			mystmt.setString(2, pwd);
			ResultSet sqlRes = mystmt.executeQuery();
			
			if (sqlRes.next() && verify) {
				request.setAttribute("email", sqlRes.getString(3));
				request.setAttribute("password", sqlRes.getString(4));
				
				
				
				System.out.println("Login succeeded!");
				request.setAttribute("content", "");
				request.getRequestDispatcher("home.jsp").forward(request, response);
				
				
			} 
			else {
				System.out.println("Login failed!");
				request.getRequestDispatcher("login.html").forward(request, response);
			}
			
		}
	catch (SQLException e) {
			e.printStackTrace();
			request.getRequestDispatcher("login.html").forward(request, response);
		}
	}
	
}
