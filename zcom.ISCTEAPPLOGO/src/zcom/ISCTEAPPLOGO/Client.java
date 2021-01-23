package zcom.ISCTEAPPLOGO;


import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import data_structs.ClientDataBox;
import data_structs.ClientLogin;
import data_structs.SerializedBuff_Image_Name_List;
import data_structs.Update_Searches;
import data_structs.WorkPackageResponse;

public class Client {

	private Socket s;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private GUI gui;
	private LinkedList<String> searches = new LinkedList<String>();
	//private ClientTaskChecker ctc;
	
	public Client(String IP, int PORT) {
		System.out.println("Client started.");
		gui = new GUI(this);
		gui.start();
		//ctc = new ClientTaskChecker(this);
		//ctc.start();
		Connect(IP,PORT);
		Communicate();
	}
	
	private void Communicate() {
		try {
			oos.writeObject(new ClientLogin());
			oos.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Object obj;
		while(true) {
			try {
				obj = ois.readObject();
				if(obj instanceof WorkPackageResponse) {
					prepareImages((WorkPackageResponse) obj);
				}
				if(obj instanceof Update_Searches) {
					updateSearches((Update_Searches) obj);
				}
				
			} catch (ClassNotFoundException | IOException e) { 
			
			break;}
		}
		
	}

	private void updateSearches(Update_Searches obj) {
		searches.clear();
		searches.addAll(obj.getUpdatedSearches());
		System.out.println("Client received: " + obj.getUpdatedSearches().toString());
		gui.refreshSearches();
	}

	private void prepareImages(WorkPackageResponse obj) {
		gui.WatermarkPoints(obj.getPn(),obj.getSearch());
		System.out.println("Plots received: " + gui.getTasksDone() );
		if(gui.getTasksDone() == gui.getTasksSize()) {
			gui.results();
		}
		
	}

	private void Connect(String IP, int PORT) {
		try {
			s = new Socket(IP, PORT);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LinkedList<String> getFiles(String absolutePath) {
		LinkedList<String> filesList = new LinkedList<String>();
		File directory = new File(absolutePath);
		File[] content = directory.listFiles();
		for(File obj : content) {
			if(obj.isFile()) {
				filesList.add(obj.getName());
			}
		}
		return filesList;
	}

	public static void main(String[] args) {
		String IP = args[0];
		int PORT = Integer.parseInt(args[1]);
		new Client(IP,PORT);

	}

	public void sendImages(SerializedBuff_Image_Name_List serializedBuff_Image_Name_List) {
		try {
			oos.writeObject(serializedBuff_Image_Name_List);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public LinkedList<String> getsearches() {
		// TODO Auto-generated method stub
		return this.searches;
	}

	public boolean AllTasksDone() {
			if(gui.getTasksDone() == gui.getTasksSize())
				return true;
		return false;
	}

	public void requestMissingTasks() {
		LinkedList<ClientDataBox> missingList = new LinkedList<ClientDataBox>();
		for (ClientDataBox clientDataBox : gui.getRecieved_Tasks_List()) {
			if(!gui.getDone_Tasks_List().contains(clientDataBox)) {
				missingList.add(clientDataBox);
			}
		}
		if(!missingList.isEmpty())
			gui.reSend(missingList);		
	}

	public boolean somethinsent() {
		if(gui.getTasksSize() == 0)
		return true;
		return false;
	}

}
