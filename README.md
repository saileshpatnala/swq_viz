# swq_viz_jena

## Description
This branch produces a local semantic web application. Currently, you are able to query library dataset that is persistent on Apache Jena Fuseki's local server. And using postman's get/post request for a specific string input, we are able to view the raw data in a n-triple form.

## Environment Setup
* *verify that your environment is set to run on java 8*
* need to have your own local Apache Jena Fuseki with dataset name `ds` and upload files linkedData.jsonld seen under folder < > 
* Download Postman to verify get/post requests 

## How to run Program
* clone the repo
* set up Apache Jena Fuseki, create dataset name `ds`, upload .jsonld files into Fuseki's UI. You can access their UI via localhost:3030
* cd into `TripleDataProcessor` folder and run `mvn package` then `mvn tomcat:run`
* on Postman, set URL to `http://localhost:8080/TripleDataProcessor/webapi/myresource` 
	* run POST command, set it's body to `x-www-form-urlencoded` give it's key = `input` and value = any text you want to query (ex: Great Britain)
	* run GET command and you should see data returned on text (e.g. Great Britain) in n-triple form

## Expected Output
* data shows in n-triple form on postman's body after get request

## In Progress
* We are still trying to get get/post request to show on client side instead of on postman raw output. 
* We are working to get the project running on library server. 
