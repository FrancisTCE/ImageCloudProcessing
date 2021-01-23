package data_structs;

import java.io.Serializable;

public class WorkerLogin implements Serializable {

	/**
	 *  This class gives the server a new worker to work with giving the workers search type
	 */
	private static final long serialVersionUID = -6876691725893180655L;
	private String search_type;

	public WorkerLogin(String search_type) {
		super();
		this.search_type = search_type;
	}

	public String getSearch_type() {
		return search_type;
	}
	
	
}
