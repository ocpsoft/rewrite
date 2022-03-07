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

import org.junit.Test;
import org.ocpsoft.rewrite.util.ParseTools.CaptureType;
import org.ocpsoft.rewrite.util.ParseTools.CapturingGroup;

import static org.assertj.core.api.Assertions.assertThat;

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
      assertThat(group.getEnd()).isEqualTo(9);
      assertThat(group.getCaptured()).isEqualTo("bar".toCharArray());

      group = ParseTools.balancedCapture("/foo/{{a}}/cab".toCharArray(), 5, 12, CaptureType.BRACE);
      assertThat(group.getEnd()).isEqualTo(9);
      assertThat(group.getCaptured()).isEqualTo("{a}".toCharArray());

      group = ParseTools.balancedCapture("/foo/{{a}}/cab".toCharArray(), 0, 12, CaptureType.REGEX);
      assertThat(group.getEnd()).isEqualTo(4);
      assertThat(group.getCaptured()).isEqualTo("foo".toCharArray());
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
      assertThat(group.getEnd()).isEqualTo(11);
      assertThat(group.getCaptured()).isEqualTo("bar\\}".toCharArray());
   }

   @Test
   public void testBalancedCaptureIgnoresEscapedNestedBeginChars()
   {
      CapturingGroup group = ParseTools.balancedCapture("/foo/{\\{a}}/cab".toCharArray(), 5, 12, CaptureType.BRACE);
      assertThat(group.getEnd()).isEqualTo(9);
      assertThat(group.getCaptured()).isEqualTo("\\{a".toCharArray());
   }

   @Test
   public void testBalancedCaptureReturnsEmptyForEmptyCapture() throws Exception
   {
      CapturingGroup group = ParseTools.balancedCapture("/foo/{}/cab".toCharArray(), 5, 8, CaptureType.BRACE);
      assertThat(group.getEnd()).isEqualTo(6);
      assertThat(group.getCaptured()).isEqualTo("".toCharArray());
   }

   @Test
   public void testIsEscapedReturnsTrueWhenCharacterIsEscaped() throws Exception
   {
      assertThat(ParseTools.isEscaped("/foo{bar\\}}".toCharArray(), 9)).isTrue();
   }

   @Test
   public void testIsEscapedReturnsTrueWhenSecondCharacterIsEscaped() throws Exception
   {
      assertThat(ParseTools.isEscaped("\\{".toCharArray(), 1)).isTrue();
   }

   @Test
   public void testIsEscapedReturnsFalseWhenCharacterIsDoubleEscaped() throws Exception
   {
      assertThat(ParseTools.isEscaped("/foo{bar\\\\}}".toCharArray(), 10)).isFalse();
   }

   @Test
   public void testIsEscapedReturnsTrueWhenCharacterIsTripleEscaped() throws Exception
   {
      assertThat(ParseTools.isEscaped("/foo{bar\\\\\\}}".toCharArray(), 11)).isFalse();
   }

   @Test
   public void testIsEscapedReturnsFalseWhenCharacterIsQuadrupleEscaped() throws Exception
   {
      assertThat(ParseTools.isEscaped("/foo{bar\\\\\\\\}}".toCharArray(), 12)).isFalse();
   }

   @Test
   public void testBalancedCaptureNested1()
   {
      CapturingGroup group = ParseTools.balancedCapture("(one(two)(three(four(five))))".toCharArray(), 0, 28,
               CaptureType.PAREN);
      assertThat(group.getEnd()).isEqualTo(28);
      assertThat(group.getCaptured()).isEqualTo("one(two)(three(four(five)))".toCharArray());
   }

   @Test
   public void testBalancedCaptureNested2()
   {
      CapturingGroup group = ParseTools.balancedCapture("(one(two)(three(four(five))))".toCharArray(), 4, 28,
               CaptureType.PAREN);
      assertThat(group.getEnd()).isEqualTo(8);
      assertThat(group.getCaptured()).isEqualTo("two".toCharArray());
   }

   @Test
   public void testBalancedCaptureNested3()
   {
      CapturingGroup group = ParseTools.balancedCapture("(one(two)(three(four(five))))".toCharArray(), 9, 28,
               CaptureType.PAREN);
      assertThat(group.getEnd()).isEqualTo(27);
      assertThat(group.getCaptured()).isEqualTo("three(four(five))".toCharArray());
   }

   @Test
   public void testBalancedCaptureNested4()
   {
      CapturingGroup group = ParseTools.balancedCapture("(one(two)(three(four(five))))".toCharArray(), 15, 28,
               CaptureType.PAREN);
      assertThat(group.getEnd()).isEqualTo(26);
      assertThat(group.getCaptured()).isEqualTo("four(five)".toCharArray());
   }

   @Test
   public void testBalancedCaptureNested5()
   {
      CapturingGroup group = ParseTools.balancedCapture("(one(two)(three(four(five))))".toCharArray(), 20, 28,
               CaptureType.PAREN);
      assertThat(group.getEnd()).isEqualTo(25);
      assertThat(group.getCaptured()).isEqualTo("five".toCharArray());
   }

}
