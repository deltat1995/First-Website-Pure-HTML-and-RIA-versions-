package controllers;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;


import javax.servlet.*;
import javax.servlet.http.*;

import DAO.*;
import beans.*;
import utils.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public class AddAndDeleteController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private FolderDAO fDAO;
	private DocDAO dDAO;
	
	public AddAndDeleteController() {
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
		
		String path = "/addDelete.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
		
		String servletPath = req.getServletPath();
		String pathInfo = req.getPathInfo();
		
		if(servletPath.equals("/Dir")) {										//GESTISCO LE CARTELLE
			switch (pathInfo) {
			
			case "/AddPar": {								//AGGIUNGERE CARTELLA
				//CONTROLLO PARAMETRI
				req.setAttribute("page", "addDir");
				templateEngine.process(path, ctx, resp.getWriter());
				return;
			}
			
			case "/AddSub": {								//AGGIUNGERE SOTTOCARTELLA
				//CONTROLLO PARAMETRI
				try {
					Integer.parseInt(req.getParameter("dir"));
					req.setAttribute("dir",req.getParameter("dir"));
				//NB: non verifico se esiste la cartella nel DB, poiché mi basta fare solo il controllo nel doPost per non avere danni
				} catch (NumberFormatException e) {
					sendError(resp, "Error!!! The inserted URL is wrong!!");
					return;
				}
				req.setAttribute("page", "addSubDir");
				templateEngine.process(path, ctx, resp.getWriter());
				return;
			}
			
			case "/DelPar": {								//CANCELLARE CARTELLA
				ArrayList<Document> docs = null;
				Integer dirID = null;
				//CONTROLLO PARAMETRI
				try {
					dirID = Integer.parseInt(req.getParameter("dir"));
					if(!fDAO.existParentDir(dirID)) {
						sendError(resp, "Error!!! The directory is wrong or is a subdirectory!!");
						return;
					}
					docs = dDAO.getDocumentsWithParent(dirID);
				} catch (NumberFormatException e) {
					sendError(resp, "Error!!! The inserted URL is wrong!!");
					return;
				}
				if (docs.size() == 0) {
					fDAO.deleteFolder(dirID);
					break;
					//non ritorna e va alla homepage
				}else {
					req.setAttribute("dir", dirID);
					req.setAttribute("docs", docs);
					req.setAttribute("isPar", 1);
					req.setAttribute("page", "delDir");
					templateEngine.process(path, ctx, resp.getWriter());
					return;
				}
			}
			
			case "/DelSub": {								//CANCELLARE SOTTOCARTELLA
				ArrayList<Document> docs = null;
				Integer dirID = null;
				//CONTROLLO PARAMETRI
				try {
					dirID = Integer.parseInt(req.getParameter("dir"));
					if(!fDAO.existSubDir(dirID)) {
						sendError(resp, "Error!!! The directory is wrong or is not a subdirectory!!");
						return;
					}
					docs = dDAO.getDocumentsInto(dirID);
				} catch (NumberFormatException e) {
					sendError(resp, "Error!!! The inserted URL is wrong!!");
					return;
				}
				if (docs.size() == 0) {
					fDAO.deleteFolder(dirID);
					//non ritorna e va alla homepage
					break;
				}else {
					req.setAttribute("dir", dirID);
					req.setAttribute("docs", docs);
					req.setAttribute("isPar", 0);
					req.setAttribute("page", "delDir");
					templateEngine.process(path, ctx, resp.getWriter());
					return;
				}
			}
			
			case "/Del": {								//CONFERMA CANCELLAZIONE CARTELLA E SOTTOCARTELLA
				System.out.println("Conferma cancello sottocartella");
				Integer dirID = null;
				//CONTROLLO PARAMETRI
				try {
					dirID = Integer.parseInt(req.getParameter("dir"));
					Integer isParent = Integer.parseInt(req.getParameter("isPar"));
					if(isParent==0 && !fDAO.existSubDir(dirID)) {
						sendError(resp, "Error!!! The directory is wrong or is not a subdirectory!!");
						return;
					}
					if(isParent==1 && !fDAO.existParentDir(dirID)) {
						sendError(resp, "Error!!! The directory is wrong or is a subdirectory!!");
						return;
					}
				} catch (NumberFormatException e) {
					sendError(resp, "Error!!! The inserted URL is wrong!!");
					return;
				}
				fDAO.deleteFolder(dirID);
				//non ritorna e va alla homepage
				break;
			}
			
			default:
				sendError(resp,"Error!!! The inserted URL is wrong!!");
				return;
			}
			
		}else {																	//GESTISCO I DOCUMENTI
			switch (pathInfo) {
			
			case "/Add": {							//AGGIUNGO DOCUMENTO
				//CONTROLLO PARAMETRI
				try {
					Integer.parseInt(req.getParameter("dir"));
					req.setAttribute("dir",req.getParameter("dir"));
				//NB: non verifico se esiste la cartella nel DB, poiché mi basta fare solo il controllo nel doPost per non avere danni
				} catch (NumberFormatException e) {
					sendError(resp, "Error!!! The inserted URL is wrong!!");
					return;
				}
				req.setAttribute("page", "addDoc");
				templateEngine.process(path, ctx, resp.getWriter());
				return;
			}
			
			case "/Del": {							//CANCELLO DOCUMENTO
				Integer docID = null;
				try {
					docID = Integer.parseInt(req.getParameter("doc"));
					if (dDAO.getDocument(docID) == null) {
						sendError(resp, "The document doesn't exist!!!");
						return;
					}
					dDAO.deleteDoc(docID);
				} catch (NumberFormatException e) {
					sendError(resp, "Error!!! The inserted URL is wrong!!");
					return;
				}
				break;
			}
			
			default:
				sendError(resp,"Error!!! The inserted URL is wrong!!");
				return;
			}
		}
		
		String pathRedirect = "/PoliDoc/index";
		resp.sendRedirect(pathRedirect);
	}
	
	private void sendError(HttpServletResponse resp, String error) {
		try {
			resp.getWriter().print(error);
			resp.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		String servletPath = req.getServletPath();
		String pathInfo = req.getPathInfo();
		
		if(servletPath.equals("/Dir")) { 				//GESTISCO AGGIUNTA DI CARTELLE E SOTTOCARTELLE
			switch (pathInfo) {
			
			case "/AddPar": {
				//CONTROLLO PARAMETRI
				String name = StringEscapeUtils.escapeHtml(req.getParameter("dirName"));
				if (name == null || name.length()==0) {
					sendError(resp,"The directory name is invalid!!!");
					return;
				}
				fDAO.addParentFolder(name);
				String pathRedirect = "/PoliDoc/index";
				resp.sendRedirect(pathRedirect);
				return;
			}
			
			case "/AddSub": {
				Integer dirID = null;
				//CONTROLLO PARAMETRI
				try {
					dirID = Integer.parseInt(req.getParameter("dir"));
					if (!fDAO.existParentDir(dirID)) {
						sendError(resp,"The directory doesn't exist!!!");
						return;
					}
				} catch (NumberFormatException e) {
					sendError(resp,"Error!!! The inserted URL is wrong!!");
					return;
				}
				
				String name = StringEscapeUtils.escapeHtml(req.getParameter("subDirName"));
				if (name == null || name.length()==0) {
					sendError(resp,"The subdirectory name is invalid!!!");
					return;
				}
				
				fDAO.addChildFolder(name, dirID);
				String pathRedirect = "/PoliDoc/index";
				resp.sendRedirect(pathRedirect);
				return;
			}
			
			default:
				sendError(resp,"Error!!! The inserted URL is wrong!!");
				return;
			}	
		}else {											//GESTISCO L'AGGIUNTA DI UN DOCUMENTO
			switch (pathInfo) {
			
			case "/Add": {
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
					if (name.length()==0 || name.length()>30) {
						sendError(resp,"Insert a document name not empty and that contain at most 30 character!!!");
						return;
					}
					Integer indexType = Integer.parseInt(req.getParameter("dtype"));
					if(indexType<0 || indexType>3) throw new Exception();
					dtype = type[indexType];
					summary = StringEscapeUtils.escapeHtml(req.getParameter("dsum"));
					if (summary == null) summary = "";
				} catch (Exception e) {
					sendError(resp,"Error!!! The required operation is invalidated!! Don't modify the POST url!!!");
					return;
				}	
				//AGGIUNGO IL DOCUMENTO SUL DB
				dDAO.addDoc(name, dtype, summary, dirID);
				
				String pathRedirect = "/PoliDoc/getListDocs?dir="+dirID;
				resp.sendRedirect(pathRedirect);
				return;
			}
			
			default:
				sendError(resp,"Error!!! The inserted URL is wrong!!");
				return;
			}
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