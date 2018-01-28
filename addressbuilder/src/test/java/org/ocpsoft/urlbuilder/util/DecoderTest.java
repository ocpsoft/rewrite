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
package org.ocpsoft.urlbuilder.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DecoderTest
{

   @Test
   public void pathNotEncoded()
   {
      assertEquals("/foobar", Decoder.path("/foobar"));
   }

   @Test
   public void queryEndingWithSpaceDecoding()
   {
      assertEquals("foo ", Decoder.decode("foo%20", true));
      assertEquals("foo ", Decoder.decode("foo ", true));
      assertEquals("foo ", Decoder.decode("foo+", true));
   }

   @Test
   public void pathSpaceDecoding()
   {
      assertEquals("/foo bar", Decoder.path("/foo%20bar"));
      assertEquals("/foo bar", Decoder.path("/foo bar"));
   }

   @Test
   public void pathQuotationMark()
   {
      assertEquals("/foo\"bar", Decoder.path("/foo%22bar"));
      assertEquals("/foo\"bar", Decoder.path("/foo\"bar"));
   }

   @Test
   public void pathQuestionMark()
   {
      assertEquals("/foo?bar", Decoder.path("/foo%3Fbar"));
      assertEquals("/foo?bar", Decoder.path("/foo%3fbar"));
      assertEquals("/foo?bar", Decoder.path("/foo?bar"));
   }

   @Test
   public void pathLetterAWithDiaeresis()
   {
      assertEquals("/foo\u00E4bar", Decoder.path("/foo%C3%A4bar"));
      assertEquals("/foo\u00E4bar", Decoder.path("/foo\u00E4bar"));
   }

   @Test
   public void pathMultipleLettersAWithDiaeresis()
   {
      assertEquals("/foo\u00E4\u00E4\u00E4bar", Decoder.path("/foo%C3%A4%C3%A4%C3%A4bar"));
   }

   @Test
   public void pathArmenianCapitalLetterCheh()
   {
      assertEquals("/foo\u0543bar", Decoder.path("/foo%D5%83bar"));
      assertEquals("/foo\u0543bar", Decoder.path("/foo\u0543bar"));
   }

   @Test
   public void pathInvalidByteSequenceConvertedToReplacementChars()
   {
      assertEquals("/foo\uFFFD\uFFFDbar", Decoder.path("/foo%83%83bar"));
   }

   @Test
   public void pathSingleContinuationCharConvertedToReplacementChars()
   {
      assertEquals("/foo\uFFFD", Decoder.path("/foo%83"));
   }


   @Test
   public void pathTrailingPercentSign()
   {
      assertEquals("/foo", Decoder.path("/foo%"));
   }

   @Test
   public void pathTrailingIncompleteEscapeSequence()
   {
      assertEquals("/foo", Decoder.path("/foo%3"));
   }

   @Test
   public void pathNonHexValueAfterPercent()
   {
      assertEquals("/f\uFFFDbar", Decoder.path("/f%oobar"));
   }
   
   @Test
   public void pathNonHexValueInContinuationByte()
   {
      assertEquals("/foo\uFFFDbar", Decoder.path("/foo%C3%xxbar"));
   }
   
   @Test
   public void querySpaceDecoding()
   {
      assertEquals("foo bar", Decoder.query("foo+bar"));
      assertEquals("foo bar", Decoder.query("foo bar"));
   }

   
}
