package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import DAO.DocDAO;
import DAO.FolderDAO;
import beans.Document;
import utils.ConnectionHandler;

@MultipartConfig
public class GetDocsList extends HttpServlet {
	private static final long serialVersionUID = -3623090834104364969L;
	private Connection connection;
	private FolderDAO fDAO;
	private DocDAO dDAO;
	
	public GetDocsList() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		ServletContext context = getServletContext();
		this.connection = ConnectionHandler.getConnection(context);
		fDAO = new FolderDAO(connection);
		dDAO = new DocDAO(connection);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer foldID = null;
		ArrayList<Document> docsList = null;
		//CONTROLLO PARAMETRI
		try {
			foldID = Integer.parseInt(req.getParameter("dir"));
			if (!fDAO.existSubDir(foldID)) {
				throw new NumberFormatException();
			}
			
			//ESTRAZIONE DEI DOCUMENTI NELLA CARTELLA
			docsList = dDAO.getDocumentsInto(foldID);
		} catch (NumberFormatException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
			resp.getWriter().println("The request is invalid!!!");
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //Error 500
			resp.getWriter().println("There are some problem in the server. Retry later.");
			return;
		}
		
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
		String json = gson.toJson(docsList);
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
