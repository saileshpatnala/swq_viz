package org.ECS193.TripleDataProcessor.routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* SUMMARY ENDPOINTS:
		- Library of Congress (LOC)
		- Virtual International Authority File (VIAF)
		- Open Movie Database: IMDB (OMDB)
		- Online Computer Library Center (OCLC) 
		- WikiData - metadata
		- Wiki/DBPedia - metadata
		- DBPedia 
		- UC Davis Library Catalog
		- FAST and ISNI DB won't be queried b/c data not returned in acceptable format
 */
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

	/* NOTE:
		- Generates JSON list of reconciler IDs and author
		- Query on the LOC ID 
		- Change format=json to format=xml if necessary
		- IDs returned: LOC, IMDB, FAST, ISNI, VIAF, WIKI
	*/
	public static String generate_reconciler_query(String loc_id) {

		return "http://query.projectpassage.org/sparql/?query=PREFIX%20passagedt%3A%20<http%3A%2F%2F18.218.102.193%2Fprop%2Fdirect%2F>%0APREFIX%20rdfs%3A%20<http%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23>%0APREFIX%20passagee%3A%20<http%3A%2F%2F18.218.102.193%2Fentity%2F>%0APREFIX%20wikibase%3A%20<http%3A%2F%2Fwikiba.se%2Fontology%23>%0APREFIX%20rdf%3A%20<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0A%0ASELECT%20%3FpersonURL%20%3FviafID%20%3FlocID%20%3FwikiID%20%3Fauthor%20WHERE%20%7B%3FpersonURL%20passagedt%3AP5%20passagee%3AQ7%3B%0A%20passagedt%3AP6%20%3FviafID%3B%20passagedt%3AP105%20%3FlocID%3B%20passagedt%3AP8%20%3FwikiID%3B%20rdfs%3Alabel%20%3Fauthor.%0A%20FILTER%28LANG%28%3Fauthor%29%3D\"en\"%29.%20%0A%20%20FILTER%28REGEX%28STR%28%3FlocID%29%2C\""+loc_id+"\"%29%29.%0A%7D&format=json";
	}


	

	/* VIAF */
	public static String generate_viaf_query(String id) {
		id = id.replaceAll(" ", "%20"); 
		return "http://viaf.org/viaf/" + id + "/viaf.jsonld";
	}

	/* WikiData - metadata */
	public static String generate_wiki_query(String id) {
		id = id.replaceAll(" ", "%20"); 
		return "https://www.wikidata.org/wiki/Special:EntityData/" + id + ".nt"; 
	}


	/* Wiki/DBpedia - metadata */
	public static String generate_wikidbpedia_query(String id) {
		id = id.replaceAll(" ", "%20"); 
		return "http://wikidata.dbpedia.org/data/" + id + ".ntriples";
	}
	

	/* DBpedia */
	public static String generate_dbpedia_query(String id) throws IOException {
		/* STEP 1 - get foaf:name literal from wiki ID */
		id = id.replaceAll(" ", "%20");
		String dbpQuery = "http://dbpedia.org/sparql?query=PREFIX+dbp%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2F%3E%0D%0ASELECT+%3Fperson+WHERE+%7B%3Fperson+dbp%3AdSearch+%3Fid+.+FILTER%28REGEX%28STR%28%3Fid%29%2C+%22" + id + "%22%29%29%7D+&format=application%2Fsparql-results%2Bjson";

		String rawJSON = query(dbpQuery);
		
		JSONObject jsonObject = new JSONObject(rawJSON);
		JSONObject resultObject = jsonObject.getJSONObject("results");

		if (resultObject.getJSONArray("bindings").length() < 1) {
            return "[]";
        }

		JSONObject temp = (JSONObject) resultObject.getJSONArray("bindings").get(0);
		JSONObject temp2 = (JSONObject) temp.get("person");
		String idElement = temp2.get("value").toString();
		List<String> elements = Arrays.asList(idElement.split("/"));
		String name = elements.get(4);
		System.out.println("---- name from DBpedia ------ " + name);

		/* STEP 2 - get n-triples dataset from foaf:name literal */
		return "http://dbpedia.org/data/" + name + ".ntriples ";
	}


	/* LOC */
	public static String generate_lc_query(String id) {
		id = id.replaceAll(" ", "%20"); 
		return "http://id.loc.gov/authorities/names/" + id + ".nt";
	}
	

	/* OCLC */	
	/* NOTE:
	 	- Worldcat.org/oclc ID not given from reconciler
		- Client side initiates query call when it sees worldcat/oclc URI in base library data graph
	 */
	public static String generate_oclc_query(String id) {
		id = id.replaceAll(" ", "%20"); 
		return "http://www.worldcat.org/oclc/" + id + ".nt";
	}
	

	/* OMDB (+IMDB) */
	/* NOTE:
		- OMDB API requires a key, current = 10729f07.
		- Given 1000 query requests per day
		- Set up your own API key on OMDBAPI website if needed.
		- lack of decent, public movie data APIs (IMDB doesn't offer a API, Netflix removed their API, Rotten Tomatoes' API requires an approval process, etc). 
    */
	public static String generate_imdb_query(String id) {
		id = id.replaceAll(" ", "%20"); 
		// THIS QUERY DOES NOT RETURN TRIPLE== DOES NOT WORK
		// return "http://www.omdbapi.com/?i=" + id + "&plot=full&r=json&apikey=10729f07";
		return "";
	}
	

	/* UC Davis Library Catalog */ 
	/* NOTE:
		- initial process, query on: LOC ID -> LIBRARY ID -> URIs
	 */
	public static String generate_library_query(String input) {
		input = "%22"+input.replaceAll(" ", "%20")+"%22";
		String url = 
		"http://localhost:3030/ds/sparql?query=select+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+.+FILTER+(+REGEX(STR(?subject),+" + input + "+)+%7C%7C+REGEX(STR(?predicate),+"+input+"+)+%7C%7C+REGEX(STR(?object),+"+input+"+)+)+}";

		return url;
	}

	/* UC Davis Library Catalog */ 
	/* NOTE:
		- used for d3 graph growth 
	 */
	public static String generate_libraryall_query(String input) {
		input = "%22"+input.replaceAll(" ", "%20")+"%22";
		String url = 
		"http://localhost:3030/ds/sparql?query=select+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+.+FILTER+(+REGEX(STR(?subject),+" + input + "+)+)+}";

		return url;
	}

	/* UC Davis Library Catalog */ 
	/* NOTE:
		- used for d3 graph growth only for "/subjects/" URIs
	 */
	public static String generate_librarysubject_query(String input) {
		input = "%22"+input.replaceAll(" ", "%20")+"%22";
		String url = 
		"http://localhost:3030/ds/sparql?query=select+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+.+FILTER+(+REGEX(STR(?subject),+" + input + "+)+)+}+limit+1";

		return url;
	}
	
	/* NOTE: 
		- Parse JSON array object returned from VIAF DB query
		- Return LOC ID from VIAF return object, used to query UCD library catalog
	 */
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
}
