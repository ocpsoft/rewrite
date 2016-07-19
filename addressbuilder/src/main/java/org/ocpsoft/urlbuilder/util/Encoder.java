package org.ocpsoft.urlbuilder.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Utility class to encode URL path and query parts.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Encoder
{

   private static final Charset UTF8 = Charset.forName("UTF-8");

   /**
    * Encodes the given string using HTML form encoding as described in RFC 2396.
    * 
    * @throws IllegalArgumentException when illegal URI syntax is attempted.
    */
   public static String path(CharSequence s) throws IllegalArgumentException
   {
      try
      {
         final URI uri = new URI("http", "0.0.0.0", "/" + s, null);
         return uri.toASCIIString().substring(15);
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException(e);
      }
   }

   /**
    * Encodes the given string using HTML form encoding as described in RFC 2396.
    * 
    * @throws IllegalArgumentException when illegal URI syntax is attempted.
    */
   public static String query(CharSequence s) throws IllegalArgumentException
   {
      try {
         return URLEncoder.encode(s.toString(), UTF8.name());
      }
      catch (UnsupportedEncodingException e) {
         throw new IllegalArgumentException(e);
      }
   }

}
