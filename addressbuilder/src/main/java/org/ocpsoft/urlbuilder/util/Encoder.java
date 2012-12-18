package org.ocpsoft.urlbuilder.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class Encoder
{

   private static final Charset UTF8 = Charset.forName("UTF-8");

   /**
    * Encodes the given string as described in RFC 2396
    */
   public static String path(CharSequence s)
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
    * Encodes the given string using HTML form encoding
    */
   public static String query(CharSequence s)
   {
      try {
         return URLEncoder.encode(s.toString(), UTF8.name());
      }
      catch (UnsupportedEncodingException e) {
         throw new IllegalStateException(e);
      }
   }

}
