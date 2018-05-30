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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.BiFunction;

public class JSONObject extends HashMap<String, Object>
         implements JSONArtifact {

   static private final long serialVersionUID = -3778496643896012786L;

   /**
    * Determines whether the supplied object is a valid JSON value
    * 
    * @param object
    *           The object to be tested for validity
    * @return True if the supplied object is a valid JSON value
    */
   static public boolean isValidObject(Object object) {
      return JSON.isValidObject(object);
   }

   /**
    * Determines whether the supplied class is a valid JSON type
    * 
    * @param clazz
    *           The class to be tested for validity
    * @return True if the supplied class is a valid JSON type
    */
   static public boolean isValidType(Class<?> clazz) {
      return JSON.isValidType(clazz);
   }

   /**
    * Parse the supplied input stream to generate a JSONObject
    * 
    * @param is
    *           InputStream to be parsed
    * @return JSONObject parsed from the supplied input stream
    * @throws IOException
    *            if an error occurs reading or parsing the input stream
    */
   static public JSONObject parse(InputStream is) throws IOException {
      return (JSONObject) JSON.parse(is);
   }

   /**
    * Parse the supplied reader to generate a JSONObject
    * 
    * @param reader
    *           Reader to be parsed
    * @return JSONObject parsed from the supplied reader
    * @throws IOException
    *            if an error occurs reading or parsing the reader
    */
   static public JSONObject parse(Reader reader) throws IOException {
      return (JSONObject) JSON.parse(reader);
   }

   /**
    * Parse the supplied input to generate a JSONObject
    * 
    * @param input
    *           InputStream to be parsed
    * @return JSONObject parsed from the supplied input
    * @throws IOException
    *            if an error occurs reading or parsing the input
    */
   static public JSONObject parse(String input) throws IOException {
      return (JSONObject) JSON.parse(input);
   }

   /**
    * Constructor
    */
   public JSONObject() {
      super();
   }

   /**
    * Provides merger of the supplied key and value according to logic ni the
    * supplied remappingFunction
    * 
    * @param key
    *           The key of the object to be merged
    * @param value
    *           The value of the object to be merged
    * @param remappingFunction
    *           The function to control how merger is accomplished
    * @return The resulting object after merger
    */
   public Object merge(String key, Object value,
      BiFunction<Object, Object, Object> remappingFunction) {
      if (key == null) {
         throw new IllegalArgumentException("key must not be null");
      }
      if (!(key instanceof String)) {
         throw new IllegalArgumentException("key must be a String");
      }
      if (value != null && isValidType(value.getClass()) == false) {
         throw new IllegalArgumentException("Invalid type of value.  Type: ["
            + value.getClass().getName() + "] with value: [" + value + "]");
      }
      if (value instanceof Number) {
         value = JSON.getNumber(((Number) value).doubleValue(),
            value.toString());
      }
      return super.merge(key, value, remappingFunction);
   }

   /**
    * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
    */
   @Override
   public Object put(String key, Object value) {
      if (key == null) {
         throw new IllegalArgumentException("key must not be null");
      }
      if (!(key instanceof String)) {
         throw new IllegalArgumentException("key must be a String");
      }
      if (value == this) {
         throw new IllegalArgumentException("Can not put an object into itself.");
      }
      if (value != null && isValidType(value.getClass()) == false) {
         throw new IllegalArgumentException("Invalid type of value.  Type: ["
            + value.getClass().getName() + "] with value: [" + value + "]");
      }
      if (value instanceof Number) {
         value = JSON.getNumber(((Number) value).doubleValue(),
            value.toString());
      }
      return super.put(key == null ? null : key.toString(), value);
   }

   /**
    * @see java.util.HashMap#putAll(java.util.Map)
    */
   @Override
   public void putAll(@SuppressWarnings("rawtypes") Map m) {
      for (Iterator<?> it = m.keySet().iterator(); it.hasNext();) {
         Object key = it.next();
         if (key == null) {
            throw new IllegalArgumentException("key must not be null");
         }
         if (!(key instanceof String)) {
            throw new IllegalArgumentException("key must be a String");
         }
         Object value = m.get(key);
         if (value != null && isValidType(value.getClass()) == false) {
            throw new IllegalArgumentException("Invalid type of value.  Type: ["
               + value.getClass().getName() + "] with value: [" + value + "]");
         }
         if (value instanceof Number) {
            value = JSON.getNumber(((Number) value).doubleValue(),
               value.toString());
         }
         super.put(key == null ? null : key.toString(), value);
      }
   }

   /**
    * @see java.util.HashMap#putIfAbsent(java.lang.Object, java.lang.Object)
    */
   @Override
   public Object putIfAbsent(String key, Object value) {
      if (key == null) {
         throw new IllegalArgumentException("key must not be null");
      }
      if (!(key instanceof String)) {
         throw new IllegalArgumentException("key must be a String");
      }
      if (value != null && isValidType(value.getClass()) == false) {
         throw new IllegalArgumentException("Invalid type of value.  Type: ["
            + value.getClass().getName() + "] with value: [" + value + "]");
      }
      if (value instanceof Number) {
         value = JSON.getNumber(((Number) value).doubleValue(),
            value.toString());
      }
      Object test = get(key);
      if (test != null) {
         return test;
      }
      super.put(key == null ? null : key.toString(), value);
      return null;
   }

   /**
    * @see java.util.HashMap#replace(java.lang.Object, java.lang.Object)
    */
   @Override
   public Object replace(String key, Object value) {
      if (key == null) {
         throw new IllegalArgumentException("key must not be null");
      }
      if (!(key instanceof String)) {
         throw new IllegalArgumentException("key must be a String");
      }
      if (value != null && isValidType(value.getClass()) == false) {
         throw new IllegalArgumentException("Invalid type of value.  Type: ["
            + value.getClass().getName() + "] with value: [" + value + "]");
      }
      if (containsKey(key) == false) {
         return null;
      }
      Object test = get(key);
      // there is something to replace
      if (value instanceof Number) {
         value = JSON.getNumber(((Number) value).doubleValue(),
            value.toString());
      }
      super.put(key == null ? null : key.toString(), value);
      return test;
   }

   /**
    * @see java.util.HashMap#replace(java.lang.Object, java.lang.Object,
    *      java.lang.Object)
    */
   @Override
   public boolean replace(String key, Object oldValue, Object value) {
      if (key == null) {
         throw new IllegalArgumentException("key must not be null");
      }
      if (!(key instanceof String)) {
         throw new IllegalArgumentException("key must be a String");
      }
      if (value != null && isValidType(value.getClass()) == false) {
         throw new IllegalArgumentException("Invalid type of value.  Type: ["
            + value.getClass().getName() + "] with value: [" + value + "]");
      }
      if (containsKey(key) == false) {
         return false;
      }
      Object test = get(key);
      if (test == null) {
         if (oldValue != null) {
            return false;
         }
      }
      if (test.equals(oldValue) == false) {
         return false;
      }
      if (value instanceof Number) {
         value = JSON.getNumber(((Number) value).doubleValue(),
            value.toString());
      }
      super.put(key == null ? null : key.toString(), value);
      return true;
   }

   /**
    * @see com.api.json.JSONArtifact#serialize()
    */
   @Override
   public String serialize() throws IOException {
      return toString();
   }

   /**
    * @see com.api.json.JSONArtifact#serialize(boolean)
    */
   @Override
   public String serialize(boolean verbose) throws IOException {
      if (verbose) {
         StringBuffer sb = new StringBuffer();
         return toString(sb, 0, JSON.INCR);
      }
      return toString();
   }

   /**
    * @see com.api.json.JSONArtifact#serialize(java.io.OutputStream)
    */
   @Override
   public void serialize(OutputStream os) throws IOException {
      if (os == null) {
         throw new NullPointerException("OutputStream is null.");
      }
      os.write(toString().getBytes("UTF8"));
      return;
   }

   /**
    * @see com.api.json.JSONArtifact#serialize(java.io.OutputStream, boolean)
    */
   @Override
   public void serialize(OutputStream os, boolean verbose) throws IOException {
      if (os == null) {
         throw new NullPointerException("OutputStream is null.");
      }
      StringBuffer sb = new StringBuffer();
      os.write(toString(sb, 0, JSON.INCR).getBytes(StandardCharsets.UTF_8));
      return;
   }

   /**
    * @see com.api.json.JSONArtifact#serialize(java.io.Writer)
    */
   @Override
   public void serialize(Writer writer) throws IOException {
      if (writer == null) {
         throw new NullPointerException("Writer is null.");
      }
      writer.write(toString());
      return;
   }

   /**
    * @see com.api.json.JSONArtifact#serialize(java.io.Writer, boolean)
    */
   @Override
   public void serialize(Writer writer, boolean verbose) throws IOException {
      if (writer == null) {
         throw new NullPointerException("Writer is null.");
      }
      StringBuffer sb = new StringBuffer();
      writer.write(toString(sb, 0, JSON.INCR));
      return;
   }

   /**
    * @return An unformatted rendering of this {@link JSONObject}
    */
   public String toString() {
      StringBuffer sb = new StringBuffer();
      return toString(sb, 0, 0);
   }

   /**
    * @see com.api.json.JSONArtifact#toString(java.lang.StringBuffer, int, int)
    */
   public String toString(StringBuffer sb, int indent, int incr) {
      boolean newObj = true;
      // depth first search to generate objects and values
      sb.append("{");
      // do indent
      indent += incr;
      TreeSet<Object> keyTree = new TreeSet<Object>();
      keyTree.addAll(keySet());
      for (Iterator<Object> it = keyTree.iterator(); it.hasNext();) {
         if (newObj) {
            newObj = false;
         } else {
            sb.append(",");
         }
         if (incr > 0) {
            sb.append(System.lineSeparator());
         }
         Object key = it.next();
         for (int i = 0; i < indent; i += incr) {
            sb.append(JSON.BLANKS);
         }
         sb.append("\"" + key + "\":");
         if (incr > 0) {
            sb.append(" ");
         }
         Object obj = get(key);
         if (JSON.isValidObject(obj)) {
            if (obj instanceof String) {
               sb.append("\"" + JSON.cleanUpString(obj) + "\"");
            } else if (obj instanceof JSONArtifact) {
               // this is a new JSONArtifact
               sb.append(((JSONArtifact) obj).toString(new StringBuffer(),
                  indent, incr));
            } else {
               sb.append(obj);
            }
         }
      }
      // back our way out
      if (incr > 0) {
         sb.append(System.lineSeparator());
      }
      indent -= incr;
      for (int i = 0; i < indent; i += incr) {
         sb.append(JSON.BLANKS);
      }
      sb.append("}");
      return sb.toString();
   }
}
