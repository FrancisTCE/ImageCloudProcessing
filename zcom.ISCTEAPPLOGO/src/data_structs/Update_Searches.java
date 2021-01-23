package data_structs;

import java.io.Serializable;
import java.util.LinkedList;

public class Update_Searches implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6490864973819636374L;
	private LinkedList<String> updatedSearches = new LinkedList<String>();

	public Update_Searches(LinkedList<String> updatedSearches) {
		super();
		this.updatedSearches = updatedSearches;
	}

	public LinkedList<String> getUpdatedSearches() {
		return updatedSearches;
	}
	
	
}
