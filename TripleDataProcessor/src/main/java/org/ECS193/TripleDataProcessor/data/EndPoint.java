package org.ECS193.TripleDataProcessor.data;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.ECS193.TripleDataProcessor.data.TripleElement.TYPE;
import org.json.JSONArray;
import org.json.JSONObject;

public class EndPoint {
	private ArrayList<Triple> triples;
	private ENDPOINT_TYPE type;
	
	public enum ENDPOINT_TYPE
	{
	    wiki, oclc, library, viaf, lcongress, dbpedia;
	}
	
	public EndPoint(String data, ENDPOINT_TYPE type_) {
		this.setTriples(new ArrayList<Triple>());
		this.type = type_;
		if (type == ENDPOINT_TYPE.library || type == ENDPOINT_TYPE.dbpedia) {
			try {
				this.parser(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (type == ENDPOINT_TYPE.lcongress || type == ENDPOINT_TYPE.oclc || type == ENDPOINT_TYPE.wiki ) {
			try {
				this.parserText(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<Triple> getTriples() {
		return triples;
	}

	public void setTriples(ArrayList<Triple> triples) {
		this.triples = triples;
	}
	
	public void addTriple(Triple triple) {
		triples.add(triple);
	}
	
	public void parser(String data) throws Exception {
		JSONObject temp;
        String subject = "";
        String predicate = ""; 
        String object = "";
        TYPE subjectType = null;
        TYPE predicateType = null;
        TYPE objectType = null;
        
        JSONObject jsonObject = new JSONObject(data);
        jsonObject = jsonObject.getJSONObject("results");
        JSONArray triples = jsonObject.getJSONArray("bindings");

        for(int i = 0; i < triples.length(); i++) {
        		temp = (JSONObject) triples.get(i);
        		
        		subject = temp.getJSONObject("subject").get("value").toString();
        		predicate = temp.getJSONObject("predicate").get("value").toString();
        		object = temp.getJSONObject("object").get("value").toString();
        		
        		if(temp.getJSONObject("subject").get("type").toString().equals("literal")) {
        			subjectType = TYPE.literal;
        		}
        		else {
        			subjectType = TYPE.uri;
        		}
        		
        		if(temp.getJSONObject("predicate").get("type").toString().equals("literal")) {
        			predicateType = TYPE.literal;
        		}
        		else {
        			predicateType = TYPE.uri;
        		}
        		
        		if(temp.getJSONObject("object").get("type").toString().equals("literal")) {
        			objectType = TYPE.literal;
        		}
        		else {
        			objectType = TYPE.uri;
        		}
        		
        		System.out.println("Triple " + (i+1) + ": ");
        		System.out.println(subject);
        	    System.out.println(predicate);
        	    System.out.println(object);
        		System.out.println();
        		
        		this.addTriple(new Triple(subject, subjectType, predicate, predicateType, object, objectType));
        }  
    	}
	public void parserText(String data) throws Exception {
        String subject = "";
        String predicate = ""; 
        String object = "";
        TYPE subjectType = null;
        TYPE predicateType = null;
        TYPE objectType = null;
        
        URL link = new URL(data);
		HttpURLConnection httpLink = (HttpURLConnection) link.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(httpLink.getInputStream()));

		String str;
		int i = 0;
		while ((str = in.readLine()) != null) {
		    System.out.println("Triple Line: " + str);
		    String[] splitStr = str.split("\\s+");
		   
		    subject = splitStr[0];
		    predicate = splitStr[1];
		    object = splitStr[2];
		    
		    System.out.println("Triple " + (++i) + ": ");
	    		System.out.println(subject);
	    	    System.out.println(predicate);
	    	    System.out.println(object);
	    		System.out.println();
		
        		this.addTriple(new Triple(subject, subjectType, predicate, predicateType, object, objectType));
		}
		
		
		
	}
}
