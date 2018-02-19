import java.util.ArrayList;

public class Data {
	private ArrayList<EndPoint> data;
	
	public Data() {
		this.setData(new ArrayList<EndPoint>());
	}

	public ArrayList<EndPoint> getData() {
		return data;
	}

	public void setData(ArrayList<EndPoint> data) {
		this.data = data;
	}
	
	public void parser() {
		//parse Json output
	}

}
