package org.ECS193.TripleDataProcessor.routes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class Conf {

	Map<String, String> endpointMapping;
	String fileName = "conf.yml";
	ArrayList<String> key = new ArrayList<String>();
    ArrayList<String> value = new ArrayList<String>();
	
    public Conf() {
		loadYaml();
	}
	
	void loadYaml() {
		Yaml yaml = new Yaml();

	    try {
	        InputStream ios = new FileInputStream(new File(fileName));

	        // Parse the YAML file and return the output as a series of Maps and Lists
	        endpointMapping = (Map< String, String>) yaml.load(ios);
	        for (Object name : endpointMapping.keySet()) {   

	            key.add(name.toString());
	            value.add(endpointMapping.get(name).toString());    
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
	    System.out.println(key + " : " + value); 

	}
}
