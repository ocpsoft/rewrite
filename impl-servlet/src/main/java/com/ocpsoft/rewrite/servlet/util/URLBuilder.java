/*
 * Copyright 2010 Lincoln Baxter, III
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
package com.ocpsoft.rewrite.servlet.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URLBuilder
{
   private Metadata metadata = new Metadata();
   private String originalURL = "";
   private List<String> segments;

   private final Map<String, List<String>> decodedSegments = new HashMap<String, List<String>>();

   /**
    * Create a URL object for the given url String. The input string must not yet have been decoded.
    * 
    * @param url The raw, un-decoded url String
    */
   public URLBuilder(final String url)
   {
      if (url != null)
      {
         originalURL = url.trim();
         if (originalURL.endsWith("/"))
         {
            metadata.setTrailingSlash(true);
         }
         if (originalURL.startsWith("/"))
         {
            metadata.setLeadingSlash(true);
         }

         String trimmedUrl = trimSurroundingSlashes(url);
         String[] segments = trimmedUrl.split("/");

         this.segments = Arrays.asList(segments);
      }
      else
      {
         throw new IllegalArgumentException("URL cannot be null.");
      }
   }

   public static URLBuilder build(final String url)
   {
      return new URLBuilder(url);
   }

   /**
    * Create a URL object for the given url segments (separated by '/' from the original url string), using the given
    * metadata object to represent the encoding and leading/trailing slash information about this URL.
    */
   public URLBuilder(final List<String> segments, final Metadata metadata)
   {
      this.metadata = metadata;
      this.segments = segments;
      this.originalURL = metadata.buildURLFromSegments(segments);
   }

   public static URLBuilder build(final List<String> segments, final Metadata metadata)
   {
      return new URLBuilder(segments, metadata);
   }

   /**
    * Get a list of all decoded segments (separated by '/') in this URL.
    */
   public List<String> getDecodedSegments()
   {
      String encoding = metadata.getEncoding();
      if (!decodedSegments.containsKey(encoding))
      {
         List<String> result = new ArrayList<String>();
         for (String segment : segments)
         {
            result.add(decodeSegment(segment));
         }
         decodedSegments.put(encoding, Collections.unmodifiableList(result));
      }
      return decodedSegments.get(encoding);
   }

   /**
    * Get a list of all encoded segments (separated by '/') in this URL.
    */
   public List<String> getEncodedSegments()
   {
      List<String> resultSegments = new ArrayList<String>();
      for (String segment : segments)
      {
         resultSegments.add(encodeSegment(segment));
      }
      return resultSegments;
   }

   /**
    * Encodes a segment using the {@link URI} class.
    * 
    * @param segment The segment to encode
    * @return the encoded segment
    */
   private static String encodeSegment(final String segment)
   {
      try
      {
         final URI uri = new URI("http", "localhost", "/" + segment, null);
         return uri.toASCIIString().substring(17);
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException(e);
      }
   }

   /**
    * Decodes a segment using the {@link URI} class.
    * 
    * @param segment The segment to decode
    * @return the decoded segment
    */
   private static String decodeSegment(final String segment)
   {
      try
      {
         String prepared = ("http://localhost/" + segment)
                  .replaceAll(" ", "%20")
                  .replaceAll("\\+", "%20");
         final URI uri = new URI(prepared);
         return uri.getPath().substring(1);
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException(e);
      }
   }

   /**
    * Return a decoded form of this URL.
    */
   public URLBuilder decode()
   {
      return new URLBuilder(getDecodedSegments(), metadata);
   }

   /**
    * Return an encoded form of this URL.
    */
   public URLBuilder encode()
   {
      return new URLBuilder(getEncodedSegments(), metadata);
   }

   /**
    * Get the number of segments (separated by '/') in this URL
    */
   public int numSegments()
   {
      return segments.size();
   }

   /**
    * Return a String representation of this URL
    */
   @Override
   public String toString()
   {
      return toURL();
   }

   /*
    * Getters & Setters
    */

   /**
    * Return a String representation of this URL
    */
   public String toURL()
   {
      return originalURL;
   }

   /**
    * Return all segments (separated by '/') in this URL
    * 
    * @return
    */
   public List<String> getSegments()
   {
      return Collections.unmodifiableList(segments);
   }

   /**
    * Return true if this URL begins with '/'
    */
   public boolean hasLeadingSlash()
   {
      return metadata.hasLeadingSlash();
   }

   /**
    * Return true if this URL ends with '/'
    */
   public boolean hasTrailingSlash()
   {
      return metadata.hasTrailingSlash();
   }

   /**
    * Get the character encoding of this URL (default UTF-8)
    */
   public String getEncoding()
   {
      return metadata.getEncoding();
   }

   /**
    * Set the character encoding of this URL (default UTF-8)
    */
   public void setEncoding(final String encoding)
   {
      metadata.setEncoding(encoding);
   }

   /**
    * Get the {@link Metadata} object for this URL
    */
   public Metadata getMetadata()
   {
      return metadata;
   }

   /**
    * Set the {@link Metadata} object for this URL
    */
   public void setMetadata(final Metadata metadata)
   {
      this.metadata = metadata;
   }

   /*
    * Helpers
    */
   private String trimSurroundingSlashes(final String url)
   {
      String result = null;
      if (url != null)
      {
         result = url.trim();
         if (result.startsWith("/"))
         {
            result = result.substring(1);
         }
         if (result.endsWith("/"))
         {
            result = result.substring(0, result.length() - 1);
         }
      }
      return result;
   }

   public URI toURI()
   {
      try {
         URI uri = new URI(toURL());
         return uri;
      }
      catch (URISyntaxException e) {
         throw new IllegalStateException("URL cannot be parsed.", e);
      }
   }
}
