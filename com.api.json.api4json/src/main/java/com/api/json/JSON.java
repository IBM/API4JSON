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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;

public class JSON implements Serializable {

   private static final long serialVersionUID = 6529512794958410259L;

   // used for serialization spacing (allows program control)
   public static String BLANKS = "   ";
   public static String BLANKSTR = "                                                            ";

   // used to escape solidus (/) as (\/)
   protected static boolean ESCAPE_SOLIDUS = false; // was true in v2.0.1

   // private static final char BSH = '\\';
   private static final char BSP = '\b';

   private static final char CLN = ':';
   private static final char CMA = ',';
   // private static final char CRT = '\r';
   private static final char DQTE = '"';
   private static final char FFD = '\f';
   public static int INCR = BLANKS.length();
   private static final char LBKT = '[';
   private static final char LBRC = '{';
   public static int LN_CNTR = 0;
   public static int LN_OFFSET = 1;
   // private static final char NLN = '\n';
   private static final char RBKT = ']';
   private static final char RBRC = '}';
   private static final char SPC = ' ';
   // private static final char SQTE = '\'';
   private static final char TAB = '\t';

   /**
    * Provides special formatting of embedded characters to enable valid JSON
    * serialization
    * 
    * @param obj
    *           Object to be formatted
    * @return Formatted String representation of the supplied object with
    *         appropriate expanded character sequences.
    */
   protected static String cleanUpString(Object obj) {
      if (obj == null) {
         return null;
      }
      String str = obj.toString();
      /**
       * The following statements are required to output content with
       * appropriate escape sequences for special characters so the
       * corresponding JSON remains valid.
       */
      str = str.replace("\\", "\\\\");
      if (ESCAPE_SOLIDUS) {
         str = str.replace("/", "\\/"); // turn solidus / into escaped \/
      }
      str = str.replaceAll("\"", Matcher.quoteReplacement("\\\""));
      str = str.replace("\n", "\\n");
      str = str.replace("\r", "\\r");
      str = str.replace("\t", "\\t");
      str = expandUnicode(str);
      return str;
   }

   /**
    * Process stream to parse a {@link JSONArray} object
    * 
    * @param jtok
    *           Input stream to be parsed.
    * @param location
    *           Object to track the location (line and column within line) of
    *           the parser with respect to the input stream.
    * @return {@link JSONArray} parsed from the input stream.
    * @throws IOException
    *            If a parsing error occurs.
    */
   private static Object doArray(JSONStreamTokenizer jtok, Integer[] location)
      throws IOException {
      JSONArray array = new JSONArray();
      boolean keepGoing = true;
      while (keepGoing) {
         int tokType = getNextToken(jtok, location);
         switch (tokType) {
            case JSONStreamTokenizer.TT_EOF: {
               throw new IOException("Unterminated object on line "
                  + location[LN_CNTR] + ", column " + location[LN_OFFSET]);
            }
            case JSONStreamTokenizer.TT_CR:
            case JSONStreamTokenizer.TT_EOL: {
               location[LN_CNTR] = location[LN_CNTR] + 1;
               location[LN_OFFSET] = 0;
               break;
            }
            case RBKT: {
               // reached end of array
               keepGoing = false;
               break;
            }
            default: {
               // check for comma delimiter
               doPushBack(jtok, location);
               Object nextObj = recurseParser(jtok, location);
               array.add(nextObj);
               tokType = getNextToken(jtok, location);
               if (tokType == JSONStreamTokenizer.TT_EOF) {
                  break;
               } else if (tokType == JSONStreamTokenizer.TT_EOL || tokType == JSONStreamTokenizer.TT_CR) {
                  location[LN_CNTR] = location[LN_CNTR] + 1;
                  location[LN_OFFSET] = 0;
                  break;
               } else if (tokType == RBRC) {
                  keepGoing = false;
                  break;
               } else if (tokType == RBKT) {
                  doPushBack(jtok, location);
               } else if (tokType != CMA) {
                  throw new IOException("Array missing comma delimiter in line "
                     + location[LN_CNTR] + ", column " + location[LN_OFFSET]);
               }
            }
         }
      }
      return array;
   }

   /**
    * Parses a {@link JSONObject} from the supplied input stream.
    * 
    * @param jtok
    *           Input stream to be parsed.
    * @param location
    *           Object to track the location (line and column within line) of
    *           the parser with respect to the input stream.
    * @return {@link JSONObject} parsed from the input stream.
    * @throws IOException
    *            If a parsing error occurs.
    */
   private static Object doObject(JSONStreamTokenizer jtok, Integer[] location)
      throws IOException {
      JSONObject jobj = new JSONObject();
      boolean keepGoing = true;
      while (keepGoing) {
         int tokType = getNextToken(jtok, location);
         switch (tokType) {
            case JSONStreamTokenizer.TT_EOF: {
               throw new IOException("Underminated object on line "
                  + location[LN_CNTR] + ", column " + location[LN_OFFSET]);
            }
            case JSONStreamTokenizer.TT_CR:
            case JSONStreamTokenizer.TT_EOL: {
               location[LN_CNTR] = location[LN_CNTR] + 1;
               location[LN_OFFSET] = 0;
               break;
            }
            case RBRC: {
               // done with this object
               keepGoing = false;
               break;
            }
            default: {
            	doPushBack(jtok, location);
               Object key = recurseParser(jtok, location);
               if (key == null) {
                  throw new IOException("Expecting string key on line "
                     + location[LN_CNTR] + ", column " + location[LN_OFFSET]);
               }
               if (jtok.ttype == JSONStreamTokenizer.TT_EOF) {
                  break;
               }
               if ((key instanceof String) || //
                  (key instanceof Number) || //
                  (key instanceof Boolean)) {
                  tokType = getNextToken(jtok, location);
                  if (tokType != CLN) {
                     throw new IOException("Expected ':'; found: " + tokType
                        + " on line " + location[LN_CNTR] + ", column "
                        + location[LN_OFFSET]);
                  }
                  // get the corresponding value
                  Object value = recurseParser(jtok, location);
                  jobj.put(key.toString(), value);
                  tokType = getNextToken(jtok, location);
                  if (tokType == JSONStreamTokenizer.TT_EOF) {
                     break;
                  } else if (tokType == JSONStreamTokenizer.TT_EOL || tokType == JSONStreamTokenizer.TT_CR) {
                     location[LN_CNTR] = location[LN_CNTR] + 1;
                     location[LN_OFFSET] = 0;
                     break;
                  } else if (tokType == RBRC) {
                     doPushBack(jtok, location);
                  } else if (tokType != CMA) {
                     throw new IOException(
                        "Missing comma delimiter or an unquoted string value on line " + location[LN_CNTR]
                           + ", near column " + location[LN_OFFSET]);
                  }
               } else {
                  // can't make the key a String
                  throw new IOException("Expecting string key on line "
                     + location[LN_CNTR] + ", column " + location[LN_OFFSET]);
               }
            } // end default
         } // end switch
      } // end while(true)
      return jobj;
   }

   /**
    * Causes the next call to the nextToken method of this tokenizer to return
    * the current value in the ttype field, and not to modify the value in the
    * nval or sval field. Updates the line column location.
    * 
    * @param jtok
    *           Input stream being parsed.
    * @param location
    *           Object to track the location (line and column within line) of
    *           the parser with respect to the input stream.
    */
   private static void doPushBack(JSONStreamTokenizer jtok, Integer[] location) throws IOException {
      jtok.pushBack();
   }

   /**
    * Parses an object from the input stream.
    * 
    * @param jtok
    *           Input stream being parsed.
    * @param location
    *           Object to track the location (line and column within line) of
    *           the parser with respect to the input stream.
    * @return The object parsed from the input stream. Note: Numbers will either
    *         be converted to {@link Long} or {@link Double}.
    */
   private static Object doValue(JSONStreamTokenizer jtok, Integer[] location) {
      // location[LN_OFFSET] = location[LN_OFFSET] + (jtok.sval == null ? 0 : jtok.sval.length());
      if (jtok.ttype == DQTE) {
         return jtok.sval;
      }
      // else could be a pure number or a string
      String test = jtok.sval;
      try {
         Double dTest = Double.valueOf(test);
         return getNumber(dTest, test);
      } catch (Exception e) {
         ; // signifies we have a string or boolean
      }
      if (test.equalsIgnoreCase("true")) {
         return Boolean.TRUE;
      }
      if (test.equalsIgnoreCase("false")) {
         return Boolean.FALSE;
      }
      if (test.equals("null")) {
         return null;
      }
      // else just a word
      return test;
   }

   /**
    * Use to set or unset escaping solidus characters in the output. By default,
    * this is set to true for compatibility.
    * 
    * @param set
    *           If true, solidus is printed as \/ rather than /
    */
   public static void escapeSolidus(boolean set) {
      ESCAPE_SOLIDUS = set;
   }

   /**
    * Expands the input String content to unicode sequences for non ASCII characters
    * 
    * @param input
    *           String to be formatted
    * @return Formatted output String
    */
   protected static String expandUnicode(String input) {
      StringBuilder sb = new StringBuilder();
      char[] chars = input.toCharArray();
      for (int i = 0; i < chars.length; i++) {
         char ch = chars[i];
         if (ch < 0x100) {
            if (ch > 0x1F && ch < 0x7F) {
               sb.append(ch);
            } else {
               if (ch > 0x0f) {
                  sb.append("\\u00"+Integer.toHexString(ch));
               } else {
                  sb.append("\\u000"+Integer.toHexString(ch));
               }
            }
         } else if (ch < 0x1000) {
            sb.append("\\u0" + Integer.toHexString(ch));
         } else {
            sb.append("\\u" + Integer.toHexString(ch));
         }
      }
      return sb.toString();
   }

   /**
    * Parses the next token from the input stream of this tokenizer. The type of
    * the next token is returned in the ttype field. Additional information
    * about the token may be in the nval field or the sval field of this
    * tokenizer.
    *
    * Typical clients of this class first set up the syntax tables and then sit
    * in a loop calling nextToken to parse successive tokens until TT_EOF is
    * returned.
    * 
    * Updates the line column offset.
    * 
    * @param jtok
    *           Input stream being parsed
    * @param location
    *           Object to track the location (line and column within line) of
    *           the parser with respect to the input stream.
    * @return Value of the ttype field describing the next token.
    * @throws IOException
    *            If a parsing error occurs.
    */
   private static int getNextToken(JSONStreamTokenizer jtok, Integer[] location)
      throws IOException {
      skipWhitespace(jtok, location);
      return jtok.nextToken(location);
   }

   /**
    * Parses the number corresponding with the supplied values. Used to ensure
    * either a {@link Long} or a {@link Double} will be used to represent
    * numeric values in JSON.
    * 
    * @param dNum
    *           Double containing the numeric value of the String value
    * @param value
    *           String representation of the value.
    * @return A {@link Long} or a {@link Double} parsed from the supplied
    *         values.
    */
   protected static Object getNumber(Double dNum, String value) {
      // if a decimal point then ensure it remains a double
      if (value.indexOf('.') >= 0) {
         return dNum;
      }
      // turn to Long if no fraction
      Long lNum = dNum.longValue();
      if (dNum - lNum.doubleValue() != 0.0d) {
         return dNum;
      }
      return lNum;
   }

   /**
    * Used to check whether or not the supplied Object can be stored as a valid
    * JSON value. Valid objects are instances of {@link String},
    * {@link Boolean}, {@link Number}, or {@link JSONArtifact}
    * 
    * @param object
    *           Object to be tested
    * @return True if the supplied Object can be stored as a valid JSON value.
    */
   private static boolean isJSONable(Object object) {
      return (object == null ||
         (object instanceof String) || 
         (object instanceof Boolean) ||
         (object instanceof Number) || 
         (object instanceof JSONArtifact));
   }

   /**
    * Used to determine if the supplied Object can be a valid JSON object.
    * 
    * @param object
    *           Object to be tested.
    * @return True if the supplied Object is a valid JSON object.
    */
   public static boolean isValidObject(Object object) {
      return isJSONable(object);
   }

   /**
    * Used to determine in the supplied Class is a valid type for JSON objects.
    * Valid types are: {@link String}, {@link Boolean}, {@link Integer},
    * {@link Double}, {@link Long}, {@link Byte}, {@link Short}, {@link Float},
    * {@link JSONObject}, or {@link JSONArray}
    * 
    * @param clazz
    *           The Class to be tested.
    * @return True if the supplied class is a valid type for JSON objects.
    */
   public static boolean isValidType(Class<?> clazz) {
      if (clazz == null) {
         throw new NullPointerException("Class is null");
      }
      return ((clazz.getName().equals(String.class.getName())) || //
         (clazz.getName().equals(Boolean.class.getName())) || //
         // do the Number classes
         (clazz.getName().equals(Integer.class.getName())) || //
         (clazz.getName().equals(Double.class.getName())) || //
         (clazz.getName().equals(Long.class.getName())) || //
         (clazz.getName().equals(Byte.class.getName())) || //
         (clazz.getName().equals(Short.class.getName())) || //
         (clazz.getName().equals(Float.class.getName())) || //
         // do the JSONArtifact classes
         (clazz.getName().equals(JSONObject.class.getName())) || //
         (clazz.getName().equals(JSONArray.class.getName())));
   }

   /**
    * Parses the supplied input stream to produce either a {@link JSONObject} or
    * a {@link JSONArray}.
    * 
    * @param is
    *           The input stream to be parsed.
    * @return The {@link JSONObject} or the {@link JSONArray} parsed from the
    *         input stream.
    * @throws IOException
    *            If a parsing error occurs.
    */
   public static JSONArtifact parse(InputStream is) throws IOException {
      // note: also UNICODE of the form "\uFFFF" is not allowed in strings
      Reader r = new BufferedReader(
         new InputStreamReader(is, StandardCharsets.UTF_8));
      return parse(r);
   }

   /**
    * Parses the supplied reader to produce either a {@link JSONObject} or a
    * {@link JSONArray}
    * 
    * @param reader
    *           The reader to be parsed.
    * @return The {@link JSONObject} or the {@link JSONArray} parsed from the
    *         reader.
    * @throws IOException
    *            If a parsing error occurs.
    */
   public static JSONArtifact parse(Reader reader) throws IOException {
      JSONStreamTokenizer jtok = new JSONStreamTokenizer(reader);
      Object jobj;
      jtok.resetSyntax();
      // set up word characters (all but special chars above are included)
      jtok.wordChars(';', 'Z'); // ;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ
      jtok.wordChars('^', 'z'); // ^_`abcdefghijklmnopqrstuvwxyz
      jtok.wordChars('|', '|'); // |
      jtok.wordChars('!', '!'); // !
      jtok.wordChars('#', '&'); // #$%&
      jtok.wordChars('~', '~'); // ~
      jtok.wordChars('(', ' '); // ()*+
      jtok.wordChars('-', '9'); // -./0123456789
      // jtok.wordChars(BSH, BSH); // \

      // know when stream ends
      jtok.eolIsSignificant(true);
      jtok.quoteChar(DQTE);
      // Note: we are handling number parsing in doValue
      // jtok.parseNumbers();
      // Note: took out tab so offset can be incremented
      // jtok.whitespaceChars(TAB, TAB); // \t
      jtok.whitespaceChars(FFD, FFD); // \f
      jtok.whitespaceChars(BSP, BSP); // \b
      // Note: took out space so offset can be incremented
      // jtok.whitespaceChars(SPC, SPC); // space
      // one-based counters
      Integer[] location = new Integer[2];
      location[LN_CNTR] = 1;
      location[LN_OFFSET] = 0;
      jobj = recurseParser(jtok, location);
      return (JSONArtifact) jobj;
   }

   /**
    * Parses a supplied String to produce a {@link JSONObject} or a
    * {@link JSONArray}.
    * 
    * @param str
    *           Supplied String to be parsed.
    * @return The parsed {@link JSONObject} or {@link JSONArray}
    * @throws IOException
    *            If a parsing error occurs.
    */
   public static JSONArtifact parse(String str) throws IOException {
      if (str == null) {
         throw new NullPointerException("str cannot be null");
      }
      return parse(new StringReader(str));
   }

   /**
    * Recursive parser to identify JSON keys and values from the input stream.
    * 
    * @param jtok
    *           reader being parsed.
    * @param location
    * @return The next JSON value, or {@link JSONObject}, or {@link JSONArray}
    *         parsed from the reader
    * @throws IOException
    *            If a parsing error occurs.
    */
   private static Object recurseParser(JSONStreamTokenizer jtok, Integer[] location)
      throws IOException {
      int tokType = getNextToken(jtok, location);
      // check what we are starting with, functions continue the work
      switch (tokType) {
         case JSONStreamTokenizer.TT_WORD: {
            return doValue(jtok, location);
         }
         case JSONStreamTokenizer.TT_NUMBER: {
            return doValue(jtok, location);
         }
         case LBKT: {
            return doArray(jtok, location);
         }
         case LBRC: {
            return doObject(jtok, location);
         }
         case DQTE: {
            return doValue(jtok, location);
         }
         case JSONStreamTokenizer.TT_EOF: {
            return null;
         }
         default: {
            throw new IOException("Unexpected character [" + ((char) tokType)
               + "] while scanning JSON String for JSON type.  Invalid JSON. See line "
               + location[LN_CNTR] + ", column " + location[LN_OFFSET]);
         }
      }
   }

   /**
    * Provides a String of blanks for indentation (up to 60 spaces)
    * corresponding to the supplied indentation size.
    * 
    * @param size
    *           the desired indentation size.
    */
   public static void setSpacing(int size) {
      if (size < 1) {
         size = 1;
      } else if (size > 60) {
         size = 60;
      }
      INCR = size;
      BLANKS = BLANKSTR.substring(0, size);
   }

   /**
    * Skips through whitespace in the supplied reader, updating the location
    * object
    * 
    * @param jtok
    *           The reader being parsed
    * @param location
    *           Object to track the location (line and column within line) of
    *           the parser with respect to the input stream.
    * @return The next ttype for a non-whitespace character encountered
    * @throws IOException
    *            If a parsing error occurs.
    */
   private static int skipWhitespace(JSONStreamTokenizer jtok, Integer[] location)
      throws IOException {
      int tokType = jtok.nextToken(location);
      while (tokType == SPC || tokType == TAB) {
         // location[LN_OFFSET] = location[LN_OFFSET] + 1;
         tokType = jtok.nextToken(location); // keep eating spaces
      }
      // back up to prior non-space character
      doPushBack(jtok, location);
      return tokType;
   }
}
