package data_structs;

import java.io.Serializable;
import java.util.LinkedList;

public class WorkPackageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2968546226096667125L;
	private int idClient;
	private String search;
	private LinkedList<Points_Name> pn = new LinkedList<Points_Name>();
	public WorkPackageResponse(int idClient, LinkedList<Points_Name> pn, String search) {
		super();
		this.idClient = idClient;
		this.pn.addAll(pn);
		this.search = search;
	}
	public int getIdClient() {
		return idClient;
	}
	public LinkedList<Points_Name> getPn() {
		return pn;
	}
	public String getSearch() {
		return search;
	}
	
	
	
}
