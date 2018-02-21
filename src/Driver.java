import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Driver {

	public static void main(String[] args) {
		Data data = new Data();
		
		try {
			String jsonOutput = getData();
			data.parser(jsonOutput);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getData() throws IOException {
		String url = "http://dbpedia.org/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+1&format=json";
	        
        URL link = new URL(url);
        HttpURLConnection httpLink = (HttpURLConnection) link.openConnection();
        httpLink.setRequestMethod("GET");
        // httpLink.getResponseProperty("User-Agent", "Mozilla/5.0");
        int responseCode = httpLink.getResponseCode();
        // System.out.println("\nResponse Code" + responseCode);

        BufferedReader in = new BufferedReader( new InputStreamReader(httpLink.getInputStream()));
        String inputLine;
        StringBuffer resp = new StringBuffer();
        while ((inputLine = in.readLine()) != null){
            resp.append(inputLine);
        }


         System.out.println("Link Request Output: " + resp);
	
		
		return null;
		
	}

}
