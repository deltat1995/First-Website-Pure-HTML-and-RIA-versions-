package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import beans.Document;


public class DocDAO {
	private Connection con;
	
	public DocDAO(Connection connection) {
		this.con = connection;
	}
	
	public ArrayList<Document> getDocumentsInto(int IDfolder) throws SQLException{
		String query = "SELECT * FROM polidoccs.document WHERE IDfolder= ? ORDER BY name ASC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		ArrayList<Document> docs = new ArrayList<>();
	
		pstatement = con.prepareStatement(query);
		pstatement.setInt(1, IDfolder);
		result = pstatement.executeQuery();
		while (result.next()) {
			Document doc = new Document();
			doc.setID(result.getInt("ID"));
			doc.setName(result.getString("name"));
			doc.setCreationDate(new Date(result.getDate("creationDate").getTime()));
			doc.setType(result.getString("type"));
			doc.setSummary(result.getString("summary"));
			doc.setIDfolder(result.getInt("IDfolder"));
			docs.add(doc);
		}
	
		try {
			result.close();
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return docs;	
	}
	
	public ArrayList<Document> getDocumentsWithParent(int IDparent) throws SQLException{
		String query = "SELECT * FROM polidoccs.document WHERE IDfolder = ANY (SELECT ID FROM polidoc.folder WHERE IDparent= ?) ORDER BY name ASC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		ArrayList<Document> docs = new ArrayList<>();
		
		pstatement = con.prepareStatement(query);
		pstatement.setInt(1, IDparent);
		result = pstatement.executeQuery();
		while (result.next()) {
			Document doc = new Document();
			doc.setID(result.getInt("ID"));
			doc.setName(result.getString("name"));
			doc.setCreationDate(new Date(result.getDate("creationDate").getTime()));
			doc.setType(result.getString("type"));
			doc.setSummary(result.getString("summary"));
			doc.setIDfolder(result.getInt("IDfolder"));
			docs.add(doc);
		}
	
		try {
			result.close();
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return docs;	
	}
	
	public void addDoc(String nome, String tipo, String sommario, int IDfolder) throws SQLException {
		String query = "INSERT INTO poliDoccs.document (name, creationDate, type, summary, IDfolder) VALUES (?,?,?,?,?);";
		PreparedStatement pstatement = null;
		
		pstatement = con.prepareStatement(query);
		pstatement.setString(1, nome);
		pstatement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
		pstatement.setString(3, tipo);
		pstatement.setString(4, sommario);
		pstatement.setInt(5, IDfolder);
		pstatement.executeUpdate();
	
		try {
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void deleteDoc(int ID) throws SQLException {
		String query = "DELETE FROM poliDoccs.document WHERE ID= ?;";
		PreparedStatement pstatement = null;
		
		pstatement = con.prepareStatement(query);
		pstatement.setInt(1, ID);
		pstatement.executeUpdate();
	
		try {
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public Document getDocument(Integer docID) throws SQLException {
		String query = "SELECT * FROM polidoccs.document WHERE ID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		Document doc = null;
		
		pstatement = con.prepareStatement(query);
		pstatement.setInt(1, docID);
		result = pstatement.executeQuery();
		if (result.next()) {
			doc = new Document();
			doc.setID(result.getInt("ID"));
			doc.setName(result.getString("name"));
			doc.setCreationDate(new Date(result.getDate("creationDate").getTime()));
			doc.setType(result.getString("type"));
			doc.setSummary(result.getString("summary"));
			doc.setIDfolder(result.getInt("IDfolder"));
		}
		else {
			doc = null;
		}
	
		try {
			result.close();
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return doc;
	}

	public void moveDoc(Integer docID, Integer intoDir) throws SQLException {
		String query = "UPDATE polidoccs.document SET IDfolder= ? WHERE ID= ?";
		PreparedStatement pstatement = null;
		
		pstatement = con.prepareStatement(query);
		pstatement.setInt(1, intoDir);
		pstatement.setInt(2, docID);
		pstatement.executeUpdate();
	
		try {
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

}
