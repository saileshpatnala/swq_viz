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
				JSONObject item = new JSONObject();
				
				JSONObject subjectItem = new JSONObject();
				item.put("subject", subjectItem);
				subjectItem.put("type", triple.getSubject().getType());
				subjectItem.put("value", triple.getSubject().getName());
				
				JSONObject predicateItem = new JSONObject();
				item.put("predicate", predicateItem);
				predicateItem.put("type", triple.getPredicate().getType());
				predicateItem.put("value", triple.getPredicate().getName());
				
				JSONObject objectItem = new JSONObject();
				item.put("object", objectItem);
				objectItem.put("type", triple.getObject().getType());
				objectItem.put("value", triple.getObject().getName());
				array.put(item);
			}
		}
		json.put(input, array);
		
		return json;
	}
	

}
