package org.ocpsoft.urlbuilder.util;

import java.nio.charset.Charset;

public class Decoder
{

   private static final Charset UTF8 = Charset.forName("UTF8");

   public static String path(final CharSequence path)
   {
      return decode(path, false);
   }

   public static String query(final CharSequence query)
   {
      return decode(query, true);
   }

   public static String decode(final CharSequence path, final boolean query) {
      StringBuilder decoded = new StringBuilder();
      int length = path.length();
      int pos = 0;

      while (pos < length) {
         // '+' -> ' ' for query strings
         if (query && path.charAt(pos) == '+') {
            decoded.append(' ');
            pos++;
            continue;
         }

         // percent-encoded values
         if (path.charAt(pos) == '%') {

            // a single Unicode char may be represented by multiple percent encoded bytes
            byte[] bytes = new byte[length / 3];
            int i = 0;
            boolean invalid = false;

            while (pos < length && path.charAt(pos) == '%') {

               // make sure we can read the two hex characters
               if (pos + 2 < length) {
                  try {
                     String hex = path.subSequence(pos + 1, pos + 3).toString();
                     int b = Integer.parseInt(hex, 16);
                     bytes[i++] = (byte) b;
                  }
                  catch (NumberFormatException e) {
                     // not a valid hex value
                     invalid = true;
                  }
               }
               pos += 3;

            }

            // decode the byte sequence with UTF8 if no invalid byte was found
            if (!invalid) {
               decoded.append(new String(bytes, 0, i, UTF8));
            }

            // We represent invalid percent encoded values the same way UTF8 does it
            // http://unicode-table.com/de/search/?q=%EF%BF%BD
            else {
               decoded.append('\uFFFD');
            }
         }

         // not escaped
         else {
            decoded.append(path.charAt(pos));
            pos++;
         }
      }
      return decoded.toString();
   }
}
