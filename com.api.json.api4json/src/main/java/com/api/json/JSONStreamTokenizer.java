/**
 * (c) Copyright IBM Corporation 2018
 * 1 New Orchard Road, 
 * Armonk, New York, 10504-1722
 * United States
 * +1 914 499 1900
 * support: wnm3@us.ibm.com
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
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * This class provides similar services to {@link StreamTokenizer} but it works
 * on a character by character basis, whereas the StreamTokenizer is based on
 * individual bytes so is not handling solidus nor unicode correctly. This class
 * is solely for parsing JSON content.
 */
public class JSONStreamTokenizer {

   // character table flags
   private static final int _IS_ORDINARY = 0x0000;
   private static final int _IS_NUMERIC = 0x0001;
   private static final int _IS_QUOTE = 0x0002;
   private static final int _IS_WHITESPACE = 0x0004;
   private static final int _IS_WORD = 0x008;

   // character constants
   private static final int BSH = '\\';
   private static final int BSP = '\b';
   private static final int CRT = (int) '\r';
   private static final int DPT = (int) '.';
   private static final int DQTE = (int) '"';
   private static final int EOF = -1;
   private static final int FFD = (int) '\f';
   private static final int HYP = (int) '-';
   private static final int NLN = (int) '\n';
   private static final int SPC = (int) ' ';
   private static final int SQTE = (int) '\'';
   private static final int TAB = (int) '\t';

   // text type references
   public static final int TT_EOF = -1;
   public static final int TT_CR = '\r';
   public static final int TT_EOL = '\n';
   public static final int TT_NUMBER = -2;
   private static final int TT_UNKNOWN = -4;
   public static final int TT_WORD = -3;

   // state trackers
   boolean _atEOL = false;
   boolean _eolSignificant = false;
   boolean _isPushedBack = false;
   boolean _lowerCaseMode = false;
   int _nextChar = -1;

   PushbackReader _reader = null;
   StringBuilder _currentValue = new StringBuilder();
   int[] _charTable = new int[65536];

   // compatibility variables
   public double nval = 0.0d;
   public String sval = null;
   public int ttype = TT_UNKNOWN;
   
   public static int LN_CNTR = 0;
   public static int LN_OFFSET = 1;

   /**
    * Constructs the JST from a {@link java.io.Reader}. There is no constructor for
    * an {@link java.io.InputStream} because it can be made into a reader. The same
    * is true for a {@link String}.
    * 
    * @param r
    *          the reader to be read containing JSON content.
    */
   public JSONStreamTokenizer(Reader r) {
      resetSyntax();
      whitespaceChars(0x0000, SPC);
      wordChars('A', 'Z');
      wordChars('a', 'z');
      wordChars(0x00A0, 0x00FF);
      parseNumbers();
      quoteChar(SQTE);
      quoteChar(DQTE);
      _reader = new PushbackReader(r, 2);
   }

   /**
    * Determine if we have reached an end of file condition.
    * 
    * @return true if end of file has been reached.
    */
   private boolean checkEOF() {
      if (_nextChar == EOF) {
         // reached end of file
         ttype = TT_EOF;
         return true;
      }
      return false;
   }

   /**
    * Determine if we are parsing a number
    * 
    * @param location line number and line offset values
    * @return true if we are parsing a number
    * @throws IOException
    *                     if unable to read from the reader
    */
   private boolean checkForNumber(Integer[] location) throws IOException {
      if ((_charTable[_nextChar] & _IS_NUMERIC) != 0) {
         if (_nextChar == HYP) {
            _nextChar = readNextChar();
            location[LN_OFFSET] = location[LN_OFFSET]+1;
            // if next character isn't a number
            if ((_charTable[_nextChar] & _IS_NUMERIC) == 0) {
               // return hyphen as part of a string
               _reader.unread(_nextChar);
               if (location[LN_OFFSET] > 0) {
                  location[LN_OFFSET] = location[LN_OFFSET]-1;
               }
               ttype = HYP;
               return true;
            }
            // else we have started a negative number
            _currentValue.append((char) HYP);
         }
         // keep looking to create the number
         int decimalCount = 0;
         while (true) {
            // accumulate digits while looking for decimal point
            _currentValue.append((char) _nextChar);
            _nextChar = readNextChar();
            location[LN_OFFSET] = location[LN_OFFSET]+1;
            if (_nextChar == EOF) {
               _reader.unread(_nextChar);
               if (location[LN_OFFSET] > 0) {
                  location[LN_OFFSET] = location[LN_OFFSET]-1;
               }
               try {
                  nval = new Double(_currentValue.toString());
                  _currentValue.setLength(0);
                  ttype = TT_NUMBER;
                  return true;
               } catch (NumberFormatException nfe) {
                  // this is part of a word starting with digits
                  break;
               }
            }
            // if this is not a number
            if ((_charTable[_nextChar] & _IS_NUMERIC) == 0 && _nextChar != HYP) {
               _reader.unread(_nextChar);
               if (location[LN_OFFSET] > 0) {
                  location[LN_OFFSET] = location[LN_OFFSET]-1;
               }
               try {
                  nval = new Double(_currentValue.toString());
                  _currentValue.setLength(0);
                  ttype = TT_NUMBER;
                  return true;
               } catch (NumberFormatException nfe) {
                  ; // fall through
               }
               // this is part of a word starting with digits
               break;
            }
            if (_nextChar == DPT) {
               decimalCount++;
            }
            if (decimalCount > 1) {
               // this is part of a word with multiple decimal points
               _reader.unread(_nextChar);
               if (location[LN_OFFSET] > 0) {
                  location[LN_OFFSET] = location[LN_OFFSET]-1;
               }
               break;
            }
            // keep accumulating digits and decimal point
            _currentValue.append((char) _nextChar);
         } // end while is digit
      }
      return false;
   }

   /**
    * Determine if we have reached quoted content
    * 
    * @param location line number and line offset
    * @return true if we have reached quoted content
    * @throws IOException
    *                     if unable to read from the reader
    */
   private boolean checkForQuotedWord(Integer[] location) throws IOException {
      if ((_charTable[_nextChar] & _IS_QUOTE) != 0) {
         ttype = _nextChar; // save quote
         int _lookAhead = readNextChar();
         location[LN_OFFSET] = location[LN_OFFSET]+1;
         // process quoted string, addressing escaped characters and octal
         // codes
         while (_lookAhead != ttype && _lookAhead != NLN && _lookAhead != CRT && _lookAhead != EOF) {
            // handle escaped content
            if (_lookAhead == BSH) {
               _nextChar = readNextChar();
               location[LN_OFFSET] = location[LN_OFFSET]+1;
               // process other escaped characters
               switch (_nextChar) {
               case 't': {
                  _nextChar = TAB;
                  break;
               }
               case 'n': {
                  _nextChar = NLN;
                  break;
               }
               case 'r': {
                  _nextChar = CRT;
                  break;
               }
               case 'f': {
                  _nextChar = FFD;
                  break;
               }
               case 'b': {
                  _nextChar = BSP;
                  break;
               }
               case BSH: {
                  _nextChar = BSH;
                  break;
               }
               case DQTE: {
                  _nextChar = DQTE;
                  break;
               }
               case SQTE: {
                  _nextChar = SQTE;
                  break;
               }
               case 'u': {
                  // part of unicode so need to read next 4 digits
                  int d1, d2, d3, d4 = 0;
                  d1 = readNextChar();
                  location[LN_OFFSET] = location[LN_OFFSET]+1;
                  if (isHexChar(d1)) {
                     d2 = readNextChar();
                     location[LN_OFFSET] = location[LN_OFFSET]+1;
                     if (isHexChar(d2)) {
                        d3 = readNextChar();
                        location[LN_OFFSET] = location[LN_OFFSET]+1;
                        if (isHexChar(d3)) {
                           d4 = readNextChar();
                           location[LN_OFFSET] = location[LN_OFFSET]+1;
                           if (isHexChar(d4)) {
                              char[] cBuf = new char[4];
                              cBuf[0] = (char) d1;
                              cBuf[1] = (char) d2;
                              cBuf[2] = (char) d3;
                              cBuf[3] = (char) d4;
                              String test = new String(cBuf);
                              if (test.equalsIgnoreCase("000a")) {
                                 _nextChar = NLN;
                              } else {
                                 _nextChar = (char) Integer.parseInt(test, 16);
                              }
                           } // end 4th digit
                        } // end 3rd digit
                     } // end 2nd digit
                  } // end 1st digit
                  break;
               } // end unicode parsing
               default: {
                  break;
               }
               } // end switch on escaped character
               _lookAhead = readNextChar();
               location[LN_OFFSET] = location[LN_OFFSET]+1;
            } else { // end dealing with escaped value
               _nextChar = _lookAhead;
               _lookAhead = readNextChar();
               location[LN_OFFSET] = location[LN_OFFSET]+1;
            }
            _currentValue.append((char) _nextChar);
         } // end while looking for matching quote or EOL
         if (_lookAhead != ttype) {
            // hit EOL, not matching quote
            _reader.unread(_lookAhead);
            if (location[LN_OFFSET] > 0) {
               location[LN_OFFSET] = location[LN_OFFSET]-1;
            }
         }
         sval = _currentValue.toString();
         if (_lowerCaseMode) {
            sval = sval.toLowerCase();
         }
         return true;
      }
      return false;
   }

   /**
    * Determine if we have reached a word. Note: this is used for detecting boolean
    * and null values.
    * 
    * @param location line number and line offset
    * @return true if we find unquoted words
    * @throws IOException
    *                     if unable to read from the reader
    */
   private boolean checkForWord(Integer[] location) throws IOException {
      if ((_charTable[_nextChar] & _IS_WORD) != 0) {
         // keep reading until we hit EOF, whitespace
         while ((_charTable[_nextChar] & (_IS_NUMERIC | _IS_WORD)) != 0) {
            _currentValue.append((char) _nextChar);
            _nextChar = readNextChar();
            location[LN_OFFSET] = location[LN_OFFSET]+1;
            if (_nextChar == EOF) {
               // reached end of file
               _reader.unread(_nextChar);
               if (location[LN_OFFSET] > 0) {
                  location[LN_OFFSET] = location[LN_OFFSET]-1;
               }
               sval = _currentValue.toString();
               _currentValue.setLength(0);
               ttype = TT_WORD;
               return true;
            }
         }
         _reader.unread(_nextChar);
         if (location[LN_OFFSET] > 0) {
            location[LN_OFFSET] = location[LN_OFFSET]-1;
         }
         sval = _currentValue.toString();
         _currentValue.setLength(0);
         if (_lowerCaseMode) {
            sval = sval.toLowerCase();
         }
         ttype = TT_WORD;
         return true;
      }
      return false;
   }

   /**
    * Sets reporting when end of line is detected
    * 
    * @param flag
    *             whether or not to report when end of line is detected
    */
   public void eolIsSignificant(boolean flag) {
      _eolSignificant = flag;

   }

   /**
    * Handles an encountered end of line that can be signaled as /r, /r/n, or /n
    * 
    * @param location
    *                 where we are in the input stream
    * @return true if an end of line was processed
    * @throws IOException
    *                     if unable to read from the reader
    */
   boolean handleEndOfLine(Integer[] location) throws IOException {
      if (_nextChar == CRT) {
         // skip /n if there is one, else push back
         _nextChar = readNextChar();
         location[LN_OFFSET] = location[LN_OFFSET]+1;
         if (_nextChar != NLN) {
            _reader.unread(_nextChar);
            if (location[LN_OFFSET] > 0) {
               location[LN_OFFSET] = location[LN_OFFSET]-1;
            }
         } else {
            // eat the newline
            location[JSON.LN_CNTR] = location[JSON.LN_CNTR]+1;
            location[JSON.LN_OFFSET] = 0;
         }
         if (_eolSignificant) {
            ttype = TT_EOL;
            return true;
         }
      } else if (_nextChar == NLN) {
         location[JSON.LN_CNTR] = location[JSON.LN_CNTR]+1;
         location[JSON.LN_OFFSET] = 0;
         if (_eolSignificant) {
            ttype = TT_EOL;
            return true;
         }
         _nextChar = readNextChar();
         location[LN_OFFSET] = location[LN_OFFSET]+1;
         if (_nextChar == EOF) {
            ttype = TT_EOF;
            return true;
         }
      } // else, keep ttype as next character
      return false;
   }

   /**
    * Tests input for hex characters 0-9, A-F, or a-f
    * 
    * @param test
    *             input character to be tested
    * @return true if input is a hex character
    */
   boolean isHexChar(int test) {
      return ( (0x30 <= test && test <= 0x39) ||
               (0x41 <= test && test <= 0x46) ||
               (0x61 <= test && test <= 0x66) );
   }

   /**
    * Discovers the next token in the reader and returns its type (ttype). If a
    * number is detected, it is returned as a double in the nval. If a word or
    * quoted word is detected, it is returned in the sval.
    * 
    * @param location
    *                 where we are reading from the input stream
    * @return the type of next token encountered in the reader
    * @throws IOException
    *                     if unable to read from the reader
    */
   public int nextToken(Integer[] location) throws IOException {
      if (_isPushedBack) {
         _isPushedBack = false;
         return ttype;
      }
      _currentValue.setLength(0);
      sval = null;
      _nextChar = readNextChar();
      location[LN_OFFSET] = location[LN_OFFSET] + 1;
      
      ttype = _nextChar;
      if (checkEOF()) {
         return ttype;
      }
      if (skipNewLines(location)) {
         location[JSON.LN_CNTR] = location[JSON.LN_CNTR]+1;
         location[JSON.LN_OFFSET] = 0;
         if (ttype == SPC) {
            return ttype;
         }
      }
      if (skipWhitespace(location)) {
         return ttype;
      }
      // have first non-whitespace character
      // check if number encountered
      if (checkForNumber(location)) {
         return ttype;
      }
      // if we got here, we are accumulating a word
      if (checkForWord(location)) {
         return ttype;
      }

      // if we got here, we should check for quoted word
      if (checkForQuotedWord(location)) {
         return ttype;
      }

      // else, return whatever character we've found
      return ttype;
   }

   /**
    * Sets the supplied character as ordinary in the character lookup table
    * 
    * @param ch
    *           The character to be tested
    */
   public void ordinaryChar(int ch) {
      _charTable[ch] = _IS_ORDINARY;
   }

   /**
    * Sets the range of characters supplied (including the endpoints) as ordinary
    * in the character lookup table
    * 
    * @param low
    *            starting, lower value character
    * @param hi
    *            ending, higher value character
    */
   public void ordinaryChars(int low, int hi) {
      for (int i = low; i < hi; i++) {
         // clear any previous flags
         _charTable[i] = _IS_ORDINARY;
      }
   }

   /**
    * Initializes the character lookup table for known digits for numeric
    * characters, along with the decimal point and minus sign. TODO: add plus sign?
    */
   public void parseNumbers() {
      // use OR because NUMERIC characters can also be wordChars
      for (int i = 0x0030; i < 0x0039; i++) {
         _charTable[i] |= _IS_NUMERIC;
      }
      _charTable[0x002d] |= _IS_NUMERIC; // '.'
      _charTable[0x002e] |= _IS_NUMERIC; // '-'
   }

   /**
    * Flags that a character has been returned after completing reading the next
    * token.
    * 
    * @throws IOException
    *                     if unable to read from the reader
    */
   public void pushBack() throws IOException {
      if (ttype != TT_UNKNOWN) {
         _isPushedBack = true;
      }
   }

   /**
    * Sets the supplied character as a quote delimiter for quoted word
    * identification in the character lookup table
    * 
    * @param ch
    *           The character defining a quote character
    */
   public void quoteChar(int ch) {
      _charTable[ch] = _IS_QUOTE;
   }

   /**
    * Read the next character from the reader.
    * 
    * @return the next character read from the reader
    * @throws IOException
    *                     if unable to read from the reader
    */
   private int readNextChar() throws IOException {
      if (_reader == null) {
         throw new IllegalStateException();
      }
      return _reader.read();
   }

   /**
    * Resets the character lookup table with single byte characters as ordinary,
    * and double byte characters as parts of words.
    */
   public void resetSyntax() {
      // allow for double byte characters to be digits later
      for (int i = 0; i < _charTable.length; i++) {
         if (i > 255) {
            // initially, consider double byte characters parts of words
            _charTable[i] = _IS_WORD;
         } else {
            // single byte characters are defined later
            _charTable[i] = _IS_ORDINARY;
         }
      }
   }

   /**
    * Reads and eats a new line ('/n') character, setting ttype as the next,
    * non-new line character or the end of file character.
    * 
    * @param location line count and line offset
    * @return true if new lines or end of file was detected
    * @throws IOException
    */
   private boolean skipNewLines(Integer[] location) throws IOException {
      if (_nextChar == NLN) {
         // skip newlines
         _nextChar = readNextChar();
         location[LN_OFFSET] = location[LN_OFFSET]+1;
         if (_nextChar == EOF) {
            ttype = TT_EOF;
            return true;
         }
         ttype = _nextChar;
         return true;
      }
      return false;
   }

   /**
    * Skips whitespace encountered up to a non-whitespace or end of line character,
    * or the end of file has been reached.
    * 
    * @return true if end of line or end of file has been reached.
    * @throws IOException
    *                     if unable to read from the reader
    */
   private boolean skipWhitespace(Integer[] location) throws IOException {
      while ((_charTable[_nextChar] & _IS_WHITESPACE) != 0) {
         // handle newline combinations /r, /r/n, /n
         if (handleEndOfLine(location)) {
            return true;
         } // else this is just whitespace so keep reading
         _nextChar = readNextChar();
         location[LN_OFFSET] = location[LN_OFFSET]+1;
      }
      return false;
   }

   /**
    * Convenience method to see the state of the next token and its type (e.g.,
    * while debugging)
    * 
    * @see java.lang.Object#toString()
    */
   public String toString() {
      String value = "";
      switch (ttype) {
      case TT_WORD: {
         value = sval;
         value = "WORD" + " value:" + value;
         break;
      }
      case TT_EOF: {
         value = "EOF";
         break;
      }
      case TT_EOL: {
         value = "EOL";
         break;
      }
      case TT_NUMBER: {
         value = Double.toString(nval);
         value = "NUMBER:" + " value:" + value;
         break;
      }
      case TT_UNKNOWN: {
         value = "unknown";
         break;
      }
      default: {
         value = "" + (char) ttype + "=0x" + Integer.toHexString(ttype);
         break;
      }
      }
      return "Token[" + value + "]";
   }

   /**
    * Sets the range of characters as whitespace in the character lookup table
    * 
    * @param low
    *            starting, lower range of whitespace characters
    * @param hi
    *            ending, higher range of whitespace characters
    */
   public void whitespaceChars(int low, int hi) {
      for (int i = low; i <= hi; i++) {
         _charTable[i] = _IS_WHITESPACE;
      }
   }

   /**
    * Flags the supplied range of characters as word characters in the lookup
    * table. These characters may also be flagged as numeric characters
    * 
    * @param low
    *            starting, lower character range of word characters
    * @param hi
    *            ending, higher character range of word characters
    */
   public void wordChars(int low, int hi) {
      for (int i = low; i <= hi; i++) {
         // use OR because wordChars can also be NUMERIC
         _charTable[i] |= _IS_WORD;
      }
   }
}
