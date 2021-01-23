package data_structs;

import java.io.Serializable;
import java.util.LinkedList;

public class Points_Name_List implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2272271157696955049L;
	private LinkedList<Points_Name> points_list = new LinkedList<Points_Name>();

	public Points_Name_List(LinkedList<Points_Name> points_list) {
		super();
		this.points_list = points_list;
	}

	public LinkedList<Points_Name> getPoints_list() {
		return points_list;
	}
	

}
