package beans;

import java.util.Date;

public class Folder {
	private Integer ID;
	private String name;
	private Date creationDate;
	private Integer IDparent;
	
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Integer getIDparent() {
		return IDparent;
	}
	public void setIDparent(Integer iDparent) {
		IDparent = iDparent;
	}
	
}
