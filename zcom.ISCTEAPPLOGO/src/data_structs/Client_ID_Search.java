package data_structs;

public class Client_ID_Search {

	private int ClientID;
	private String Search;
	private String fileName;
	
	public Client_ID_Search(int clientID, String fileName, String search) {
		super();
		ClientID = clientID;
		Search = search;
		this.fileName = fileName;
	}
	public int getClientID() {
		return ClientID;
	}
	public String getSearch() {
		return Search;
	}
	public String getFileName() {
		return fileName;
	}
	
	
	
	
}
