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

import org.apache.commons.lang.StringEscapeUtils;

import DAO.FolderDAO;
import utils.ConnectionHandler;

@MultipartConfig
public class CreateDirectory extends HttpServlet{
	private static final long serialVersionUID = -1985840593747513603L;
	private Connection connection;
	private FolderDAO fDAO;
	
	public CreateDirectory() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		ServletContext context = getServletContext();
		this.connection = ConnectionHandler.getConnection(context);
		fDAO = new FolderDAO(connection);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		sendInvalidReq(resp);
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		String servletPath = req.getServletPath();
		//GESTISCO AGGIUNTA DI CARTELLE E SOTTOCARTELLE
			switch (servletPath) {
				case "/AddDir": {
					//CONTROLLO PARAMETRI
					String name = StringEscapeUtils.escapeHtml(req.getParameter("dirName"));
					if (name == null || name.isBlank()) {
						resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
						resp.getWriter().println("The name of directory haven't to be empty!!!");
						return;
					}
					//AGGIUNTA CARTELLA
					try {
						fDAO.addParentFolder(name);
					} catch (SQLException e) {
						e.printStackTrace();
						resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //Error 500
						resp.getWriter().println("There are some problem in the server. Retry later.");
						return;
					}
					resp.setStatus(HttpServletResponse.SC_OK); //Succeed 200
					return;
				}
				case "/AddSubDir": {
					Integer dirID = null;
					//CONTROLLO PARAMETRI
					try {
						dirID = Integer.parseInt(req.getParameter("dir"));
						if (!fDAO.existParentDir(dirID)) {
							throw new NumberFormatException();
						}
					} catch (SQLException e) {
						e.printStackTrace();
						resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //Error 500
						resp.getWriter().println("There are some problem in the server. Retry later.");
						return;
					}catch (NumberFormatException e) {
						sendInvalidReq(resp);
						return;
					}
					
					String name = StringEscapeUtils.escapeHtml(req.getParameter("subDirName"));
					if (name == null || name.isBlank()) {
						resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
						resp.getWriter().println("The name of subdirectory haven't to be empty!!!");
						return;
					}
					//AGGIUNTA SOTTOCARTELLA
					try {
						fDAO.addChildFolder(name, dirID);
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
					sendInvalidReq(resp);
					return;
			}	
	}
	
	private void sendInvalidReq(HttpServletResponse resp) throws IOException {
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
		resp.getWriter().println("The request is invalid!!!");
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
