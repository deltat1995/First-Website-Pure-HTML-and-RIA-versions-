package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;

public class UserDAO {
	private Connection con;
	
	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public boolean checkLogin (String username, String password) throws SQLException {
		String query = "SELECT * FROM polidoccs.user WHERE username=? AND password=?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		boolean loginChecked = false;
		pstatement = con.prepareStatement(query);
		pstatement.setString(1, username);
		pstatement.setString(2, password);
		result = pstatement.executeQuery();
		if (result.next()) {
			loginChecked = true;
		}
		
		try {
			result.close();
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return loginChecked;
	}
	
	public boolean isUnique(String username) throws SQLException {
		String query = "SELECT * FROM polidoccs.user WHERE username=?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		boolean isUnique = true;
		pstatement = con.prepareStatement(query);
		pstatement.setString(1, username);
		result = pstatement.executeQuery();
		if (result.next()) {
			isUnique = false;
		}
		
		try {
			result.close();
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isUnique;
	}
	
	public void signup(User user) throws SQLException {
		String query = "INSERT INTO polidoccs.user (username, email, password) VALUES (?,?,?);";
		PreparedStatement pstatement = null;
		pstatement = con.prepareStatement(query);
		pstatement.setString(1, user.getUsername());
		pstatement.setString(2, user.getEmail());
		pstatement.setString(3, user.getPassword());
		pstatement.executeUpdate();
	
		try {
			pstatement.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
	}
}