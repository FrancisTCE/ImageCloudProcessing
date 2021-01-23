package zcom.ISCTEAPPLOGO;

public class ClientTaskChecker extends Thread{
	
	private Client client;
		
	public ClientTaskChecker(Client client) {
		super();
		this.client = client;
	}

	@Override
	public void run() {
	
		while(true) {
			if(client.somethinsent()) {
				
			}else {
			if(!client.AllTasksDone()) {
				client.requestMissingTasks();
			} else {
				try {
					sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
		}
		
		
	}

}
