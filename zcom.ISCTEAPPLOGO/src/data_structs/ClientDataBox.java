package data_structs;

import java.awt.image.BufferedImage;

public class ClientDataBox {

	private String search;
	private BufferedImage image;
	private String imageName;
	private int points;
	
	public ClientDataBox(String search, BufferedImage image, String imageName, int points) {
		super();
		this.search = search;
		this.image = image;
		this.imageName = imageName;
		this.points = points;
	}
	public String getSearch() {
		return search;
	}
	public BufferedImage getImage() {
		return image;
	}
	public String getImageName() {
		return imageName;
	}
	public int getPoints() {
		return points;
	}
	
	
	
	
	
	
}
