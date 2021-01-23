package data_structs;

import zcom.ISCTEAPPLOGO.ServerThread;

public class ServerPing extends Thread {

	private ServerThread st;
	private boolean breakable = false;
	private boolean working = false;

	public ServerPing(ServerThread st) {
		super();
		this.st = st;
	}
	
	@Override
	public void run() {
		
		//long createdMillis = System.currentTimeMillis();
		
		while(!breakable) {		
			/*long nowMillis = System.currentTimeMillis();
			if(((int)((nowMillis - createdMillis) / 1000))>=2000) {
				st.ping();
			}*/
			try {
				sleep(10000);
				if(breakable)
					break;
				while(working) {
					sleep(60000);
					break;
				}
				st.ping();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setBreak(boolean b) {
		this.breakable = b;
	}
	
	public void setWorking(boolean b) {
		this.working = b;
	}
	
	
}
