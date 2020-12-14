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
public class TrashController extends HttpServlet{
	private static final long serialVersionUID = 6959539736187556718L;
	private Connection connection;
	private FolderDAO fDAO;
	private DocDAO dDAO;
	
	public TrashController() {
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
		String servletPath = req.getServletPath();
		//GESTISCO LA RIMOZIONE DI CARTELLE E SOTTOCARTELLE
		switch (servletPath) {
			case "/DelDir": {
				Integer dirID = null;
				//CONTROLLO PARAMETRI
				try {
					dirID = Integer.parseInt(req.getParameter("dir"));
					if(!fDAO.existFolder(dirID)) {
						throw new NumberFormatException();
					}
					fDAO.deleteFolder(dirID);
				} catch (NumberFormatException e) {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
					resp.getWriter().println("The request is invalid!!!");
					return;
				}catch (SQLException e) {
					e.printStackTrace();
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //Error 500
					resp.getWriter().println("There are some problem in the server. Retry later.");
					return;
				}
				
				resp.setStatus(HttpServletResponse.SC_OK); //Succeed 200
				return;
			}
			case "/DelDoc": {
				Integer docID = null;
				try {
					docID = Integer.parseInt(req.getParameter("doc"));
					if (dDAO.getDocument(docID) == null) {
						throw new NumberFormatException();
					}
					dDAO.deleteDoc(docID);
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
			default:
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
				resp.getWriter().println("The request is invalid!!!");
				return;
		}	
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
