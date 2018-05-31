package org.ECS193.TripleDataProcessor.routes;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ECS193.TripleDataProcessor.data.Data;
import org.ECS193.TripleDataProcessor.data.EndPoint.ENDPOINT_TYPE;
import org.json.JSONObject;

@Path("libraryall")
public class LibraryAll {
	
	@POST   
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String post(String input) {
		System.out.println("POST request: " + input);
		String result = "";
		String url = "";
		JSONObject jsonObject = new JSONObject();
		try {
			url = Helper.generate_libraryall_query(input);
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