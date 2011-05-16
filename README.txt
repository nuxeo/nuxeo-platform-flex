Content :
---------

This directory contains :
 - flex-server-connector 
   the Nuxeo EP plugin that exposes Nuxeo API to flex clients using AMF protocol
 - nuxeo-flex-components
   the AS SWC that contains helpers and objects necessary to exchange data with Nuxeo server
 - samples
   some sample code

For the samples, please see the README in the samples directory.

Building :
----------

All build is managed by maven for the java part and for the swf part.
For convenience, ant goals are also provided:

Launch build using maven :
 - mvn clean install

Launch build using ant :
 - ant install


Deploy to Nuxeo server :
------------------------
To deploy the connector, you simply have to copy the flex-server-connector jar into the plugins directory of nuxeo.ear.

Deploy using ant :
 - and deploy

This task uses the build.properties to define the target JBoss/Nuxeo instance.
This task runs the maven build and then copy the jar into the nuxeo.ear.

Finally copy the jars below under nuxeo.ear/libi (jboss) or nxserver/lib (tomcat):
 - granite-core-1.1.0.GA.jar
 - granite-seam-1.1.0.GA.jar
