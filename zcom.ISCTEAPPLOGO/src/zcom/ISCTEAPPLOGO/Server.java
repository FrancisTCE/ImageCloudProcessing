package zcom.ISCTEAPPLOGO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import concurrent_structs.Task;
import concurrent_structs.TaskWorkerSide;
import data_structs.Client_ID_Search;
import data_structs.SerializedBuff_Image_Name;
import data_structs.SerializedBuff_Image_Name_List;
import data_structs.WorkPackage;
import data_structs.WorkPackageResponse;
import data_structs.WorkerLogin;
import data_structs.Worker_ID_Search;
import data_structs.Workers_Numbers;

public class Server {
	private ServerSocket ss;
	private int ID = 0;
	private LinkedList<String> activeSearches = new LinkedList<String>();
	private LinkedList<Client_ID_Search> queue = new LinkedList<Client_ID_Search>();
	private LinkedList<ServerThread> st_List = new LinkedList<ServerThread>();
	private LinkedList<Workers_Numbers> workernumbers = new LinkedList<Workers_Numbers>();
	private TaskWorkerSide task;
	
	public Server(int PORT) {
		
		try {
			ss = new ServerSocket(PORT);
			ConsolePost("Server started with: " + ss.toString());
		} catch (IOException e) { e.printStackTrace(); }
		Socket s;
		ServerThread st;
		task = new TaskWorkerSide(queue);
		while(true) {
			try {
				s = ss.accept();
				ConsolePost(s.getInetAddress().toString());
				st = new ServerThread(s,this,queue);
				st.start();
				st_List.add(st);
			} catch (IOException e) { e.printStackTrace(); break; }
		}
	}
	
	public int getID() {
		ID++;
		return ID;
	}
	
	
	public static void main(String[] args) {
		int PORT = Integer.parseInt(args[0]);
		new Server(PORT);
	}
	
	public void ConsolePost(String str) {
		System.out.println(">> Server: " + str);
	}
	
	public TaskWorkerSide getTask() {
		return task;
	}

	public synchronized LinkedList<String> getUpdatedSearches() {
		Set<String> noDups = new LinkedHashSet<String>(activeSearches);
		activeSearches.clear();
		activeSearches.addAll(noDups);
		for (String string : noDups) {
			System.out.println("Search:" + string);
		}
		return activeSearches;
	}

	public synchronized void setUpdatedSearches(WorkerLogin wl) {
		boolean exists = false;
		for (Workers_Numbers wn: workernumbers) {
			if(wn.getSearch().equals(wl.getSearch_type())) {
				exists = true;
				wn.IncreaseCount();
			}
		}
		if(!exists) {
			workernumbers.add(new Workers_Numbers(wl.getSearch_type(), 1));
			activeSearches.add(wl.getSearch_type());
		}

		sendSearchesToClients();
	}
	
	public synchronized void workerDisconnected(String worker_search) {
		ConsolePost("Removing a worker with: " + worker_search);
		for (Workers_Numbers wn: workernumbers) {
			if(wn.getSearch().contentEquals(worker_search)) {
				wn.DecreaseCount();
				if(wn.getAmmount() == 0) {
					workernumbers.remove(wn);
					activeSearches.remove(worker_search);
					
				}
			}
		}
		sendSearchesToClients();
	}
	
	private synchronized void sendSearchesToClients() {
		Set<String> noDups = new LinkedHashSet<String>(activeSearches);
		LinkedList<String> dab = new LinkedList<String>();
		dab.addAll(noDups);
		//activeSearches.clear();
		//activeSearches.addAll(noDups);

		for (String string : noDups) {
			System.out.println("Available searches: " + string);
		}
		
		for (ServerThread st : st_List) {
			if(st.isClient()) {
				st.sendSearches(dab);
			}
		}
	}

	public synchronized void sendImagesToWorker(int idClient, int idWorker, SerializedBuff_Image_Name_List images_List_serialized) {
		for (ServerThread st : st_List) {
			if(idWorker == st.getID()) {
				st.sendWorkPackage(new WorkPackage(idClient, idWorker, images_List_serialized));
				st.setClientFailSafe(idClient,images_List_serialized.getSearches().getFirst(), 
						images_List_serialized.getImages_list().getFirst(), images_List_serialized.getSubImage());
			}
		}
		
	}

	public synchronized void sendPointsToClient(WorkPackageResponse wpr) {
		for (ServerThread st : st_List) {
			if(wpr.getIdClient() == st.getID()) {
				st.sendPointsToClient(wpr);
			}
		}
		
	}

	public synchronized void FailedFile(int failedClientID, String failedFileName, String failedSearchOfFile,
			LinkedList<SerializedBuff_Image_Name> images_list, BufferedImage subImage) {
		for (ServerThread st : st_List) {
			if(failedClientID == st.getID()) {
				st.ActivateFailSafe(failedClientID,failedFileName,failedSearchOfFile, images_list, subImage);
				ConsolePost("Redo file: " + failedFileName + ":" + failedSearchOfFile);
			}
		}
	}

	public SerializedBuff_Image_Name_List getWorkPackage(Client_ID_Search cis) {
		for (ServerThread st : st_List) {
			if(st.getID() == cis.getClientID()) {
				return st.getSingularWorkPackage(cis);
			}
		}
		return null;
	}

}
