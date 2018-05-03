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
	
	/*
		PREFIX passagedt: <http://18.218.102.193/prop/direct/>
		PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
		PREFIX passagee: <http://18.218.102.193/entity/>
		PREFIX wikibase: <http://wikiba.se/ontology#>
		PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

		SELECT ?personURL ?viafID ?locID ?wikiID ?fastID ?isniID ?imdbID ?author 
		WHERE {
		  ?personURL passagedt:P5 passagee:Q7;
		             passagedt:P6 ?viafID;
		             passagedt:P105 ?locID; 
		             passagedt:P8 ?wikiID; 
		             passagedt:P7 ?fastID; 
		             passagedt:P40 ?isniID; 
		             passagedt:P116 ?imdbID; 
		             rdfs:label ?author.
		 FILTER(LANG(?author)="en" && REGEX(STR(?locID),"n79032879")).
		}
	*/

	// NOTE: generates mapped URIs (above) from reconciler DB for a specific library of congress ID from library DS. If you want output in xml, get rid of &format=json
	// OUTPUT: JSON data of all IDs 
	public static String generate_reconciler_query(String loc_id) {
		return "http://query.projectpassage.org/sparql/?query=PREFIX%20passagedt%3A%20%3Chttp%3A%2F%2F18.218.102.193%2Fprop%2Fdirect%2F%3E%0APREFIX%20rdfs%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0APREFIX%20passagee%3A%20%3Chttp%3A%2F%2F18.218.102.193%2Fentity%2F%3E%0APREFIX%20wikibase%3A%20%3Chttp%3A%2F%2Fwikiba.se%2Fontology%23%3E%0APREFIX%20rdf%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0A%0ASELECT%20%3FpersonURL%20%3FviafID%20%3FlocID%20%3FwikiID%20%3FfastID%20%3FisniID%20%3FimdbID%20%3Fauthor%20WHERE%20%7B%3FpersonURL%20passagedt%3AP5%20passagee%3AQ7%3B%0A%20passagedt%3AP6%20%3FviafID%3B%20passagedt%3AP105%20%3FlocID%3B%20passagedt%3AP8%20%3FwikiID%3B%20passagedt%3AP7%20%3FfastID%3B%20passagedt%3AP40%20%3FisniID%3B%20passagedt%3AP116%20%3FimdbID%3B%20rdfs%3Alabel%20%3Fauthor.%0A%20FILTER%28LANG%28%3Fauthor%29%3D%22en%22%20%26%26%20REGEX%28STR%28%3FlocID%29%2C%22"+loc_id+"%22%29%29.%0A%7D&format=json";
	}

	/*
		PREFIX passagedt: <http://18.218.102.193/prop/direct/>
		PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
		PREFIX passagee: <http://18.218.102.193/entity/>
		PREFIX wikibase: <http://wikiba.se/ontology#>
		PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

		SELECT ?personURL ?viafID ?locID ?wikiID ?fastID ?isniID ?imdbID ?author
		WHERE {
		         ?personURL passagedt:P5 passagee:Q7;
		          passagedt:P6 ?viafID;
		          passagedt:P105 ?locID;
		          passagedt:P8 ?wikiID;
		          passagedt:P7 ?fastID;
		          passagedt:P40 ?isniID;
		          passagedt:P116 ?imdbID;
		          rdfs:label ?author.
		  FILTER(LANG(?author)="en")
		}
	*/

	// NOTE: will generate all authors and mapped IDs (above) from reconciler DB. If you want output in xml, get rid of &format=json
	// OUTPUT: JSON data of all IDs 
	public static String generate_all_reconciler_query() {
		return "http://query.projectpassage.org/sparql/?query=PREFIX%20passagedt%3A%20%3Chttp%3A%2F%2F18.218.102.193%2Fprop%2Fdirect%2F%3E%0APREFIX%20rdfs%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0APREFIX%20passagee%3A%20%3Chttp%3A%2F%2F18.218.102.193%2Fentity%2F%3E%0APREFIX%20wikibase%3A%20%3Chttp%3A%2F%2Fwikiba.se%2Fontology%23%3E%0APREFIX%20rdf%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0ASELECT%20%3FpersonURL%20%3FviafID%20%3FlocID%20%3FwikiID%20%3FfastID%20%3FisniID%20%3FimdbID%20%3Fauthor%20WHERE%20%7B%3FpersonURL%20passagedt%3AP5%20passagee%3AQ7%3B%20passagedt%3AP6%20%3FviafID%3B%20passagedt%3AP105%20%3FlocID%3B%20passagedt%3AP8%20%3FwikiID%3B%0A%20passagedt%3AP7%20%3FfastID%3B%20passagedt%3AP40%20%3FisniID%3B%20passagedt%3AP116%20%3FimdbID%3B%20rdfs%3Alabel%20%3Fauthor.%20FILTER%28LANG%28%3Fauthor%29%3D%22en%22%29%7D%0A%0A%0A&format=json";
	}

	// generate proper URI to query and get jsonld data from
	public static String generate_URI(String type, String id) {
		id = input.replaceAll(" ", "%20"); 
		
		// VIAF ID
		if type == "viafID" {
			return "http://viaf.org/viaf/" + id;
		}

		// Library of Congress ID
		elif type == "locID" {
			return "http://id.loc.gov/authorities/names/" + id;
		}

		// Wiki Data ID
		elif type == "wikiID" {
			return "http://www.wikidata.org/entity/" + id;
		}

		// FAST ID
		elif type == "fastID" {
			return "http://id.worldcat.org/fast/" + id;

		}

		// ISNI ID
		elif type == "isniID" {
			return "http://www.isni.org/" + id;
		}

		// OCLC ID 
		else {
			return "http://www.worldcat.org/oclc/" + id;
		}

	}
	
	// public static String generate_viaf_query(String id) {
	// 	return "http://viaf.org/viaf/" + id + "/viaf.jsonld";
	// }

	// NOTE: returning data in n-triples, except for VIAF
	public static String generate_URI_query(string uri) {
		id = input.replaceAll(" ", "%20"); 
		
		// VIAF ID
		if type == "viafID" {
			return "http://viaf.org/viaf/" + id + "/viaf.jsonld"; 
		}

		// Library of Congress ID
		elif type == "locID" {
			return "http://id.loc.gov/authorities/names/" + id + ".nt";
			// if you want json replace ".nt" with ".json"
		}

		// Wiki Data ID
		// SOURCE: https://www.wikidata.org/wiki/Wikidata:Data_access
		elif type == "wikiID" {
			return "https://www.wikidata.org/wiki/Special:EntityData/" + id + ".nt"; 
			// this will download a file instead of displaying it 
			// if you want json replace ".nt" with ".json"
		}

		/* UNAVAILABLE */
		// // FAST ID
		// elif type == "fastID" {
		// 	return "http://id.worldcat.org/fast/" + id;

		// }

		// // ISNI ID
		// elif type == "isniID" {
		// 	return "http://www.isni.org/isni/" + id;
		// }

		// OCLC ID 
		else {
			return "http://www.worldcat.org/oclc/" + id + ".nt";
			// if you want in jsonld replace ".nt" with ".jsonld"
		}

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
		String url = generate_URI_query(id);
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
		String url = generate_reconciler_query(input); // input = locID
		String rawJSON = query(url);
		
		
		JSONObject jsonObject = new JSONObject(rawJSON);
		
		
		return "";
	}
}
