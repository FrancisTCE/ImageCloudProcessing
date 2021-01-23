package data_structs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class SerializedBuff_Image_Name implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8859305522133015214L;
	private String name;
	private byte[] image;
	
	public SerializedBuff_Image_Name(String name, BufferedImage image) {
		this.name = name;
		this.image = serialize(image);
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

	public String getName() {
		return name;
	}

	public BufferedImage getImage() {
		 InputStream in = new ByteArrayInputStream(this.image);
		 try {
			return ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
}
