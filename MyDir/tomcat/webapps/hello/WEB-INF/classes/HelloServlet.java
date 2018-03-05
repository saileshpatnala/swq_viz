import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.Object;
import java.net.*;

 
public class HelloServlet extends HttpServlet {
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException {
 	Runtime runtime = Runtime.getRuntime();




    PrintWriter out = response.getWriter();
	try {
		 // Process process = runtime.exec("curl -g 'http://dbpedia.org/sparql?query=SELECT+DISTINCT+?concept+WHERE+{+?s+a+?concept+}+LIMIT+50'");

         out.println("<html>");
         out.println("<head><title>Hello, World</title></head>");
         out.println("<body>");
         out.println("<h1>Hello, world!</h1>");  // says Hello
         // Echo client's request information
         out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
         // out.println("<p>Protocol: " + request.getProtocol() + "</p>");
         // out.println("<p>PathInfo: " + request.getPathInfo() + "</p>");

        //String url = "http://dbpedia.org/sparql?query=SELECT+DISTINCT+?concept+WHERE+{+?s+a+?concept+}+LIMIT+15&format=json";
        
        //String url = "http://localhost:3030/feb25wonhee/sparql?query=SELECT+?subject+?predicate+?object+WHERE+{+?subject+?predicate+%22Great%20Britain%22+}&format=json";
        String str = "Great Britain";
        String url = String.format("http://localhost:3030/feb25wonhee/sparql?query=select+?s+?p+?o+WHERE+{+?s+?p+?o+.+FILTER+(+REGEX(STR(?s),+%s)+||+REGEX(STR(?p),+%s)+||+REGEX(STR(?o),+%s)+)+}", str);
        //String input = "Chekhov";
        //String url = "http://viaf.org/viaf/search?query=cql.any+%3D+%22Chekhov%22&httpAccept=application/json";
        //String url =  "http://localhost:3030/feb25wonhee/sparql?query=select+?s+?p+?o+WHERE+{+?s+?p+\"Great Britain\"}";

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


         out.println("<p>Link Request Output: \n" + resp + "</p>");
         // Generate a random number upon each request
         // out.println("<p>A Random Number: <strong>" + Math.random() + "</strong></p>");
         out.println("</body></html>");
      } catch(Throwable cause) {} finally {
         out.close();  // Always close the output writer
      }
}
}
