package org.ECS193.TripleDataProcessor.routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Helper {
	
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
	
	public static String generate_reconcile_query(String id) {
		return "";
	}
	
	public static String generate_viaf_query(String id) {
		return "http://viaf.org/viaf/" + id + "/viaf.jsonld";
	}
	
	// public static String generate_library_query(String input) {
	// 	// Wiki endpoint
	// 	// String url =
	// 	// "http://dbpedia.org/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
	// 	// Jena Endpoint
	// 	// String url =
	// 	// "http://localhost:3030/ds/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
	// 	// VIAF Endpoint
	// 	// String url =
	// 	// "http://viaf.org/viaf/search?query=cql.any+%3D+%22Chekhov%22&httpAccept=application/json";

	// 	// Jena Endpoint for specific search
	// 	input = "%22"+input.replaceAll(" ", "%20")+"%22";
	// 	String url = 
	// 	"http://localhost:3030/ds/sparql?query=select+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+.+FILTER+(+REGEX(STR(?subject),+" + input + "+)+%7C%7C+REGEX(STR(?predicate),+"+input+"+)+%7C%7C+REGEX(STR(?object),+"+input+"+)+)+}";

	// 	return url;
	// }

	// Get library ID from viaf src ID 
	public static String generate_library_query(String input) {
		input = "%22"+input.replaceAll(" ", "%20")+"%22";
		String libraryIDinput = "http://localhost:3030/ds/sparql?query=select+?subject+WHERE+{+?subject+?predicate+?object+.+FILTER+(+REGEX(STR(?subject),+%22alma%22)+%26%26+REGEX(STR(?object),+"+input+"+)+)+}";
		System.out.println("library ID: " + libraryIDinput);
		return libraryIDinput;
	}

	public static String generate_final_query(String input) {
		input = "%22"+input.replaceAll(" ", "%20")+"%22";
		String url = 
		"http://localhost:3030/ds/sparql?query=select+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+.+FILTER+(+REGEX(STR(?subject),+" + input + "+)+%7C%7C+REGEX(STR(?predicate),+"+input+"+)+%7C%7C+REGEX(STR(?object),+"+input+"+)+)+}";

		return url;
	}
	
	public static String parserViaf(String id) throws IOException {
		String libraryInput = "";
		String url = generate_viaf_query(id);
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

	public static String parserLibraryQuery(String input) throws IOException {
		String finalInput = "";

		finalInput = input.substring(170, input.length()-19);
		System.out.println("finalInput: " + finalInput);

		return finalInput;

	}

	public static String parserReconcile(String input) throws IOException {
		String output = "";
		String url = generate_reconcile_query(input);
		String rawJSON = query(url);
		
		
		JSONObject jsonObject = new JSONObject(rawJSON);
		
		
		return "";
	}
}
