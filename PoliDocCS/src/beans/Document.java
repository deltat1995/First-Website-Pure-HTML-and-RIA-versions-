package beans;

import java.util.Date;

public class Document {
	private Integer ID;
	private String name;
	private Date creationDate;
	private String type;
	private String summary;
	private Integer IDfolder;
	
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public Integer getIDfolder() {
		return IDfolder;
	}
	public void setIDfolder(Integer iDfolder) {
		IDfolder = iDfolder;
	}
}
