package zcom.ISCTEAPPLOGO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import concurrent_structs.Task;
import concurrent_structs.TaskWorkerSide;
import data_structs.ClientLogin;
import data_structs.Client_ID_Search;
import data_structs.SerializedBuff_Image_Name;
import data_structs.SerializedBuff_Image_Name_List;
import data_structs.ServerPing;
import data_structs.Update_Searches;
import data_structs.WorkPackage;
import data_structs.WorkPackageResponse;
import data_structs.WorkerLogin;
import data_structs.Worker_ID_Search;

public class ServerThread extends Thread {

	private Socket s;
	private Server server;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private int ID;
	//private LinkedList<Worker_ID_Search> queue = new LinkedList<Worker_ID_Search>();
	private LinkedList<Client_ID_Search> queue = new LinkedList<Client_ID_Search>();
	//private LinkedList<Points_Name> points_names = new LinkedList<Points_Name>();
	private LinkedList<String> searches = new LinkedList<String>();
	private TaskWorkerSide task;
	private String worker_search = null;
	private boolean finishedTask = false;
	private boolean client = false;
	private boolean worker = false;
	private boolean disconected = false;
	private String failedFileName = "";
	private int failedClientID = -1;
	private String failedSearchOfFile = "";
	//private boolean working = false;
	private ServerPing sp;
	//private SubServerThread failSafeThread;
	private LinkedList<SerializedBuff_Image_Name> images_list_failsafe = new LinkedList<SerializedBuff_Image_Name>();
	private SerializedBuff_Image_Name_List clientData;
	private BufferedImage subImage_failsafe;
	private boolean ENDED_ALL_IMAGES = false;
	//private int tasker_id;
	
	public ServerThread(Socket s, Server server, LinkedList<Client_ID_Search> queue) {
		this.s = s;
		this.server = server;
		this.queue = queue;
		sp = new ServerPing(this);
	}
	
	@Override
	public void run() {
		task = server.getTask();
		
		Connect(s);
		Communicate();
	}

	private void Communicate() {
		Object obj;	
		while(true) {
			try {
				obj = ois.readObject();
				if(obj instanceof ClientLogin) {
					sp.start();
					ID = server.getID();
					client = true;
					searches.addAll(server.getUpdatedSearches());
					oos.writeObject(new Update_Searches(searches));
				}
				if(obj instanceof WorkerLogin) {
					if(((WorkerLogin) obj).getSearch_type() == null) {
						//task.place(new Worker_ID_Search(ID, worker_search));
						Client_ID_Search cis = task.consume(this.worker_search);
						int cid = cis.getClientID();
						WorkPackage wp = new WorkPackage(cid, this.ID, server.getWorkPackage(cis));
						oos.writeObject(wp);
					} else {
					sp.start();
					worker = true;
					ID = server.getID();
					server.getID();
					this.worker_search =  ((WorkerLogin) obj).getSearch_type();
					server.setUpdatedSearches((WorkerLogin) obj);
					Client_ID_Search cis = task.consume(this.worker_search);
					int cid = cis.getClientID();
					WorkPackage wp = new WorkPackage(cid, this.ID, server.getWorkPackage(cis));
					oos.writeObject(wp);
					//task.place(new Worker_ID_Search(ID, ((WorkerLogin) obj).getSearch_type()));
					
					}
				}
				if(obj instanceof SerializedBuff_Image_Name_List) {
					//PrepareTasks((SerializedBuff_Image_Name_List) obj);
					clientData = null;
					clientData = (SerializedBuff_Image_Name_List) obj;
					PrepareTasksSingular((SerializedBuff_Image_Name_List) obj);
					ENDED_ALL_IMAGES = false;
				}
				if(obj instanceof WorkPackageResponse) {
					if(finishedTask) {
					server.sendPointsToClient((WorkPackageResponse) obj);
					} else {
						sp.setWorking(false);
						stackPoints((WorkPackageResponse) obj);
					}	
				}
			} catch (ClassNotFoundException | IOException e) {
				if(isWorker() && !ENDED_ALL_IMAGES) {
				server.workerDisconnected(worker_search);
				server.FailedFile(failedClientID,failedFileName,failedSearchOfFile, images_list_failsafe, subImage_failsafe);
				server.ConsolePost("Failsafe activated for: " + failedFileName + " with search " + failedSearchOfFile );
				}disconected = true;
				sp.setBreak(true);
				ping();
				break;
			}
		}
		
	}

	private void stackPoints(WorkPackageResponse obj) {
		server.sendPointsToClient(obj);
	}

	/*private void PrepareTasks(SerializedBuff_Image_Name_List images_List_serialized) {
		Worker_ID_Search wis;
		for (String search : images_List_serialized.getSearches()) {
			server.ConsolePost("Trying to consume " + search);
			wis = task.consume(search);
			server.sendImagesToWorker(getID(),wis.getID(),images_List_serialized);
		}
	}*/
	
	private void PrepareTasksSingular(SerializedBuff_Image_Name_List images_List_serialized) {
		/*Worker_ID_Search wis;
		LinkedList<SerializedBuff_Image_Name> aux_singular = new LinkedList<SerializedBuff_Image_Name>();
		LinkedList<String> searches = new LinkedList<String>();
		Set<String> noDups = new LinkedHashSet<String>(images_List_serialized.getSearches());
		LinkedList<String> dab = new LinkedList<String>();
		dab.addAll(noDups);
		for (String search : dab) {
			for (SerializedBuff_Image_Name a : images_List_serialized.getImages_list()) {
				aux_singular.clear();
				searches.clear();
				wis = task.consume(search);
				aux_singular.add(a);
				searches.add(search);
				System.out.println("Sending: " + aux_singular.get(0).getName());
				server.sendImagesToWorker(getID(), wis.getID(), new SerializedBuff_Image_Name_List(aux_singular, searches, images_List_serialized.getSubImage()));
			}
		}*/
		// muultithreading version:
		for (String search : images_List_serialized.getSearches()) {
			for (SerializedBuff_Image_Name serializedBuff_Image_Name : images_List_serialized.getImages_list()) {
				task.place(new Client_ID_Search(this.getID(),serializedBuff_Image_Name.getName(), search));	
			}
		}
		Set<String> noDups = new LinkedHashSet<String>(images_List_serialized.getSearches());
		LinkedList<String> dab = new LinkedList<String>();
		dab.addAll(noDups);
		for (String search : dab) {
			//new SubServerThread(this, server.getTask(), search, images_List_serialized).start();
		}
		ENDED_ALL_IMAGES = true;
		
	}

	public synchronized void prepareToSendToWorkerImage(Worker_ID_Search wis, SerializedBuff_Image_Name_List images_List_serialized, LinkedList<String> searches2) {
		server.sendImagesToWorker(getID(), wis.getID(), new SerializedBuff_Image_Name_List(images_List_serialized.getImages_list(), searches2, images_List_serialized.getSubImage()));
	}
	
	private void Connect(Socket s) {
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getID() {
		return this.ID;
	}

	public void sendWorkPackage(WorkPackage workPackage) {
		if(disconected) {
			
		}else {
		try {
			oos.writeObject(workPackage);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		}
		
	}

	public void sendPointsToClient(WorkPackageResponse wpr) {
		try {
			oos.writeObject(wpr);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Socket is disconected");
		}
		
	}

	public boolean isClient() {
		return client;
	}

	public boolean isWorker() {
		return worker;
	}

	public void sendSearches(LinkedList<String> activeSearches) {
		try {
			System.out.println("Sending " + activeSearches.size() + " searches");
			oos.writeObject(new Update_Searches(activeSearches));
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Socket is disconected");
		}
		
	}

	public void ping() {
		try {
			if(!disconected) {
			/*	if(client)
					System.out.println("Pinging socket to check connection with client id: " + this.ID);
				if(worker)
					System.out.println("Pinging socket to check connection with worker id: " + this.ID);*/
				oos.writeObject("");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(isWorker()) {
				server.workerDisconnected(worker_search);
				server.FailedFile(failedClientID,failedFileName,failedSearchOfFile, images_list_failsafe, subImage_failsafe);
				server.ConsolePost("Failsafe activated for: " + failedFileName + " with search " + failedSearchOfFile );
			}
			e.printStackTrace();
		}
		
	}

	public boolean isDisconected() {
		return disconected;
	}

	public LinkedList<Client_ID_Search> getQueue() {
		return queue;
	}

	public void ActivateFailSafe(int failedClientID2, String failedFileName2, String failedSearchOfFile2,
			LinkedList<SerializedBuff_Image_Name> images_list, BufferedImage subImage ) {
		LinkedList<String> search_aux = new LinkedList<String>();
		search_aux.add(failedSearchOfFile2);
		LinkedList<SerializedBuff_Image_Name> images_list1 = new LinkedList<SerializedBuff_Image_Name>();
		BufferedImage failedImage = null;
		for (SerializedBuff_Image_Name sbin : images_list) {
			if(sbin.getName().equals(failedFileName2))
				failedImage = sbin.getImage();
		}
		images_list1.add(new SerializedBuff_Image_Name(failedFileName2, failedImage));
		SerializedBuff_Image_Name_List images_List_serialized_aux = 
				new SerializedBuff_Image_Name_List(images_list1, search_aux, subImage);
		//new SubServerThread(this, server.getTask(), failedSearchOfFile2, images_List_serialized_aux).start();;
		
		
	}

	public void setClientFailSafe(int idClient, String search, SerializedBuff_Image_Name image,
			BufferedImage subImage) {
		
		failedClientID = idClient;
		failedSearchOfFile = search;
		failedFileName = image.getName();
		subImage_failsafe = subImage;
		images_list_failsafe.add(image);
		
	}

	public SerializedBuff_Image_Name_List getSingularWorkPackage(Client_ID_Search cis) {
			for (SerializedBuff_Image_Name sbin : clientData.getImages_list()) {
				if(sbin.getName().equals(cis.getFileName())) {
					String searches1 = cis.getSearch();
					LinkedList<String> searches = new LinkedList<String>();
					searches.add(searches1);
					BufferedImage subImage = clientData.getSubImage();
					LinkedList<SerializedBuff_Image_Name> images_list = new LinkedList<SerializedBuff_Image_Name>();
					images_list.add(new SerializedBuff_Image_Name(cis.getFileName(), sbin.getImage()));
					SerializedBuff_Image_Name_List sbin1 = new SerializedBuff_Image_Name_List(images_list, searches, subImage);
					return sbin1;
				}
			}
		return null;
	}

	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
