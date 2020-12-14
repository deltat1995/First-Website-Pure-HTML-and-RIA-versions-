package controllers;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

import javax.servlet.*;
import javax.servlet.http.*;

import DAO.*;
import beans.*;
import utils.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public class HomeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private FolderDAO fDAO;
	private DocDAO dDAO;
	
	public HomeController() {
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
	
		//GESTISCO LO SPOSTAMENTO DEL DOCUMENTO / HOMEPAGE
		String servletpath = req.getServletPath();
		if(servletpath.equals("/moveDoc")) {		//CASO DELLO SPOSTAMENTO DEL DOCUMENTO
			try {
				moveDocHandler(req,resp);
			} catch (Exception e) {
				resp.getWriter().print(e.getMessage());
				resp.getWriter().flush();
				return;
			}
		}
		else {                                        //SEMPLICE CASO DELLA HOMEPAGE(/index or *.html)
			
			//ESTRAGGO CARTELLE E SOTTOCARTELLE DAL DB
			final ArrayList<Folder> pF = fDAO.getParentFolders();
			TreeMap<Folder, ArrayList<Folder>> subDirs = new TreeMap<>(new Comparator<Folder>() {
				@Override
				public int compare(Folder o1, Folder o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			
			for (Folder f : pF) {
				final ArrayList<Folder> subDir = fDAO.getChildFolders(f.getID());
				subDirs.put(f, subDir);
			}
			
			//INSERISCO I PARAMETRI NELLA REQUEST
			req.setAttribute("folders", subDirs);
			req.setAttribute("moveDoc", false);
			
			
			String path = "/homepage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
			templateEngine.process(path, ctx, resp.getWriter());
		}
	}
	
	private void moveDocHandler(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer docID = null;
		Integer intoDir = null;
		
		//CONTROLLO PARAMETRI
		docID = Integer.parseInt(req.getParameter("doc"));
		if (req.getParameter("dir")!=null){
			try {
				intoDir = Integer.parseInt(req.getParameter("dir"));
				if (!fDAO.existSubDir(intoDir)) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				throw new Exception("Error!!! The inserted URL is wrong!!");
			}	
		}
		
		Document doc = dDAO.getDocument(docID);
		if (doc == null) {
			throw new Exception("The document doesn't exist!!!");
		}
		
		if (intoDir==null) {					 //DEVE SELEZIONARE LA SOTTOCARTELLA
			
			//ESTRAGGO CARTELLE E SOTTOCARTELLE DAL DB
			final ArrayList<Folder> pF = fDAO.getParentFolders();
			TreeMap<Folder, ArrayList<Folder>> subDirs = new TreeMap<>(new Comparator<Folder>() {
				@Override
				public int compare(Folder o1, Folder o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			int countSubDir = 0;
			for (Folder f : pF) {
				final ArrayList<Folder> subDir = fDAO.getChildFolders(f.getID());
				countSubDir += subDir.size();
				subDirs.put(f, subDir);
			}
			
			if (countSubDir<2) {
				throw new Exception("Doesn't exist an alternative subdirectory where move this document!!!");
			}
			
			//INSERISCO I PARAMETRI NELLA REQUEST
			req.setAttribute("folders", subDirs);
			req.setAttribute("moveDoc", true);
			req.setAttribute("doc", doc);
			
			String path = "/homepage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
			templateEngine.process(path, ctx, resp.getWriter());
		}
		else {								  	//SPOSTO IL DOCUMENTO E REINDIRIZZO ALLA SOTTOCARTELLA DI DESTINAZIONE
			dDAO.moveDoc(docID,intoDir);
			String pathRedirect = "/PoliDoc/getListDocs?dir="+intoDir;
			resp.sendRedirect(pathRedirect);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		try {
			doGet(req, resp);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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