package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringEscapeUtils;
import DAO.UserDAO;

@MultipartConfig
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = -3787764578086958391L;
	private Connection connection;
	private UserDAO userDAO;
	
	public LoginController() {
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
		HttpSession session = req.getSession(true);
		//ESTRAGGO CARTELLE E SOTTOCARTELLE DAL DB
    	if(!session.isNew() && session.getAttribute("username") != null) {
    		String path = getServletContext().getContextPath() +  "/homepage.html";
			resp.sendRedirect(path);
    	}
    	else {
    		String path = "/login.html";
    		RequestDispatcher view = req.getRequestDispatcher(path);
            view.forward(req, resp);
    	}
	}
	
	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		resp.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		HttpSession session = req.getSession(true);
		if (!session.isNew() && session.getAttribute("username") != null) {
			String homePath = req.getServletContext().getContextPath()+ "/homepage.html";
			resp.sendRedirect(homePath);
			return;
		}
		String username = StringEscapeUtils.escapeJava(req.getParameter("username"));
		String password = StringEscapeUtils.escapeJava(req.getParameter("password"));
    	if(username==null || password==null || username.isBlank() || password.isEmpty()) {
    		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
			resp.getWriter().println("Credentials must be not null");
			return;
    	}
		
		boolean logged = false;
		
		try {
			logged = userDAO.checkLogin(username, password);
		} catch (SQLException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //Error 500
			resp.getWriter().println("There are some problem in the server. Retry later.");
			return;
		}
		if (logged) {
		    session.setMaxInactiveInterval(600);
		    session.setAttribute("username",username);
		    resp.setStatus(HttpServletResponse.SC_OK); //Succeed 200
			return;
		}
		else {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //Error 401
			resp.getWriter().println("The credentials are wrong!!");
			return;
		}
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
