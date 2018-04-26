package org.ECS193.TripleDataProcessor.routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ECS193.TripleDataProcessor.data.Data;
import org.ECS193.TripleDataProcessor.data.EndPoint.ENDPOINT_TYPE;
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
			libraryInput = Helper.parserViaf(input);
			url = Helper.generate_library_query(libraryInput);
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