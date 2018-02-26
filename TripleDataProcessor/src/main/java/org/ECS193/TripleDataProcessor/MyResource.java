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

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
//    @Produces(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt() {
//        return "mithun";
    		String temp = ""; 
    		JSONObject jsonObject = new JSONObject();
        try {
        		temp = getData();
    			Data data = new Data(temp);
 
    			jsonObject = data.constructJSON();
    			System.out.println(jsonObject.toString());
//    			jsonObject = new JSONObject(temp);    		
    			
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return jsonObject.toString();
    }
    
    public static String getData() throws IOException {
		String url = "http://dbpedia.org/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
//		String url = "http://localhost:3030/ds/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
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
