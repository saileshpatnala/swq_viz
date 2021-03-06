package org.ECS193.TripleDataProcessor.routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ECS193.TripleDataProcessor.data.Data;
import org.ECS193.TripleDataProcessor.data.EndPoint.ENDPOINT_TYPE;
import org.json.JSONObject;

@Path("dbpedia")
public class DBpedia {

	@POST   
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String post(String input) {
		System.out.println("POST request: " + input);
		String output = "[]";

		try {
			return parserDBpedia(input);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	public static String parserDBpedia(String input) throws IOException {
		JSONObject jsonObject = new JSONObject();

		try {
			String url = Helper.generate_dbpedia_query(input);
			
			Data data = new Data(url, ENDPOINT_TYPE.dbpedia);
			jsonObject = data.constructJSON(input);		
		}	
		catch(Exception e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
}
