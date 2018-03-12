import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Driver {

	public static void main(String[] args) {
		try {
			Data data = new Data(getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getData() throws IOException {
		
		/*
		// Wiki Endpoint
		String url = "http://dbpedia.org/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
		
		// Generic query for all library data
		String url =  "http://localhost:3030/ds/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";

		// Old query for library data with static string
		String url = "http://localhost:3030/feb25wonhee/sparql?query=select+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+.+FILTER+(+REGEX(STR(?subject),+%22Great%20Britain%22)+%7C%7C+REGEX(STR(?predicate),+%22Great%20Britain%22)+%7C%7C+REGEX(STR(?object),+%22Great%20Britain%22)+)+}";
		*/

		// Query for library data based on string or URI
		
		// string example
		String input = "World War";  
		
		// URI example
		//String input = "http://www.worldcat.org/oclc/66131919";  			
		
		input = "%22"+input.replaceAll(" ", "%20")+"%22";
		String url = "http://localhost:3030/march4/sparql?query=select+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+.+FILTER+(+REGEX(STR(?subject),+"+input+"+)+%7C%7C+REGEX(STR(?predicate),+"+input+"+)+%7C%7C+REGEX(STR(?object),+"+input+"+)+)+}";

		// External endpoints
        
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
