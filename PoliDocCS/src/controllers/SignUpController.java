package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;


import DAO.UserDAO;
import beans.User;

@MultipartConfig
public class SignUpController extends HttpServlet {
	
	private static final long serialVersionUID = 1658991089638502523L;
	private Connection connection;
	private UserDAO userDAO;
	
	public SignUpController() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		ServletContext context = getServletContext();
		
		//DB CONNECTION
		try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
			userDAO = new UserDAO(connection);
			
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
		resp.getWriter().println("The request is invalid!!!");
		return;
	}
	
	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		resp.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		String username = StringEscapeUtils.escapeJava(req.getParameter("username"));
		String email = StringEscapeUtils.escapeJava(req.getParameter("email"));
		String password = StringEscapeUtils.escapeJava(req.getParameter("password"));
		
    	if(username==null || password==null || email==null || username.isBlank() || password.isEmpty() || email.isBlank() || !validateEmail(email)) {
    		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
			resp.getWriter().println("Error!! Some required data are empty.");
			return;
    	}
		
		try {
			boolean isUnique = userDAO.isUnique(username);
			
			if (isUnique) {
				User us = new User();
				us.setUsername(username);
				us.setEmail(email);
				us.setPassword(password);
				userDAO.signup(us);
			    resp.setStatus(HttpServletResponse.SC_CREATED); //Succeed 201
				resp.getWriter().println("User correctly registered!!");
				return;
			}
			else {
				resp.setStatus(HttpServletResponse.SC_CONFLICT); //Error 409
				resp.getWriter().println("The username already exist!!");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //Error 500
			resp.getWriter().println("There are some problem in the server. Retry later.");
			return;
		}
	}
	
    //https://stackoverflow.com/questions/46155/how-to-validate-an-email-address-in-javascript
    private boolean validateEmail(String email) {
    	final String re = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    	return Pattern.matches(re, email.toLowerCase());
    }
	
	
	@Override
	public void destroy(){
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}

