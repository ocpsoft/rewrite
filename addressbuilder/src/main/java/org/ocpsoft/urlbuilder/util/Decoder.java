package org.ocpsoft.urlbuilder.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public class Decoder
{
   public static String path(final CharSequence path)
   {
      try
      {
         final URI uri = new URI("http://0.0.0.0/" + path);
         return uri.getPath().substring(1);
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException(e);
      }
   }

   public static String query(final CharSequence query)
   {
      try
      {
         return URLDecoder.decode(query.toString(), "UTF-8");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new IllegalArgumentException(e);
      }
   }
}
