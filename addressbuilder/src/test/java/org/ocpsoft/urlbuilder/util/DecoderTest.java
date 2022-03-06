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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DecoderTest
{

   @Test
   public void pathNotEncoded()
   {
      assertThat(Decoder.path("/foobar")).isEqualTo("/foobar");
   }

   @Test
   public void queryEndingWithSpaceDecoding()
   {
      assertThat(Decoder.decode("foo%20", true)).isEqualTo("foo ");
      assertThat(Decoder.decode("foo ", true)).isEqualTo("foo ");
      assertThat(Decoder.decode("foo+", true)).isEqualTo("foo ");
   }

   @Test
   public void pathSpaceDecoding()
   {
      assertThat(Decoder.path("/foo%20bar")).isEqualTo("/foo bar");
      assertThat(Decoder.path("/foo bar")).isEqualTo("/foo bar");
   }

   @Test
   public void pathQuotationMark()
   {
      assertThat(Decoder.path("/foo%22bar")).isEqualTo("/foo\"bar");
      assertThat(Decoder.path("/foo\"bar")).isEqualTo("/foo\"bar");
   }

   @Test
   public void pathQuestionMark()
   {
      assertThat(Decoder.path("/foo%3Fbar")).isEqualTo("/foo?bar");
      assertThat(Decoder.path("/foo%3fbar")).isEqualTo("/foo?bar");
      assertThat(Decoder.path("/foo?bar")).isEqualTo("/foo?bar");
   }

   @Test
   public void pathLetterAWithDiaeresis()
   {
      assertThat(Decoder.path("/foo%C3%A4bar")).isEqualTo("/foo\u00E4bar");
      assertThat(Decoder.path("/foo\u00E4bar")).isEqualTo("/foo\u00E4bar");
   }

   @Test
   public void pathMultipleLettersAWithDiaeresis()
   {
      assertThat(Decoder.path("/foo%C3%A4%C3%A4%C3%A4bar")).isEqualTo("/foo\u00E4\u00E4\u00E4bar");
   }

   @Test
   public void pathArmenianCapitalLetterCheh()
   {
      assertThat(Decoder.path("/foo%D5%83bar")).isEqualTo("/foo\u0543bar");
      assertThat(Decoder.path("/foo\u0543bar")).isEqualTo("/foo\u0543bar");
   }

   @Test
   public void pathInvalidByteSequenceConvertedToReplacementChars()
   {
      assertThat(Decoder.path("/foo%83%83bar")).isEqualTo("/foo\uFFFD\uFFFDbar");
   }

   @Test
   public void pathSingleContinuationCharConvertedToReplacementChars()
   {
      assertThat(Decoder.path("/foo%83")).isEqualTo("/foo\uFFFD");
   }


   @Test
   public void pathTrailingPercentSign()
   {
      assertThat(Decoder.path("/foo%")).isEqualTo("/foo");
   }

   @Test
   public void pathTrailingIncompleteEscapeSequence()
   {
      assertThat(Decoder.path("/foo%3")).isEqualTo("/foo");
   }

   @Test
   public void pathNonHexValueAfterPercent()
   {
      assertThat(Decoder.path("/f%oobar")).isEqualTo("/f\uFFFDbar");
   }
   
   @Test
   public void pathNonHexValueInContinuationByte()
   {
      assertThat(Decoder.path("/foo%C3%xxbar")).isEqualTo("/foo\uFFFDbar");
   }
   
   @Test
   public void querySpaceDecoding()
   {
      assertThat(Decoder.query("foo+bar")).isEqualTo("foo bar");
      assertThat(Decoder.query("foo bar")).isEqualTo("foo bar");
   }

   
}
