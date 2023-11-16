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

import org.junit.Assert;
import org.junit.Test;
import com.api.json.JSONArray;
import com.api.json.JSONObject;

/**
 * JUnit test for self reference problems
 */
public class TestSelfReference {

   @Test
   public void test() {
      // TODO Auto-generated method stub
      JSONObject x = new JSONObject();
      JSONObject y = new JSONObject();
      JSONArray z = new JSONArray();
      try {
         x.put("x", x);
         Assert.fail("Should not allow x to be put into itself");
      } catch (Exception e) {
         if (e instanceof IllegalArgumentException) {
            Assert.assertTrue("Correctly identified IllegalArgumentException.", true);
         } else {
            Assert.fail("Should get IllegalArgumentException but got "+e.getLocalizedMessage());
         }
      }
      y.put("x", x);
      try {
         x.putAll(y);
         Assert.fail("Should not allow x to be put into itself via putAll");
      } catch (Exception e) {
         if (e instanceof IllegalArgumentException) {
            Assert.assertTrue("Correctly identified IllegalArgumentException.", true);
         } else {
            Assert.fail("Should get IllegalArgumentException but got "+e.getLocalizedMessage());
         }
      }
      try {
         z.add(z);
         Assert.fail("Should not allow z to be added into itself");
      } catch (Exception e) {
         if (e instanceof IllegalArgumentException) {
            Assert.assertTrue("Correctly identified IllegalArgumentException.", true);
         } else {
            Assert.fail("Should get IllegalArgumentException but got "+e.getLocalizedMessage());
         }
      }
   }
}
