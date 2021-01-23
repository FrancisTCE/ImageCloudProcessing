package data_structs;

import java.io.Serializable;

public class WorkPackage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8447358959549249874L;
	private int idClient;
	private int idWorker;
	private SerializedBuff_Image_Name_List images_List_serialized;
	
	public WorkPackage(int idClient, int idWorker, SerializedBuff_Image_Name_List images_List_serialized) {
		super();
		this.idClient = idClient;
		this.idWorker = idWorker;
		this.images_List_serialized = images_List_serialized;
	}
	public int getIdClient() {
		return idClient;
	}
	public int getIdWorker() {
		return idWorker;
	}
	public SerializedBuff_Image_Name_List getImages_List_serialized() {
		return images_List_serialized;
	}
	
	

}
