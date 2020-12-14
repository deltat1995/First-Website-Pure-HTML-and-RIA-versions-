package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import beans.Folder;


public class FolderDAO {
	private Connection con;
	
	public FolderDAO(Connection connection) {
		this.con = connection;
	}
	
	public ArrayList<Folder> getParentFolders() throws SQLException {
		String query = "SELECT * FROM polidoccs.folder WHERE IDparent IS NULL ORDER BY name ASC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		ArrayList<Folder> folders = new ArrayList<>();
		
		pstatement = con.prepareStatement(query);
		result = pstatement.executeQuery();
		while (result.next()) {
			Folder folder = new Folder();
			folder.setID(result.getInt("ID"));
			folder.setName(result.getString("name"));
			folder.setCreationDate(new Date(result.getDate("creationDate").getTime()));
			folder.setIDparent(null);
			folders.add(folder);
		}
		try {
			result.close();
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return folders;
	}
	
	
	public ArrayList<Folder> getChildFolders(int IDparent) throws SQLException {
		String query = "SELECT * FROM polidoccs.folder WHERE IDparent= ? ORDER BY name ASC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		ArrayList<Folder> folders = new ArrayList<>();
		pstatement = con.prepareStatement(query);
		pstatement.setInt(1, IDparent);
		result = pstatement.executeQuery();
		while (result.next()) {
			Folder folder = new Folder();
			folder.setID(result.getInt("ID"));
			folder.setName(result.getString("name"));
			folder.setCreationDate(new Date(result.getDate("creationDate").getTime()));
			folder.setIDparent(result.getInt("IDparent"));
			folders.add(folder);
		}
	
		try {
			result.close();
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return folders;
	} 
	
	public void addParentFolder(String nome) throws SQLException {
		String query = "INSERT INTO poliDoccs.folder (name, creationDate, IDparent) VALUES (?, ?, NULL);";
		PreparedStatement pstatement = null;
		pstatement = con.prepareStatement(query);
		pstatement.setString(1, nome);
		pstatement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
		pstatement.executeUpdate();
		try {
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addChildFolder(String nome, int IDparent) throws SQLException {
		String query = "INSERT INTO poliDoccs.folder (name, creationDate, IDparent) VALUES (?, ?, ?);";
		PreparedStatement pstatement = null;
		pstatement = con.prepareStatement(query);
		pstatement.setString(1, nome);
		pstatement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
		pstatement.setInt(3, IDparent);
		pstatement.executeUpdate();
		try {
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteFolder(int ID) throws SQLException {
		String query = "DELETE FROM poliDoccs.folder WHERE ID= ?;";
		PreparedStatement pstatement = null;
		pstatement = con.prepareStatement(query);
		pstatement.setInt(1, ID);
		pstatement.executeUpdate();
		try {
			pstatement.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}


	public boolean existParentDir(Integer dirID) throws SQLException {
		String query = "SELECT * FROM polidoccs.folder WHERE IDparent IS NULL AND ID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
	
		pstatement = con.prepareStatement(query);
		pstatement.setInt(1, dirID);
		result = pstatement.executeQuery();
		if (result.next()) {
			return true;
		}
	
		try {
			result.close();
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public boolean existSubDir(Integer dirID) throws SQLException {
		String query = "SELECT * FROM polidoccs.folder WHERE IDparent IS NOT NULL AND ID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		pstatement = con.prepareStatement(query);
		pstatement.setInt(1, dirID);
		result = pstatement.executeQuery();
		if (result.next()) {
			return true;
		}
		try {
			result.close();
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public boolean existFolder(Integer dirID) throws SQLException {
		String query = "SELECT * FROM polidoccs.folder WHERE ID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		pstatement = con.prepareStatement(query);
		pstatement.setInt(1, dirID);
		result = pstatement.executeQuery();
		if (result.next()) {
			return true;
		}
		try {
			result.close();
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
