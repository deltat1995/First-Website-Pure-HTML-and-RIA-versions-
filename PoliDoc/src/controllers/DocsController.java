package controllers;

import java.io.IOException;

import java.sql.*;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import DAO.DocDAO;
import DAO.FolderDAO;
import beans.Document;
import beans.Folder;
import utils.SharedPropertyMessageResolver;

public class DocsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private FolderDAO fDAO;
	private DocDAO dDAO;
	
	public DocsController() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		//TEMPLATE ENGINE
				ServletContext context = getServletContext();
				ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
				templateResolver.setTemplateMode(TemplateMode.HTML);
				templateResolver.setSuffix(".html");
				this.templateEngine = new TemplateEngine();
				this.templateEngine.setTemplateResolver(templateResolver);
				this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(context, "i18n", "messages"));
		        
		//DB CONNECTION
		try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
			fDAO = new FolderDAO(connection);
			dDAO = new DocDAO(connection);
			
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		
		String servletpath = req.getServletPath();
		if(servletpath.equals("/getListDocs")) {  		//GESTISCO LA RICHIESTA DELLA LISTA DI DOCUMENTI
			try {
				docsReqHandler(req,resp);
			} catch (Exception e) {
				resp.getWriter().print(e.getMessage());
				resp.getWriter().flush();
				return;
			}
		}
		else { 											//GESTISCO LA RICHIESTA DI INFORMAZIONI DI UN DOCUMENTO (/getDocInfo)
			try {
				docInfoHandler(req,resp);
			} catch (Exception e) {
				resp.getWriter().print(e.getMessage());
				resp.getWriter().flush();
				return;
			}
		}
	}
	
	private void docInfoHandler(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer docID = null;
		
		//CONTROLLO PARAMETRI
		try {
			docID = Integer.parseInt(req.getParameter("doc"));
		} catch (NumberFormatException e) {
			throw new Exception("Error!!! The inserted URL is wrong!!");
		}
		
		//ESTRAZIONE DELLE INFORMAZIONI DEL DOCUMENTO
		Document doc = dDAO.getDocument(docID);
		if (doc == null) {
			throw new Exception("The document doesn't exist!!!");
		}
		
		//INSERISCO I PARAMETRI NELLA REQUEST
		req.setAttribute("doc", doc);
		
		String path = "/docInfo.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		templateEngine.process(path, ctx, resp.getWriter());
		
	}

	private void docsReqHandler(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer foldID = null;
		
		//CONTROLLO PARAMETRI
		try {
			foldID = Integer.parseInt(req.getParameter("dir"));
			if (!fDAO.existSubDir(foldID)) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			throw new Exception("Error!!! The inserted URL is wrong!!");
		}
		
		//ESTRAZIONE DEI DOCUMENTI NELLA CARTELLA
		ArrayList<Document> docs = dDAO.getDocumentsInto(foldID);
		Folder folder = fDAO.getFolder(foldID);
		
		//INSERISCO I PARAMETRI NELLA REQUEST
		req.setAttribute("docs", docs);
		req.setAttribute("folder", folder);
		
		String path = "/documenti.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		templateEngine.process(path, ctx, resp.getWriter());
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		try {
			doGet(req, resp);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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