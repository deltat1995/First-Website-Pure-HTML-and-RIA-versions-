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

import DAO.DocDAO;
import DAO.FolderDAO;
import utils.ConnectionHandler;


@MultipartConfig
public class CreateDocument extends HttpServlet {
	private static final long serialVersionUID = -8752151444322597009L;
	private Connection connection;
	private FolderDAO fDAO;
	private DocDAO dDAO;
	
	public CreateDocument() {
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
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		String[] type = {"PDF", "Word Document", "TXT File", "Other"};
		Integer dirID = null;
		String name = null;
		String dtype = null;
		String summary = null;
		//CONTROLLO PARAMETRI
		try {
			dirID = Integer.parseInt(req.getParameter("dir"));
			if (!fDAO.existSubDir(dirID)) {
				throw new Exception();
			}
			name = StringEscapeUtils.escapeHtml(req.getParameter("dname"));
			if (name == null) throw new Exception();
			if (name.isBlank() || name.length()>30) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
				resp.getWriter().println("Insert a document name not empty and that contain at most 30 character!!!");
				return;
			}
			Integer indexType = Integer.parseInt(req.getParameter("dtype"));
			if(indexType<0 || indexType>3) throw new Exception();
			dtype = type[indexType];
			summary = StringEscapeUtils.escapeHtml(req.getParameter("dsum"));
			if (summary == null) summary = "";
		}catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //Error 500
			resp.getWriter().println("There are some problem in the server. Retry later.");
			return;
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); //Error 400
			resp.getWriter().println("The request is invalid!!!");
			return;
		}	
		//AGGIUNGO IL DOCUMENTO SUL DB
		try {
			dDAO.addDoc(name, dtype, summary, dirID);
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
	public void destroy(){
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
