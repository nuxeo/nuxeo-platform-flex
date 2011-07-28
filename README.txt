Content :
---------

This directory contains :

 - nuxeo-flex-connector
   The Nuxeo EP plugin that exposes Nuxeo API to flex clients using AMF protocol

 - nuxeo-flex-components and nuxeo-flex-components-extra
   The AS SWC that contains helpers and objects necessary to exchange data with Nuxeo server

 - samples
   This directory contains sample implementations showing how to use Nuxeo API from Flex clients.
   All samples include a client (Flex) part and a server (Java) part.
   The server side jar embeds the Flex part to add it as a resource to the webapp so you can download the SWF into your browser.

 - nuxeo-flex-distribution
   A sample distribution based on Nuxeo CAP and all Flex samples


Building :
----------

You need Maven 2.2.1.

 - Build the nuxeo-flex-connector:
   mvn package -Pconnector

 - Build the samples:
   mvn package -Psamples

 - Full build (including the sample distribution):
   mvn package -Pall


Deploy to Nuxeo server :
------------------------

  * Automatic deployment (recommended):

    Look at the nuxeo-flex-distribution module and create your own distribution module
    for automatically packaging Nuxeo with the Flex connector and other required modules.

    Such a method ensures there will be no missing libraries.

  * Manual deployment:

    To manually deploy the connector, you have to copy the jar from nuxeo-flex-connector/target/
    into the plugins directory of your Nuxeo server:
     - $NUXEO_HOME/server/default/deploy/nuxeo.ear/plugins/ (JBoss)
     - $NUXEO_HOME/nxserver/plugins/ (Tomcat)

    And to copy at least the two Granite libraries "granite-core" and "granite-seam" into:
     - $NUXEO_HOME/server/default/deploy/nuxeo.ear/lib/ (JBoss)
     - $NUXEO_HOME/nxserver/lib/ (Tomcat)

    Note you may need to add other third-party libraries.

Deploy the samples to Nuxeo server :
------------------------------------
    Jar files go into Nuxeo plugins directory:
     - $NUXEO_HOME/server/default/deploy/nuxeo.ear/plugins/ (JBoss)
     - $NUXEO_HOME/nxserver/plugins/ (Tomcat)
