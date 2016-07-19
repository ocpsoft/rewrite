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
package com.ocpsoft.pretty.faces.url;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ocpsoft.urlbuilder.util.Decoder;
import org.ocpsoft.urlbuilder.util.Encoder;

import com.ocpsoft.pretty.faces.util.StringUtils;

/**
 * Utility class providing common functionality required when interacting with and asking questions of URLs.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class URL
{
   private Metadata metadata = new Metadata();
   private String originalURL = "";
   private List<String> segments;

   private final Map<String, List<String>> decodedSegments = new LinkedHashMap<String, List<String>>();

   /**
    * Create a URL object for the given url String. The input string must not yet have been decoded.
    * 
    * @param url The raw, un-decoded url String
    */
   public URL(String url)
   {
      if (url != null)
      {
         url = url.trim();
         originalURL = url;
         if (StringUtils.hasLeadingSlash(url))
         {
            metadata.setLeadingSlash(true);
            url = url.substring(1);
         }
         if (StringUtils.hasTrailingSlash(url))
         {
            metadata.setTrailingSlash(true);
            url = url.substring(0, url.length() - 1);
         }

         this.segments = Arrays.asList(StringUtils.splitBySlash(url));
      }
      else
      {
         throw new IllegalArgumentException("URL cannot be null.");
      }
   }

   public static URL build(final String url)
   {
      return new URL(url);
   }

   /**
    * Create a URL object for the given url segments (separated by '/' from the original url string), using the given
    * metadata object to represent the encoding and leading/trailing slash information about this URL.
    */
   public URL(final List<String> segments, final Metadata metadata)
   {
      this.metadata = metadata;
      this.segments = segments;
      this.originalURL = metadata.buildURLFromSegments(segments);
   }

   public static URL build(final List<String> segments, final Metadata metadata)
   {
      return new URL(segments, metadata);
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
            result.add(Decoder.path(segment));
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
         resultSegments.add(Encoder.path(segment));
      }
      return resultSegments;
   }

   /**
    * Return a decoded form of this URL.
    */
   public URL decode()
   {
      return new URL(getDecodedSegments(), metadata);
   }

   /**
    * Return an encoded form of this URL.
    */
   public URL encode()
   {
      return new URL(getEncodedSegments(), metadata);
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
}
