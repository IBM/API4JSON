/**
 * (c) Copyright 2018-2023 IBM Corporation
 * 1 New Orchard Road, 
 * Armonk, New York, 10504-1722
 * United States
 * +1 914 499 1900
 * Nathaniel Mills wnm3@us.ibm.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.api.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Interface class to define a set of generic apis both JSONObject and JSON
 * array implement. This is namely so that functions such as serialize, which
 * are common between the two, can be easily invoked.
 */
public interface JSONArtifact {

   /**
    * Convert this object into a String of JSON text.
    * 
    * @return String containing the JSON formatted according to the verbose
    *         flag.
    * @throws IOException
    *            Thrown on IO errors during serialization.
    */
   String serialize() throws IOException;

   /**
    * 
    * @param verbose
    *           Whether or not to write the JSON text in a verbose format.
    * @return String containing the JSON formatted according to the verbose
    *         flag.
    * @throws IOException
    *            Thrown on IO errors during serialization.
    */
   String serialize(boolean verbose) throws IOException;

   /**
    * Convert this object into a stream of JSON text. Same as calling
    * serialize(os,false); Note that encoding is always written as UTF-8, as per
    * JSON spec.
    * 
    * @param os
    *           The output stream to serialize data to.
    * @throws IOException
    *            Thrown on IO errors during serialization.
    */
   void serialize(OutputStream os) throws IOException;

   /**
    * Convert this object into a stream of JSON text. Same as calling
    * serialize(writer,false); Note that encoding is always written as UTF-8, as
    * per JSON spec.
    * 
    * @param os
    *           The output stream to serialize data to.
    * @param verbose
    *           Whether or not to write the JSON text in a verbose format.
    * @throws IOException
    *            Thrown on IO errors during serialization.
    */
   void serialize(OutputStream os, boolean verbose) throws IOException;

   /**
    * Convert this object into a stream of JSON text. Same as calling
    * serialize(writer,false);
    * 
    * @param writer
    *           The writer which to serialize the JSON text to.
    * @throws IOException
    *            Thrown on IO errors during serialization.
    */
   void serialize(Writer writer) throws IOException;

   /**
    * Convert this object into a stream of JSON text, specifying verbosity.
    * 
    * @param writer
    *           The writer which to serialize the JSON text to.
    * @param verbose
    *           Whether or not to write the JSON text in a verbose format.
    * @throws IOException
    *            Thrown on IO errors during serialization.
    */
   void serialize(Writer writer, boolean verbose) throws IOException;

   /**
    * Abstract method to help with recursive formatting of JSON into a String
    * 
    * @param sb
    *           Buffer to receive formatted JSON content
    * @param indent
    *           The amount of indentation expected in the formatted output.
    * @param incr
    *           the amount of incremental spacing used for formatted output.
    * @return Formatted JSON output.
    */
   abstract String toString(StringBuilder sb, int indent, int incr);
}
