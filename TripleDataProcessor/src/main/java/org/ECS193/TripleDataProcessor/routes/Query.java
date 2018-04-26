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
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.ECS193.TripleDataProcessor.data.Data;
import org.ECS193.TripleDataProcessor.data.EndPoint.ENDPOINT_TYPE;
import org.json.JSONObject;

@Path("query")
public class Query {
	
	@POST   
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String post(String input) {
		System.out.println("POST request: " + input);
		String result = "";
		String url = "";
		JSONObject jsonObject = new JSONObject();
		try {
			url = Helper.generate_library_query(input);
			result = Helper.query(url);
			Data data = new Data(result, ENDPOINT_TYPE.library);
			jsonObject = data.constructJSON(input);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	  
		return jsonObject.toString();
		
	}

}