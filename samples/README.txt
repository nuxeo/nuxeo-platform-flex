Content :
---------

This directory contains samples code showing how to use Nuxeo API from flex clients.
All samples include a client (flex) part and a server (java) part.
The server side jar embeds the flex part to add it as a resource to the webapp so you can download the swf into your browser.

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
To deploy the samples, you simply have to copy the sample jars (one per target directory in each sample) into the plugins directory of nuxeo.ear.

Deploy using ant :
 - and deploy

This task uses the build.properties to define the target JBoss/Nuxeo instance.
This task runs the maven build and then copy the jars into the nuxeo.ear.






