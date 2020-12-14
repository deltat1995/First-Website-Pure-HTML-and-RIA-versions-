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
	
	public ArrayList<Folder> getParentFolders() {
		String query = "SELECT * FROM polidoc.folder WHERE IDparent IS NULL ORDER BY name ASC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		ArrayList<Folder> folders = new ArrayList<>();
		try {
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
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return folders;
	}
	
	
	public ArrayList<Folder> getChildFolders(int IDparent) {
		String query = "SELECT * FROM polidoc.folder WHERE IDparent= ? ORDER BY name ASC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		ArrayList<Folder> folders = new ArrayList<>();
		try {
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
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return folders;
	} 
	
	public void addParentFolder(String nome) {
		String query = "INSERT INTO poliDoc.folder (name, creationDate, IDparent) VALUES (?, ?, NULL);";
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, nome);
			pstatement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
			pstatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstatement.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	public void addChildFolder(String nome, int IDparent) {
		String query = "INSERT INTO poliDoc.folder (name, creationDate, IDparent) VALUES (?, ?, ?);";
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, nome);
			pstatement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
			pstatement.setInt(3, IDparent);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstatement.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	public void deleteFolder(int ID) {
		String query = "DELETE FROM poliDoc.folder WHERE ID= ? OR IDparent= ?;";
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, ID);
			pstatement.setInt(2, ID);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstatement.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}


	public Folder getFolder(Integer foldID) {
		String query = "SELECT * FROM polidoc.folder WHERE ID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		Folder folder = new Folder();
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, foldID);
			result = pstatement.executeQuery();
			if (result.next()) {
				folder.setID(result.getInt("ID"));
				folder.setName(result.getString("name"));
				folder.setCreationDate(new Date(result.getDate("creationDate").getTime()));
				folder.setIDparent(result.getInt("IDparent"));
			}
			else {
				folder = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return folder;
	}

	public boolean existParentDir(Integer dirID) {
		String query = "SELECT * FROM polidoc.folder WHERE IDparent IS NULL AND ID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, dirID);
			result = pstatement.executeQuery();
			if (result.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return false;
	}

	public boolean existSubDir(Integer dirID) {
		String query = "SELECT * FROM polidoc.folder WHERE IDparent IS NOT NULL AND ID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, dirID);
			result = pstatement.executeQuery();
			if (result.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return false;
	}
}
