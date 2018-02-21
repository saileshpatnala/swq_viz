import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.*;
import org.json.JSONObject;

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
	
	public void parser(String data) throws Exception {
		// creating JSONObject
        JSONObject jsonObject = new JSONObject(data);
//        Object obj = new JSONParser().parse(data);
        
        // typecasting obj to JSONObject
//        JSONObject jsonObject = (JSONObject) obj;
        System.out.println("object: " + jsonObject);
		System.out.println("HERE "+ jsonObject);

        String name = (String) jsonObject.get("subject");
        System.out.println(name);

        long age = (Long) jsonObject.get("predicate");
        System.out.println(age);

        // loop array
        JSONArray msg = (JSONArray) jsonObject.get("object");
//        Iterator<String> iterator = msg.iterator();
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }

	}

}
