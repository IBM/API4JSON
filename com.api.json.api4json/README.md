# API4JSON
### Version 2.0.2

This library is modeled after the IBM json4j API's. It provides serialization using sorted keys. The goal is to provide a drop in alternative to the com.ibm.json4j-x.x.x.jar file for people wanting the same API structure, but needing to install the jar file in cloud functions or to use with command line utilities using JSON. One minor improvement over the json4j library is, numbers are always treated as Long or Double values for consistency when working with parsed JSON content. Though the put API will accept other Numeric values, they are stored as Long or Double, and retrieved as such. In json4j, if you stored an Integer and used the get API, it would return an Integer. But, if you serialized and then parsed the data, it would change to returning a Long. The same was true in json4j for a Float becoming a Double after parsing serialized content.

I have attempted to perform conversions for unicode characters, and to preserve solidus escape sequences.

For people familiar with the IBM json4j library, simply use this jar and change the import statements from using com.ibm.json.java to com.api.json and everything should work as before.

Developers, please use Eclipse version 2021-12 or later.

Please contact Nathaniel Mills (wnm3@us.ibm.com) with questions.