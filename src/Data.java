import java.util.ArrayList;
import org.json.JSONArray;
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
        JSONObject jsonObject = new JSONObject(data);
        
//        System.out.println("object: " + jsonObject);
//        System.out.println();

        jsonObject = jsonObject.getJSONObject("results");
//        System.out.println(jsonObject);
//        System.out.println();

        JSONArray triples = jsonObject.getJSONArray("bindings");
       
//        System.out.println(triples);
//        System.out.println(triples.length());
        
        JSONObject temp;
        String subject = "";
        String predicate = ""; 
        String object = "";
        for(int i = 0; i < triples.length(); i++) {
        		temp = (JSONObject) triples.get(i);
        		
        		subject = temp.getJSONObject("subject").get("value").toString();
        		predicate = temp.getJSONObject("predicate").get("value").toString();
        		object = temp.getJSONObject("object").get("value").toString();
        		
        		System.out.println("Triple " + (i+1) + ": ");
        		System.out.println(subject);
        	    System.out.println(predicate);
        	    System.out.println(object);
        		System.out.println();
        }
	}
}
