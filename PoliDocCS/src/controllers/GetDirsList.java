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

import DAO.FolderDAO;
import beans.Folder;
import beans.FolderList;
import utils.ConnectionHandler;


@MultipartConfig
public class GetDirsList extends HttpServlet {

	private static final long serialVersionUID = -8096967413499561227L;
	private Connection connection;
	private FolderDAO fDAO;
	
	public GetDirsList() {
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
		//ESTRAGGO CARTELLE E SOTTOCARTELLE DAL DB
		ArrayList<FolderList> dirsArrayList = new ArrayList<>();
		try {
			ArrayList<Folder> parents = fDAO.getParentFolders();
			for (Folder p : parents) {
				ArrayList<Folder> subDirs = fDAO.getChildFolders(p.getID());
				FolderList fList = new FolderList();
				fList.setParent(p);
				fList.setChilds(subDirs);
				dirsArrayList.add(fList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //Error 500
			resp.getWriter().println("There are some problem in the server. Retry later.");
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
		String json = gson.toJson(dirsArrayList);
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
