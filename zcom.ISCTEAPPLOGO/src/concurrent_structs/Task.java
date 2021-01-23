package concurrent_structs;

import java.util.LinkedList;

import data_structs.Worker_ID_Search;

public class Task implements Runnable {

	private LinkedList<Worker_ID_Search> queue = new LinkedList<Worker_ID_Search>();
	
	public Task(LinkedList<Worker_ID_Search> queue) {
		this.queue = queue;
	}

	public synchronized Worker_ID_Search consume(String request) {
		int index = -1;
		try {
			while(true) {
				index = -1;
				if(queue.isEmpty()) {
					System.out.println("No workers available for task. Waiting.");
					wait();
				}
				for (Worker_ID_Search worker_ID_Search : queue) {
					index++;
					if(worker_ID_Search.getSearch().equals(request)) {
						queue.remove(index);
						return worker_ID_Search;
					}
				}
				System.out.println("No workers available for task. Waiting.");
				wait();
		} 
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized void place(Worker_ID_Search wis) {
		queue.add(wis);
		notifyAll();
	}
	
	
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
}
