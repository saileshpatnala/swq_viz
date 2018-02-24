# swq_viz_jena Documentation

## Installation
[link](https://www.ntu.edu.sg/home/ehchua/programming/howto/Tomcat_HowTo.html)

## Running Set Up with Java Servelet
1. First go to directory where servlet-api.jar exists, in this case `cd /HOME/swq_viz_jena/MyDir/tomcat/webapps/hello/WEB-INF/classes`
1. Then start the java servelet `javac -cp .:"/HOME/swq_viz_jena/MyDir/tomcat/lib/servlet-api.jar" HelloServlet.java`
1. To start TomCat Server, go into the following directory: 'MyDir/tomcat/bin'
1. And then use the following start command: `./catalina.sh run`
1. Go to web browser and type `localhost:9999/hello/sayhello`
1. To Stop Server: Control + c

## Set Up with Java Servelet with Apache Jena Fuseki SPARQL endpoint
1. Launch fuseki server (directions in other repo)
1. go to info section of specific dataset you want sparql endpoint to query.
1. copy URL of SPARQL Query, ex: `http://localhost:3030/feb23/sparql` into Java Servelet example and run SPARQL query
