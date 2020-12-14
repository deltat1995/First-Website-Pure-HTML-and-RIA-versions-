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


import DAO.DocDAO;
import DAO.FolderDAO;
import utils.ConnectionHandler;

@MultipartConfig
public class MoveDocument extends HttpServlet{
	private static final long serialVersionUID = -2215946844465258623L;
	private Connection connection;
	private FolderDAO fDAO;
	private DocDAO dDAO;
	
	public MoveDocument() {
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
		Integer docID = null;
		Integer intoDir = null;
		
		try {
			//CONTROLLO PARAMETRI
			docID = Integer.parseInt(req.getParameter("doc"));
			intoDir = Integer.parseInt(req.getParameter("dir"));
			if (!fDAO.existSubDir(intoDir)) {
				throw new NumberFormatException();
			}
			if (dDAO.getDocument(docID) == null) {
				throw new NumberFormatException();
			}
			
			//SPOSTO IL DOCUMENTO
			dDAO.moveDoc(docID,intoDir);
			
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
		
		resp.setStatus(HttpServletResponse.SC_OK); //Succeed 200
		return;
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
