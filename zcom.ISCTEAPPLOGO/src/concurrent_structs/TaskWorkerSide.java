package concurrent_structs;

import java.util.LinkedList;

import data_structs.Client_ID_Search;

public class TaskWorkerSide implements Runnable {

private LinkedList<Client_ID_Search> queue = new LinkedList<Client_ID_Search>();
	
	public TaskWorkerSide(LinkedList<Client_ID_Search> queue) {
		this.queue = queue;
	}

	public synchronized Client_ID_Search consume(String request) {
		int index = -1;
		try {
			while(true) {
				index = -1;
				if(queue.isEmpty()) {
					System.out.println("No tasks available for task. Waiting.");
					wait();
				}
				for (Client_ID_Search client_ID_Search : queue) {
					index++;
					if(client_ID_Search.getSearch().equals(request)) {
						queue.remove(index);
						return client_ID_Search;
					}
				}
				System.out.println("No tasks available for task. Waiting.");
				wait();
		} 
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized void place(Client_ID_Search cis) {
		queue.add(cis);
		notifyAll();
	}
	
	
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
