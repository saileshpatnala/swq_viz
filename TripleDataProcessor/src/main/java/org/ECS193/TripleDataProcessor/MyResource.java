package org.ECS193.TripleDataProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
			return Response.seeOther(new java.net.URI(INDEX_FILE_PATH))
			.cookie(cookie)
			.build();
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
		JSONObject jsonObject = new JSONObject();
		try {
			result = query(input);
			Data data = new Data(result);
			jsonObject = data.constructJSON(input);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	  
		return jsonObject.toString();
	}
	

	public static String query(String input) throws IOException {
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
}
