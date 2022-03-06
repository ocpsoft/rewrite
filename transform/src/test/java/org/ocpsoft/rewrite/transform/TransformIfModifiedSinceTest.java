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
package org.ocpsoft.rewrite.transform;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import okhttp3.Headers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for correct handling of the 'If-Modified-Since' header.
 * 
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class TransformIfModifiedSinceTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsWebResource(new StringAsset("something"), "test.txt")
               .addClasses(TransformIfModifiedSinceTestProvider.class, UppercaseTransformer.class)
               .addAsServiceProvider(ConfigurationProvider.class, TransformIfModifiedSinceTestProvider.class);
   }

   @Test
   public void testNoIfModifiedSinceHeader() throws Exception
   {
      /**
       * No 'If-Modified-Since' header is set. Therefore the result should be a 200 OK with the expected content.
       */
      HttpAction first = get("/test.txt");
      assertThat(first.getStatusCode()).isEqualTo(200);
      assertThat(first.getResponseContent()).isEqualTo("SOMETHING");
   }

   @Test
   public void testIfModifiedSinceHeaderInThePast() throws Exception
   {
      /**
       * The requested resource has the current date as the modification date. The test is setting the
       * 'If-Modified-Since' header to one hour in the past. This means the client is asking if the resource has changed
       * since the last time he requested it (which is one hour ago). As the modification date is newer, the resource
       * has to be sent again.
       */
      HttpAction request = get(client, "/test.txt", ifModifiedSinceHeaderNowPlusHours(-1));
      assertThat(request.getStatusCode()).isEqualTo(200);
      assertThat(request.getResponseContent()).isEqualTo("SOMETHING");
   }

   @Test
   public void testIfModifiedSinceHeaderInTheFuture() throws Exception
   {
      /**
       * The requested resource has the current date as the modification date. The test is setting the
       * 'If-Modified-Since' header to one hour in the future. This simulates the situation in which the client is
       * asking for the resource again and provides a date that is after the last modification date of the resouce.
       * Therefore the server can send a '304 Not Modified' response.
       */
      HttpAction second = get(client, "/test.txt", ifModifiedSinceHeaderNowPlusHours(1));
      assertThat(second.getStatusCode()).isEqualTo(304);
   }

   private Headers ifModifiedSinceHeaderNowPlusHours(int hours)
   {

      Calendar cal = GregorianCalendar.getInstance();
      cal.add(GregorianCalendar.HOUR_OF_DAY, hours);
      Date date = cal.getTime();

      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

      return Headers.of("If-Modified-Since", simpleDateFormat.format(date));
   }

}
