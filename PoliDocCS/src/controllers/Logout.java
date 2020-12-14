package controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@MultipartConfig
public class Logout extends HttpServlet{
	private static final long serialVersionUID = -3143851443059864955L;
	
	public Logout() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		String path = getServletContext().getContextPath();
		resp.sendRedirect(path);
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
		resp.getWriter().println("The request is invalid!!!");
		return;
	}
	
}
