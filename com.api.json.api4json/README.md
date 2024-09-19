# API4JSON
### Version 4.0.0

There was a bug for unicode with single hex digits that was fixed. Because this was generating bad data I thought it important to release a new major version. The bug was turning \u000b into \u00011 for example.

Switched from Java 1.8 to Java 11

The license for this version of the code has changed from LGPL to Apache 2.0, as described in the LICENSE.TXT file and the copyright comments in the code. For historical purposes, the older LGPL-licensed version will remain at 2.0.1.

This library is modeled after the IBM JSON4J APIs. It provides serialization using sorted keys. The goal is to provide a drop-in alternative to the com.ibm.json4j-x.x.x.jar file for people wanting the same API structure but needing to install the jar file in cloud functions or to use with command line utilities using JSON. One minor improvement over the JSON4J library is that numbers are always treated as Long or Double values for consistency when working with parsed JSON content. Though the put API will accept other Numeric values, they are stored as Long or Double and retrieved as such. In json4j, if you stored an Integer and used the get API, it would return an Integer. But, if you serialized and then parsed the data, it would change to returning a Long. The same was true in JSON4J for a Float becoming a Double after parsing serialized content.

I have attempted to perform conversions for Unicode characters and preserve solidus escape sequences.

For people familiar with the IBM json4j library, simply use this jar and change the import statements from com.ibm.json.java to com.api.json, and everything should work as before.

## Development Environment ##

You'll need to set up the following build environment:
  * Java JDK 11 (we've used the IBM Semuru JDK from [this](https://developer.ibm.com/languages/java/semeru-runtimes/downloads/) download site)
  * Install Maven from [this](https://maven.apache.org/download.cgi) download site.  
  * Developers, please use Eclipse version 2024-06 or later.


## Building Jar Files ##
Each directory under RAG-Data-Pipeline is a Maven project that uses a pom.xml file to define dependencies and the build process.  All the Java-based Maven projects may be built from a command line using the command executed from within the project's directory:
```
mvn clean install
```

Please contact Nathaniel Mills (wnm3@us.ibm.com) with questions.