package data_structs;

import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;

public class Points_Name implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4592906793359664550L;
	private LinkedList<Point> points = new LinkedList<Point>();
	private String name;
	public Points_Name(LinkedList<Point> points, String name) {
		super();
		this.points.addAll(points);
		this.name = name;
	}
	public LinkedList<Point> getPoints() {
		return points;
	}
	public String getName() {
		return name;
	}
	
	
	
	
	
}
