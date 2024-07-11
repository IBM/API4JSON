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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class JSONArray extends ArrayList<Object> implements JSONArtifact {

   private static final long serialVersionUID = 8669267182948350538L;

   /**
    * Parses the supplied input stream to derive a {@link JSONArray}
    * 
    * @param is
    *           The input stream to be parsed
    * @return {@link JSONArray} associated with the input stream.
    * @throws IOException
    *            If a parsing error occurs.
    */
   public static JSONArray parse(InputStream is) throws IOException {
      JSONArtifact artifact = JSON.parse(is);
      try {
         return (JSONArray) artifact;
      } catch (ClassCastException cce) {
         throw new IOException("Does not parse as a JSONArray. It is a "
            + artifact.getClass().getName());
      }
   }

   /**
    * Parses the supplied reader to derive a {@link JSONArray}
    * 
    * @param reader
    *           The Reader to be parsed
    * @return {@link JSONArray} associated with the input reader.
    * @throws IOException
    *            If a parsing error occurs.
    */
   public static JSONArray parse(Reader reader) throws IOException {
      JSONArtifact artifact = JSON.parse(reader);
      try {
         return (JSONArray) artifact;
      } catch (ClassCastException cce) {
         throw new IOException("Does not parse as a JSONArray. It is a "
            + artifact.getClass().getName());
      }
   }

   /**
    * Parses the supplied String to derive a {@link JSONArray}
    * 
    * @param input
    *           The String to be parsed
    * @return {@link JSONArray} associated with the input String.
    * @throws IOException
    *            If a parsing error occurs.
    */
   public static JSONArray parse(String input) throws IOException {
      JSONArtifact artifact = JSON.parse(input);
      try {
         return (JSONArray) artifact;
      } catch (ClassCastException cce) {
         throw new IOException("Does not parse as a JSONArray. It is a "
            + artifact.getClass().getName());
      }
   }

   /**
    * Constructor
    */
   public JSONArray() {
      super();
   }

   /**
    * Constructor with initial capacity allocated
    * 
    * @param intialCapacity initial size for the array
    */
   public JSONArray(int intialCapacity) {
      super(intialCapacity);
   }

   /*
    * @see java.util.ArrayList#add(int, java.lang.Object)
    */
   @Override
   public void add(int index, Object element) {
      if (element != null && !JSON.isValidType(element.getClass())) {
         throw new IllegalArgumentException("Invalid type of value.  Type: ["
            + element.getClass().getName() + "] with value: [" + element + "]");
      }
      if (element == this) {
         throw new IllegalArgumentException("Can not put an object into itself.");
      }
      if (element instanceof Number) {
         element = JSON.getNumber(((Number) element).doubleValue(),
            element.toString());
      }

      super.add(index, element);
   }

   /*
    * @see java.util.ArrayList#add(java.lang.Object)
    */
   @Override
   public boolean add(Object element) {
      if (element != null && !JSON.isValidType(element.getClass())) {
         throw new IllegalArgumentException("Invalid type of value.  Type: ["
            + element.getClass().getName() + "] with value: [" + element + "]");
      }
      if (element == this) {
         throw new IllegalArgumentException("Can not put an object into itself.");
      }
      if (element instanceof Number) {
         element = JSON.getNumber(((Number) element).doubleValue(),
            element.toString());
      }
      return super.add(element);
   }

   /*
    * @see java.util.ArrayList#addAll(Collection)
    */
   @Override
   public boolean addAll(Collection<?> collection) {
      Collection<Object> testCol = new ArrayList<>();
      for (Iterator<?> it = collection.iterator(); it.hasNext();) {
         Object element = it.next();
         if (element != null && !JSON.isValidType(element.getClass())) {
            throw new IllegalArgumentException(
               "Invalid type of value.  Type: [" + element.getClass().getName()
                  + "] with value: [" + element + "]");
         }
         if (element == this) {
            throw new IllegalArgumentException("Can not put an object into itself.");
         }
         if (element instanceof Number) {
            element = JSON.getNumber(((Number) element).doubleValue(),
               element.toString());
         }
         testCol.add(element);
      }
      return super.addAll(testCol);
   }

   /*
    * @see java.util.ArrayList#addAll(int, Collection)
    */
   @Override
   public boolean addAll(int index, Collection<?> collection) {
      Collection<Object> testCol = new ArrayList<>();
      for (Iterator<?> it = collection.iterator(); it.hasNext();) {
         Object element = it.next();
         if (element != null && !JSON.isValidType(element.getClass())) {
            throw new IllegalArgumentException(
               "Invalid type of value.  Type: [" + element.getClass().getName()
                  + "] with value: [" + element + "]");
         }
         if (element == this) {
            throw new IllegalArgumentException("Can not put an object into itself.");
         }
         if (element instanceof Number) {
            element = JSON.getNumber(((Number) element).doubleValue(),
               element.toString());
         }
         testCol.add(element);
      }
      return super.addAll(index, testCol);
   }

   /*
    * @see com.api.json.JSONArtifact#serialize()
    */
   @Override
   public String serialize() throws IOException {
      return toString();
   }

   /*
    * @see com.api.json.JSONArtifact#serialize(boolean)
    */
   @Override
   public String serialize(boolean verbose) throws IOException {
      if (verbose) {
         StringBuilder sb = new StringBuilder();
         return toString(sb, 0, JSON.INCR);
      }
      return toString();
   }

   /*
    * @see com.api.json.JSONArtifact#serialize(java.io.OutputStream)
    */
   @Override
   public void serialize(OutputStream os) throws IOException {
      if (os == null) {
         throw new NullPointerException("OutputStream is null.");
      }
      os.write(toString().getBytes(StandardCharsets.UTF_8));
   }

   /*
    * @see com.api.json.JSONArtifact#serialize(java.io.OutputStream,
    *      boolean)
    */
   @Override
   public void serialize(OutputStream os, boolean verbose) throws IOException {
      if (os == null) {
         throw new NullPointerException("OutputStream is null.");
      }
      StringBuilder sb = new StringBuilder();
      if (verbose) {
          os.write(toString(sb, 0, JSON.INCR).getBytes(StandardCharsets.UTF_8));
      } else {
          os.write(toString().getBytes(StandardCharsets.UTF_8));
      }
   }

   /*
    * @see com.api.json.JSONArtifact#serialize(java.io.Writer)
    */
   @Override
   public void serialize(Writer writer) throws IOException {
      if (writer == null) {
         throw new NullPointerException("Writer is null.");
      }
      writer.write(toString());
   }

   /*
    * @see com.api.json.JSONArtifact#serialize(java.io.Writer, boolean)
    */
   @Override
   public void serialize(Writer writer, boolean verbose) throws IOException {
      if (writer == null) {
         throw new NullPointerException("Writer is null.");
      }
      StringBuilder sb = new StringBuilder();
      if (verbose) {
          writer.write(toString(sb, 0, JSON.INCR));
      } else {
          writer.write(toString());
      }
   }

   /*
    * @see java.util.ArrayList#set(int, java.lang.Object)
    */
   @Override
   public Object set(int index, Object element) {
      if (element != null && !JSON.isValidType(element.getClass())) {
         throw new IllegalArgumentException("Invalid type of value.  Type: ["
            + element.getClass().getName() + "] with value: [" + element + "]");
      }
      return super.set(index, element);
   }

   /*
    * @see java.util.AbstractCollection#toString()
    */
   public String toString() {
      StringBuilder sb = new StringBuilder();
      return toString(sb, 0, 0);
   }

   /*
    * @see com.api.json.JSONArtifact#toString(java.lang.StringBuilder, int,
    *      int)
    */
   public String toString(StringBuilder sb, int indent, int incr) {
      boolean newObj = true;
      // depth first search to generate objects and values
      sb.append("[");
      if (incr > 0) {
         sb.append(System.lineSeparator());
      }
      // do indent
      indent += incr;
      for (Iterator<Object> it = iterator(); it.hasNext();) {
         if (newObj) {
            newObj = false;
         } else {
            sb.append(",");
            if (incr > 0) {
               sb.append(System.lineSeparator());
            }
         }
         Object obj = it.next();
         for (int i = 0; i < indent; i += incr) {
            sb.append(JSON.BLANKS);
         }
         if (JSON.isValidObject(obj)) {
            if (obj instanceof String) {
               sb.append("\"" + JSON.cleanUpString(obj) + "\"");
            } else if (obj instanceof JSONArtifact) {
               sb.append(((JSONArtifact) obj).toString(new StringBuilder(),
                  indent, incr));
            } else {
               sb.append(obj);
            }
         }
      }
      // back our way out
      if (incr > 0 && size() > 0) {
         sb.append(System.lineSeparator());
      }
      indent -= incr;
      for (int i = 0; i < indent; i += incr) {
         sb.append(JSON.BLANKS);
      }
      sb.append("]");
      return sb.toString();
   }
}
