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
