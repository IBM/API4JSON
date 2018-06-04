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
   }
}
