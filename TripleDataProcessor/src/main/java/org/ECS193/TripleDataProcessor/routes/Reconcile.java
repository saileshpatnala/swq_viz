package org.ECS193.TripleDataProcessor.routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ECS193.TripleDataProcessor.data.Data;
import org.ECS193.TripleDataProcessor.data.EndPoint.ENDPOINT_TYPE;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("reconcile")
public class Reconcile {

	@POST   
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String post(String input) {
		System.out.println("POST request: " + input);
		String output = "";
		try {
			output = parserReconcile(input);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	  
		return output;
	}
	
	public static String parserReconcile(String input) throws IOException {
		List<String> possiblePoints = Arrays.asList("viafID", "locID", "wikiID", "imdbID", "author");
		String url = Helper.generate_reconciler_query(input);
		String rawJSON = Helper.query(url);
		JSONObject jsonOut = new JSONObject();
		JSONArray array = new JSONArray();

		//Parsing
		JSONObject jsonObject = new JSONObject(rawJSON);
		
		JSONObject headObject = jsonObject.getJSONObject("head");
        JSONArray endPoints = headObject.getJSONArray("vars");
        
		JSONObject resultObject = jsonObject.getJSONObject("results");
        if (resultObject.getJSONArray("bindings").length() < 1) {
            return "[]";
        }
		JSONObject bindingPoints = (JSONObject)resultObject.getJSONArray("bindings").get(0);
        
        for(int i = 0; i < endPoints.length(); i++) {
            	
        		if(possiblePoints.contains(endPoints.get(i))) {
        			JSONObject temp = new JSONObject();
        			String key = endPoints.get(i).toString();
        			String value = ((JSONObject) bindingPoints.get(key)).getString("value");        					
        			temp.put(key, value);
        			array.put(temp);
        		}
        }
        jsonOut.put("Reconciler", array);

		return jsonOut.toString();
	}
	
}
