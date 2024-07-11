# API4JSON
## Version 3.0.1
This library is modeled after the IBM JSON4J APIs. It provides serialization using sorted keys. The goal is to provide a drop-in alternative to the com.ibm.json4j-x.x.x.jar file for people wanting the same API structure but needing to install the jar file in cloud functions or to use with command line utilities using JSON. One minor improvement over the JSON4J library is that numbers are always treated as Long or Double values for consistency when working with parsed JSON content. Though the put API will accept other Numeric values, they are stored as Long or Double and retrieved as such. In json4j, if you stored an Integer and used the get API, it would return an Integer. But, if you serialized and then parsed the data, it would change to returning a Long. The same was true in JSON4J for a Float becoming a Double after parsing serialized content.

I have attempted to perform conversions for Unicode characters and preserve solidus escape sequences.

For people familiar with the IBM json4j library, simply use this jar and change the import statements from com.ibm.json.java to com.api.json, and everything should work as before.

## Development Environment ##

You'll need to set up the following build environment:
  * Java JDK 11 (we've used the IBM Semuru JDK from [this](https://developer.ibm.com/languages/java/semeru-runtimes/downloads/) download site)
  * Install Maven from [this](https://maven.apache.org/download.cgi) download site.  
  * Developers, please use Eclipse version 2024-06 or later.

Please contact Nathaniel Mills (wnm3@us.ibm.com) with questions.
