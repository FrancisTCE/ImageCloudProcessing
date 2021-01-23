package data_structs;

public class Workers_Numbers {

	private String wl;
	private int ammount = 0;
	public Workers_Numbers(String wl, int ammount) {
		super();
		this.wl = wl;
		this.ammount = ammount;
	}
	public String getSearch() {
		return wl;
	}
	public int getAmmount() {
		return ammount;
	}
	public void IncreaseCount() {
		ammount++;
	}
	public void DecreaseCount() {
		ammount--;
	}
}
