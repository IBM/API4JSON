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

package com.api.json.api4json;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import com.api.json.JSON;
import com.api.json.JSONArray;
import com.api.json.JSONArtifact;
import com.api.json.JSONObject;

public class TestJSON {

   @Test
   public void test() {
      String msg = "{type: ORDERING_DISABLED, message: EscalationNotice failed with status 400 because AIDT Interrupt Screen Not Active.}";
      try {
        JSON.parse(msg);
        Assert.fail("Expected an IOException \"Missing comma delimiter or an unquoted string value on line 1, near column 58\"");
    } catch (IOException e1) {
        Assert.assertEquals("Missing comma delimiter or an unquoted string value on line 1, near column 58",e1.getLocalizedMessage());
    }
      String unicodeTest = "™蒜肉"; // "\u2122\u849c\u8089";
      JSONArray testArray = new JSONArray();
      testArray.add("test");
      testArray.add("test1");
      testArray.add(1234.5);
      String filename = "." + File.separator + "src" + File.separator + "test"
         + File.separator + "resources" + File.separator
         + "jsonParseTestObject.json";
      File testFile = new File(filename);
      if (testFile.exists() && testFile.isDirectory() == false
         && testFile.canRead()) {
         try {
            JSONArtifact test = JSON.parse(new FileInputStream(testFile));
            Assert.assertFalse(test == null);
            JSONObject obj = (JSONObject) test;
            System.out.println(obj.serialize(true));
            Assert.assertFalse(obj == null);
            JSONArray entities = (JSONArray) obj.get("entities");
            Assert.assertFalse(entities == null);
            JSONObject entity = (JSONObject) entities.get(0);
            String out = (String) entity.get("a");
            Assert.assertFalse(out == null);
            Assert.assertEquals("https://newline\n\"here\" escaped solidus /",
               out);
            out = (String)entity.get("aaa");
            Assert.assertEquals(unicodeTest, out);
            Integer six = 6;
            ((JSONObject) test).put("six", six);
            Object objSix = ((JSONObject) test).get("six");
            Assert.assertEquals(Long.class, objSix.getClass());
            Assert.assertEquals(Double.class, entity.get("double").getClass());
            Assert.assertEquals(Double.class,
               entity.get("moreDouble").getClass());
            Assert.assertArrayEquals(testArray.toArray(),
               ((JSONArray) entity.get("values")).toArray());
         } catch (IOException e) {
            Assert.fail("Could not parse: " + filename + " got exception: "
               + e.getLocalizedMessage());
         }
      } else {
         Assert.fail("Can not read " + filename);
      }
      filename = filename.substring(0,
         filename.length() - "jsonParseTestObject.json".length())
         + "ExampleWCSWorkspace.json";
      testFile = new File(filename);
      if (testFile.exists() && testFile.isDirectory() == false
         && testFile.canRead()) {
         try {
            JSONObject workspace = (JSONObject)JSON.parse(new FileInputStream(testFile));
            JSONArray entities = (JSONArray)workspace.get("entities");
            Assert.assertEquals("Expect entities size == 20", 20L, ((Integer)entities.size()).longValue());
            Assert.assertEquals(Boolean.class, workspace.get("learning_opt_out").getClass());
            JSONArray counterexamples = (JSONArray)workspace.get("counterexamples");
            Assert.assertEquals("Expect counterexamples size == 0", 0L, ((Integer)counterexamples.size()).longValue());
         } catch (IOException e) {
            Assert.fail("Could not parse: " + filename + " got exception: "
                     + e.getLocalizedMessage());
         }
      }

      // test for key escaping
      JSONObject test = new JSONObject();
      test.put("key with \"quoted string\"", "some value");
      String testVal = "{\"key with \\\"quoted string\\\"\":\"some value\"}";
      Assert.assertEquals(testVal, test.toString());
      
      testVal = "{\"total_rows\":5597611,\"offset\":0,\"rows\":[\r\n" + 
            "\t{}]}\r\n" + 
            "";
      try {
         test = (JSONObject)JSON.parse(testVal);
         System.out.println(test.serialize(true));
      } catch (IOException e) {
         Assert.fail("Failed to parse: "+testVal+" due to embedded carriage returns. "+e.getLocalizedMessage());
      }
   }
}
