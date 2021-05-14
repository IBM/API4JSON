/**
 * (c) Copyright IBM Corporation 2018
 * 1 New Orchard Road, 
 * Armonk, New York, 10504-1722
 * United States
 * +1 914 499 1900
 * support: wnm3@us.ibm.com
 *
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * License are described here:
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html
 * https://www.gnu.org/licenses/lgpl-3.0.en.html
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
