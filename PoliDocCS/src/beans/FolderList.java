package beans;

import java.util.ArrayList;


public class FolderList {
	private Folder parent;
	private ArrayList<Folder> childs;
	
	
	public Folder getParent() {
		return parent;
	}
	public void setParent(Folder parent) {
		this.parent = parent;
	}
	public ArrayList<Folder> getChilds() {
		return childs;
	}
	public void setChilds(ArrayList<Folder> childs) {
		this.childs = childs;
	}
}
