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

@Path("imdb")
public class IMDB {

	@POST   
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String post(String input) {
		System.out.println("POST request: " + input);
		String output = "";
		try {
			output = parserIMDB(input);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	  
		return output;
		
	}
	
	public static String parserIMDB(String input) throws IOException {
		String output = "";
		String url = Helper.generate_oclc_query(input);
		String rawJSON = Helper.query(url);
		
		
		JSONObject jsonObject = new JSONObject(rawJSON);
		
		
		return "";
	}
	
}
