package zcom.ISCTEAPPLOGO;


import java.util.LinkedList;

import concurrent_structs.Task;
import data_structs.SerializedBuff_Image_Name;
import data_structs.SerializedBuff_Image_Name_List;
import data_structs.Worker_ID_Search;

public class SubServerThread extends Thread {
	
	private ServerThread st;
	private Task task;
	private String search;
	private SerializedBuff_Image_Name_List images_List_serialized;
	
	public SubServerThread(ServerThread st, Task task, String search, SerializedBuff_Image_Name_List images_List_serialized) {
		super();
		this.st = st;
		this.task = task;
		this.search = search;
		this.images_List_serialized = new SerializedBuff_Image_Name_List(images_List_serialized.getImages_list(), null, images_List_serialized.getSubImage());
	}
	
	@Override
	public void run() {
		getTasks();
	}

	private void getTasks() {
		Worker_ID_Search wis;
		for (SerializedBuff_Image_Name element : images_List_serialized.getImages_list()) {
			wis = task.consume(search);
			LinkedList<String> searches = new LinkedList<String>();
			LinkedList<SerializedBuff_Image_Name> image_to_worker = new LinkedList<SerializedBuff_Image_Name>();
			image_to_worker.add(new SerializedBuff_Image_Name(element.getName(), element.getImage()));
			
			//image_to_worker.add(new SerializedBuff_Image_Name_List(images_list, searches, subImage));
			searches.add(search);
			st.prepareToSendToWorkerImage(wis, new SerializedBuff_Image_Name_List(image_to_worker, null, images_List_serialized.getSubImage()),searches);
		}
		
	}
	
	

}
