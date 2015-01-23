/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.util;

import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.rewrite.util.ParseTools.CaptureType;
import org.ocpsoft.rewrite.util.ParseTools.CapturingGroup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ParseToolsTest
{
   @Test
   public void testBalancedCapture()
   {
      CapturingGroup group = ParseTools.balancedCapture("/foo/{bar}/cab".toCharArray(), 5, 12, CaptureType.BRACE);
      Assert.assertEquals(9, group.getEnd());
      Assert.assertArrayEquals("bar".toCharArray(), group.getCaptured());

      group = ParseTools.balancedCapture("/foo/{{a}}/cab".toCharArray(), 5, 12, CaptureType.BRACE);
      Assert.assertEquals(9, group.getEnd());
      Assert.assertArrayEquals("{a}".toCharArray(), group.getCaptured());

      group = ParseTools.balancedCapture("/foo/{{a}}/cab".toCharArray(), 0, 12, CaptureType.REGEX);
      Assert.assertEquals(4, group.getEnd());
      Assert.assertArrayEquals("foo".toCharArray(), group.getCaptured());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBalancedThrowsExceptionIfBeginCharIsEscaped()
   {
      ParseTools.balancedCapture("/foo/\\{{a}}/cab".toCharArray(), 6, 12, CaptureType.BRACE);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBalancedThrowsExceptionIfBeginCharIsEscaped2()
   {
      ParseTools.balancedCapture("/foo/\\\\\\{{a}}/cab".toCharArray(), 6, 12, CaptureType.BRACE);
   }

   @Test
   public void testBalancedDoesNotThrowExceptionIfBeginCharIsNotEscaped()
   {
      ParseTools.balancedCapture("/foo/\\\\{a}/cab".toCharArray(), 7, 13, CaptureType.BRACE);
   }

   @Test
   public void testBalancedDoesNotThrowExceptionIfBeginCharIsNotEscaped2()
   {
      ParseTools.balancedCapture("/foo/\\\\\\\\{a}/cab".toCharArray(), 9, 15, CaptureType.BRACE);
   }

   @Test
   public void testBalancedDoesNotThrowExceptionIfBeginCharIsNotEscaped3()
   {
      ParseTools.balancedCapture("/foo/\\\\{{a}}/cab".toCharArray(), 7, 13, CaptureType.BRACE);
   }

   @Test
   public void testBalancedCaptureIgnoresEscapedTerminations()
   {
      CapturingGroup group = ParseTools.balancedCapture("/foo/{bar\\}}/cab".toCharArray(), 5, 12, CaptureType.BRACE);
      Assert.assertEquals(11, group.getEnd());
      Assert.assertArrayEquals("bar\\}".toCharArray(), group.getCaptured());
   }

   @Test
   public void testBalancedCaptureIgnoresEscapedNestedBeginChars()
   {
      CapturingGroup group = ParseTools.balancedCapture("/foo/{\\{a}}/cab".toCharArray(), 5, 12, CaptureType.BRACE);
      Assert.assertEquals(9, group.getEnd());
      Assert.assertArrayEquals("\\{a".toCharArray(), group.getCaptured());
   }

   @Test
   public void testBalancedCaptureReturnsEmptyForEmptyCapture() throws Exception
   {
      CapturingGroup group = ParseTools.balancedCapture("/foo/{}/cab".toCharArray(), 5, 8, CaptureType.BRACE);
      Assert.assertEquals(6, group.getEnd());
      Assert.assertArrayEquals("".toCharArray(), group.getCaptured());
   }

   @Test
   public void testIsEscapedReturnsTrueWhenCharacterIsEscaped() throws Exception
   {
      Assert.assertTrue(ParseTools.isEscaped("/foo{bar\\}}".toCharArray(), 9));
   }

   @Test
   public void testIsEscapedReturnsTrueWhenSecondCharacterIsEscaped() throws Exception
   {
      Assert.assertTrue(ParseTools.isEscaped("\\{".toCharArray(), 1));
   }

   @Test
   public void testIsEscapedReturnsFalseWhenCharacterIsDoubleEscaped() throws Exception
   {
      Assert.assertFalse(ParseTools.isEscaped("/foo{bar\\\\}}".toCharArray(), 10));
   }

   @Test
   public void testIsEscapedReturnsTrueWhenCharacterIsTripleEscaped() throws Exception
   {
      Assert.assertFalse(ParseTools.isEscaped("/foo{bar\\\\\\}}".toCharArray(), 11));
   }

   @Test
   public void testIsEscapedReturnsFalseWhenCharacterIsQuadrupleEscaped() throws Exception
   {
      Assert.assertFalse(ParseTools.isEscaped("/foo{bar\\\\\\\\}}".toCharArray(), 12));
   }

   @Test
   public void testBalancedCaptureNested1()
   {
      CapturingGroup group = ParseTools.balancedCapture("(one(two)(three(four(five))))".toCharArray(), 0, 28,
               CaptureType.PAREN);
      Assert.assertEquals(28, group.getEnd());
      Assert.assertArrayEquals("one(two)(three(four(five)))".toCharArray(), group.getCaptured());
   }

   @Test
   public void testBalancedCaptureNested2()
   {
      CapturingGroup group = ParseTools.balancedCapture("(one(two)(three(four(five))))".toCharArray(), 4, 28,
               CaptureType.PAREN);
      Assert.assertEquals(8, group.getEnd());
      Assert.assertArrayEquals("two".toCharArray(), group.getCaptured());
   }

   @Test
   public void testBalancedCaptureNested3()
   {
      CapturingGroup group = ParseTools.balancedCapture("(one(two)(three(four(five))))".toCharArray(), 9, 28,
               CaptureType.PAREN);
      Assert.assertEquals(27, group.getEnd());
      Assert.assertArrayEquals("three(four(five))".toCharArray(), group.getCaptured());
   }

   @Test
   public void testBalancedCaptureNested4()
   {
      CapturingGroup group = ParseTools.balancedCapture("(one(two)(three(four(five))))".toCharArray(), 15, 28,
               CaptureType.PAREN);
      Assert.assertEquals(26, group.getEnd());
      Assert.assertArrayEquals("four(five)".toCharArray(), group.getCaptured());
   }

   @Test
   public void testBalancedCaptureNested5()
   {
      CapturingGroup group = ParseTools.balancedCapture("(one(two)(three(four(five))))".toCharArray(), 20, 28,
               CaptureType.PAREN);
      Assert.assertEquals(25, group.getEnd());
      Assert.assertArrayEquals("five".toCharArray(), group.getCaptured());
   }

}
