package org.ECS193.TripleDataProcessor;
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
	
	public JSONObject constructJSON(String input) {
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		for (EndPoint endPoint : data) {
			for(Triple triple : endPoint.getTriples()) {
				System.out.println("HERE " + triple.getSubject().getName());
				JSONObject item = new JSONObject();
				item.put("subject", triple.getSubject().getName());
				item.put("predicate", triple.getPredicate().getName());
				item.put("object", triple.getObject().getName());
				array.put(item);
			}
		}
		json.put(input, array);
		
		return json;
	}
	

}
