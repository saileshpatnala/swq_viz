package org.ECS193.TripleDataProcessor;

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

import org.json.JSONObject;

/**
 * Root re	source (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *	
     * @return String that will be returned as a text/plain response.
     */
    @POST
	@Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt(String input) {
    		String temp = ""; 
    		JSONObject jsonObject = new JSONObject();
        try {
        		temp = getData();
    			Data data = new Data(temp);
 
    			jsonObject = data.constructJSON(input);
    			System.out.println(jsonObject.toString());
    			
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return jsonObject.toString();
    }
    
    public static String getData() throws IOException {
    		//Wiki endpoint
    		String url = "http://dbpedia.org/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
    		//Jena Endpoint	
    	//	String url = "http://localhost:3030/ds/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
    		//VIAF Endpoint
//    		String url = "http://viaf.org/viaf/search?query=cql.any+%3D+%22Chekhov%22&httpAccept=application/json";	
		
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
