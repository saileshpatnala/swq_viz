package org.ECS193.TripleDataProcessor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

@Path("mainresource")
public class Driver {

//	public static void main(String[] args) {
//		try {
//			Data data = new Data(getData());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	@GET
  @Produces(MediaType.APPLICATION_JSON)
	public String getIt() {
  		String temp = ""; 
  		JSONObject jsonObject = new JSONObject();
        try {
      		temp = getData();
  			Data data = new Data(temp);
  			jsonObject = data.constructJSON("asdf");
  			System.out.println(jsonObject.toString());
		
		} catch (IOException e) {
			e.printStackTrace();
		}
      
        return jsonObject.toString();
	}
	
	public static String getData() throws IOException {
		String url = "http://dbpedia.org/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
//		String url =  "http://localhost:3030/ds/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
        URL link = new URL(url);
        HttpURLConnection httpLink = (HttpURLConnection) link.openConnection();
        httpLink.setRequestMethod("GET");
       
        BufferedReader in = new BufferedReader( new InputStreamReader(httpLink.getInputStream()));
        String inputLine;
        StringBuffer resp = new StringBuffer();
        while ((inputLine = in.readLine()) != null){
            resp.append(inputLine);
        }

        System.out.println("Raw Output: " + resp);
        System.out.println();

		return resp.toString();
		
	}

}
