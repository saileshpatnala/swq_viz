package org.ECS193.TripleDataProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ECS193.TripleDataProcessor.EndPoint.ENDPOINT_TYPE;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.CookieParam;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {
	private static final String COOKIE_PARAM = "search";
	private static final String INDEX_FILE_PATH = "index";

	@POST   
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_HTML)
	public Response post(String input) {
		System.out.println("POST request: " + input);
		NewCookie cookie = new NewCookie(COOKIE_PARAM, input);
		try {
			return Response.ok(INDEX_FILE_PATH).cookie(cookie).build();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@CookieParam(COOKIE_PARAM) String input) {
		System.out.println("GET request: " + input);
		String result = "";
		String libraryInput = "";
		String url = "";
		JSONObject jsonObject = new JSONObject();
		try {
			libraryInput = parserViaf(input);
			url = generate_query(libraryInput);
			result = query(url);
			Data data = new Data(result, ENDPOINT_TYPE.library);
			jsonObject = data.constructJSON(input);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	  
		return jsonObject.toString();
	}
	

	public static String query(String url) throws IOException {
		
		URL link = new URL(url);
		HttpURLConnection httpLink = (HttpURLConnection) link.openConnection();
		httpLink.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(httpLink.getInputStream()));
		String inputLine;
		StringBuffer resp = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			resp.append(inputLine);
		}

		System.out.println("Raw Output: " + resp);
		System.out.println();

		return resp.toString();
	}
	
	public static String parserViaf(String id) throws IOException {
		String libraryInput = "";
		String url = generate_query_viaf(id);
		String rawJSON = query(url);
		
		JSONObject temp;
		String idElement;
		
		JSONObject jsonObject = new JSONObject(rawJSON);
		JSONArray graphs = jsonObject.getJSONArray("@graph");
		
		for(int i = 0; i < graphs.length(); i++) {
				temp = (JSONObject) graphs.get(i);
				idElement = temp.get("@id").toString();
				List<String> elements = Arrays.asList(idElement.split("/"));
				if(elements.size() == 6) {
					String gotIt = elements.get(5);

					if (gotIt.substring(0, 2).equals("LC")) {
						libraryInput = "n" + gotIt.substring(gotIt.indexOf("++") + 2, gotIt.indexOf("#"));
						System.out.println("LIBRARY INPUT: " + libraryInput);
						return libraryInput;
					}
				}
		}
		
		return libraryInput;
	}
	
	public static String generate_query_viaf(String id) {
		return "http://viaf.org/viaf/" + id + "/viaf.jsonld";
	}
	
	public static String generate_query(String input) {
		// Wiki endpoint
		// String url =
		// "http://dbpedia.org/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
		// Jena Endpoint
		// String url =
		// "http://localhost:3030/ds/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
		// VIAF Endpoint
		// String url =
		// "http://viaf.org/viaf/search?query=cql.any+%3D+%22Chekhov%22&httpAccept=application/json";

		// Jena Endpoint for specific search
		input = "%22"+input.replaceAll(" ", "%20")+"%22";
		String url = 
		"http://localhost:3030/ds/sparql?query=select+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+.+FILTER+(+REGEX(STR(?subject),+" + input + "+)+%7C%7C+REGEX(STR(?predicate),+"+input+"+)+%7C%7C+REGEX(STR(?object),+"+input+"+)+)+}";

		return url;
	}
	
}
