package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import DAO.DocDAO;
import beans.Document;
import utils.ConnectionHandler;

@MultipartConfig
public class GetDocInfo extends HttpServlet {
	private static final long serialVersionUID = -353457932223416935L;
	private Connection connection;
	private DocDAO dDAO;
	
	public GetDocInfo() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		ServletContext context = getServletContext();
		this.connection = ConnectionHandler.getConnection(context);
		dDAO = new DocDAO(connection);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer docID = null;
		
		//CONTROLLO PARAMETRI
		try {
			docID = Integer.parseInt(req.getParameter("doc"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
			resp.getWriter().println("The request is invalid!!!");
			return;
		}
		
		//ESTRAZIONE DELLE INFORMAZIONI DEL DOCUMENTO
		Document doc = null;
		
		try {
			doc = dDAO.getDocument(docID);
		} catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //Error 500
			resp.getWriter().println("There are some problem in the server. Retry later.");
			return;
		}
		if (doc == null) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
			resp.getWriter().println("The requested document doesn't exist!!!");
			return;
		}
		
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
		String json = gson.toJson(doc);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
		resp.getWriter().println("The request is invalid!!!");
		return;
	}
	
	
	@Override
	public void destroy(){
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
