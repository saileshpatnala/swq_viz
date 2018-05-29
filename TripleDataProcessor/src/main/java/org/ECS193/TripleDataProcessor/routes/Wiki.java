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
import org.ECS193.TripleDataProcessor.data.EndPoint;
import org.ECS193.TripleDataProcessor.data.EndPoint.ENDPOINT_TYPE;
import org.json.JSONObject;

@Path("wiki")
public class Wiki {

	@POST   
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String post(String input) {
		System.out.println("POST request: " + input);
		String output = "";

		try {
			return parserWiki(input);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	public static String parserWiki(String input) throws IOException {
		JSONObject jsonObject = new JSONObject();

		String url = Helper.generate_wiki_query(input);
//		String url2 = Helper.generate_wikidbpedia_query(input);

		String rawJSON = Helper.query2(url);
//		String rawJSON2 = Helper.query2(url2);
		
		Data data = new Data(url, ENDPOINT_TYPE.wiki);
//		data.addEndPoint(new EndPoint(url2, ENDPOINT_TYPE.wiki));		

		jsonObject = data.constructJSON(input);		

		return jsonObject.toString();
	}
	
}
