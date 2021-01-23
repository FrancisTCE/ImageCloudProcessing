package data_structs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;

import javax.imageio.ImageIO;

public class SerializedBuff_Image_Name_List implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6889677360536016516L;
	private LinkedList<SerializedBuff_Image_Name> images_list = new LinkedList<SerializedBuff_Image_Name>();
	private LinkedList<String> searches = new LinkedList<String>();
	private byte[] subImage;
	
	public SerializedBuff_Image_Name_List(LinkedList<SerializedBuff_Image_Name> images_list, LinkedList<String> searches,
			BufferedImage subImage) {
		this.images_list.addAll(images_list);
		this.searches = searches;
		this.subImage = serialize(subImage);
	}

	public LinkedList<SerializedBuff_Image_Name> getImages_list() {
		return images_list;
	}

	public LinkedList<String> getSearches() {
		return searches;
	}
	
	private byte[] serialize(BufferedImage image) {
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 try {
			ImageIO.write(image, "png", baos);
			 baos.flush();
			 byte[] img = baos.toByteArray();
			 baos.close();
			 return img;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public BufferedImage getSubImage() {
		 InputStream in = new ByteArrayInputStream(this.subImage);
		 try {
			return ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
	}
	
}
