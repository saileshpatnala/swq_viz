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
//		String url = "http://dbpedia.org/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+3&format=json";
		String url =  "http://localhost:3030/feb23_modified/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+?object+}+LIMIT+15&format=json";
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
