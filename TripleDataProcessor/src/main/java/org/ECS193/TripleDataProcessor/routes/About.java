package org.ECS193.TripleDataProcessor.routes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

@Path("about")
public class About {
	
	private String searchFilePath = "src/main/webapp/about.html";

	@GET
	@Produces({MediaType.TEXT_HTML})
	public InputStream viewSearch()
	{
		try {
			File f = new File(this.searchFilePath);
			return new FileInputStream(f);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}