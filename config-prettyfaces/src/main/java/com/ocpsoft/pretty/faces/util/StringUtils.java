package com.ocpsoft.pretty.faces.util;

/**
 * 
 * Helper class providing similar functions like the
 * StringUtils class of Apache Commons.
 * 
 * @author Christian Kaltepoth
 *
 */
public class StringUtils
{
   
   private static final char SLASH = '/';
   
   public static boolean isBlank(String s) {
      return s == null || s.trim().length() == 0;
   }
   
   public static boolean isNotBlank(String s) {
      return s != null && s.trim().length() > 0;
   }
   
   public static boolean hasLeadingSlash(final String s)
   {
      return s != null && isNotBlank(s) && SLASH == s.charAt(0);
   }

   public static boolean hasTrailingSlash(final String s)
   {
      return s != null && isNotBlank(s) && SLASH == s.charAt(s.length() - 1);
   }

   public static String[] splitBySlash(final String s)
   {
      if (s == null || isBlank(s))
      {
         return new String[0];
      }
      char[] chars = s.toCharArray();
      int numberOfSegments = countSlashes(s) + 1;

      final String[] segments = new String[numberOfSegments];
      int currentSegmentIndex = 0;
      int lastSlashIndex = -1;
      for (int i = 0; i < chars.length; ++i)
      {
         if (chars[i] == '/')
         {
            segments[currentSegmentIndex] = new String(chars, lastSlashIndex + 1, i - lastSlashIndex - 1);
            ++currentSegmentIndex;
            lastSlashIndex = i;
         }
      }
      if (lastSlashIndex + 1 < chars.length)
      {
         segments[currentSegmentIndex] = new String(chars, lastSlashIndex + 1, chars.length - lastSlashIndex - 1);
      }
      else
      {
         segments[currentSegmentIndex] = "";
      }

      return segments;

   }

   public static int countSlashes(final String s)
   {
      int result = 0;
      for (char ch : s.toCharArray())
      {
         if (ch == '/')
         {
            ++result;
         }
      }
      return result;
   }

   public static String removePathPrefix(String prefix, String url)
   {
       // if the remaining path does not start with a / 
       // then don't remove the context path
       // this handles context paths like / or /context/ that remove too much 
       // or URLs like /contextPathPlus that don't actually match /contextPath
       // this also addresses https://github.com/ocpsoft/rewrite/issues/180
       if (url.charAt(prefix.length()) == '/' && url.startsWith(prefix))
       {
          url = url.substring(prefix.length());
       }

       return url;
   }

}
