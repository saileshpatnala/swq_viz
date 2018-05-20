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
	
	/* NOTE
		- will generate all authors and mapped IDs (above) from reconciler DB. If you want output in xml, get rid of &format=json
		- OUTPUT: JSON data of all IDs 
	*/
	public static String generate_reconciler_query(String loc_id) {
		return "http://query.projectpassage.org/sparql/?query=PREFIX%20passagedt%3A%20%3Chttp%3A%2F%2F18.218.102.193%2Fprop%2Fdirect%2F%3E%0APREFIX%20rdfs%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0APREFIX%20passagee%3A%20%3Chttp%3A%2F%2F18.218.102.193%2Fentity%2F%3E%0APREFIX%20wikibase%3A%20%3Chttp%3A%2F%2Fwikiba.se%2Fontology%23%3E%0APREFIX%20rdf%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0A%0ASELECT%20%3FpersonURL%20%3FviafID%20%3FlocID%20%3FwikiID%20%3FfastID%20%3FisniID%20%3FimdbID%20%3Fauthor%20WHERE%20%7B%3FpersonURL%20passagedt%3AP5%20passagee%3AQ7%3B%0A%20passagedt%3AP6%20%3FviafID%3B%20passagedt%3AP105%20%3FlocID%3B%20passagedt%3AP8%20%3FwikiID%3B%20passagedt%3AP7%20%3FfastID%3B%20passagedt%3AP40%20%3FisniID%3B%20passagedt%3AP116%20%3FimdbID%3B%20rdfs%3Alabel%20%3Fauthor.%0A%20FILTER%28LANG%28%3Fauthor%29%3D%22en%22%20%26%26%20REGEX%28STR%28%3FlocID%29%2C%22"+loc_id+"%22%29%29.%0A%7D&format=json";
	}
	
	public static String generate_viaf_query(String id) {
		id = id.replaceAll(" ", "%20"); 
		return "http://viaf.org/viaf/" + id + "/viaf.jsonld";
	}
	
	public static String generate_wiki_query(String id) {
		// SOURCE: https://www.wikidata.org/wiki/Wikidata:Data_access

		// Wiki ID - meta data 
		id = id.replaceAll(" ", "%20"); 
		return "https://www.wikidata.org/wiki/Special:EntityData/" + id + ".nt"; 
	}

	public static String generate_wikidbpedia_query(String id) {
		// Wiki DBpedia ID - meta data 
		id = id.replaceAll(" ", "%20"); 
		return "http://wikidata.dbpedia.org/data/" + id + ".ntriples";

	}
	
	public static String generate_dbpedia_query(String id) {
		id = id.replaceAll(" ", "%20"); 
		var dbpQuery = "http://dbpedia.org/sparql?query=PREFIX+dbp%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2F%3E%0D%0ASELECT+%3Fperson+WHERE+%7B%3Fperson+dbp%3AdSearch+%3Fid+.+FILTER%28REGEX%28STR%28%3Fid%29%2C+%22" + id + "%22%29%29%7D+&format=application%2Fsparql-results%2Bjson";

		var name = ;

		return "dbpedia.org/data/" + name + ".ntriples ";
	}

	public static String generate_lc_query(String id) {
		// Library of Congress ID
		id = id.replaceAll(" ", "%20"); 
		return "http://id.loc.gov/authorities/names/" + id + ".nt";
	}
		
	/* NOTE:
	 	- Worldcat.org/oclc ID doesn't exist in reconciler DB
		- Client side initiates query call when it sees worldcat/oclc URI in base library data graph
	 */
	public static String generate_oclc_query(String id) {
		// OCLC ID 
		id = id.replaceAll(" ", "%20"); 
		return "http://www.worldcat.org/oclc/" + id + ".nt";
	}
	
	/* NOTE:
		- Wonhee's API key is 10729f07, given 1000 query requests per day
		- Set up your own API key on OMDBAPI website if needed.
		- lack of decent, public movie data APIs (IMDB doesn't offer a API, Netflix removed their API, Rotten Tomatoes' API requires an approval process, etc). 
     */
	public static String generate_imdb_query(String id) {
		// IMDB ID 
		id = id.replaceAll(" ", "%20"); 
		return "http://www.omdbapi.com/?i=" + id + "&plot=full&r=json&apikey=10729f07";
	}
	
	/* NOTE:
		- USUALLY
	 */
	public static String generate_library_query(String input) {
		// Library DS (Jena Fuseki) query
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
	
	/* NOTE: 
		- returning data in n-triples, except for VIAF
		- ISNI ID and FAST id 
	 */
	public static String generate_URI_query(String uri, String type) {
		String id = uri.replaceAll(" ", "%20"); 

		// 	return "http://id.worldcat.org/fast/" + id;

		// 	return "http://www.isni.org/isni/" + id;
		
		return "";

	}
}
