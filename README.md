# swq_viz_jena

## Description
This branch produces a local semantic web application. Currently, you are able to query library dataset that is persistent on Apache Jena Fuseki's local server. And using postman's get/post request for a specific string input, we are able to view the data via D3.js.

## Environment Setup
* *verify that your environment is set to run on java 8*
* need to have your own local Apache Jena Fuseki with dataset name `ds` and upload file `strict_query_fixed.jsonld` seen under folder data 

## How to run Program
* clone the repo
* set up Apache Jena Fuseki, create dataset name `ds`, upload .jsonld files into Fuseki's UI. You can access their UI via localhost:3030
* cd into `TripleDataProcessor` folder and run `mvn package` then `mvn tomcat:run`. Make sure you're running java 8. 
* go to a browser and type `http://localhost:8080/TripleDataProcessor/webapi/myresource` 

## Expected Output
* should see D3 display based on user given input in search bar 

## In Progress
* We are working to get the project running on library server. 
* querying external SPARQL endpoints
