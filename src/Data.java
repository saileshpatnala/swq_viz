import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class Data {
	private ArrayList<EndPoint> data;
	
	public Data(String data) {
		this.setData(new ArrayList<EndPoint>());
		this.addEndPoint(new EndPoint(data));
	}

	public ArrayList<EndPoint> getData() {
		return data;
	}

	public void setData(ArrayList<EndPoint> data) {
		this.data = data;
	}
	
	public void addEndPoint(EndPoint endPoint) {
		data.add(endPoint);
	}
	

}
