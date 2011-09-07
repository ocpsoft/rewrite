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
import java.util.List;

import com.ocpsoft.common.util.Strings;

public class URLBuilder
{

   private Metadata metadata = new Metadata();

   private final List<String> segments = new ArrayList<String>();

   private QueryStringBuilder query = QueryStringBuilder.begin();

   /**
    * Return a new instance of {@link URLBuilder}
    */
   public static URLBuilder begin()
   {
      return new URLBuilder();
   }

   public static URLBuilder build(final List<String> segments, final Metadata metadata)
   {
      return URLBuilder.begin().addPathSegments(segments).setMetadata(metadata);
   }

   public static URLBuilder build(final String url)
   {
      if (url == null)
      {
         throw new IllegalArgumentException("URL cannot be null.");
      }
      if (url.contains("?"))
      {
         String[] parts = url.split("\\?");
         String path = parts[0];
         String query = parts[1];
         if (parts.length > 2)
         {
            query = Strings.join(Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length)), "?");
         }
         return new URLBuilder().addPathSegments(path).addQueryParameters(query);
      }
      return new URLBuilder().addPathSegments(url);
   }

   /*
    * Constructors
    */
   private URLBuilder()
   {}

   public URLBuilder(final List<String> encodedSegments, final Metadata metadata, final QueryStringBuilder query)
   {
      this.segments.addAll(encodedSegments);
      this.metadata = metadata.copy();
      this.query = query;
   }

   /*
    * End Constructors
    */

   public URLBuilder addPathSegments(final List<String> segments)
   {
      this.segments.addAll(segments);
      return this;
   }

   public URLBuilder addPathSegments(final String path)
   {
      if (path != null)
      {
         String temp = path.trim();
         if (temp.endsWith("/"))
         {
            metadata.setTrailingSlash(true);
         }
         if (temp.startsWith("/") && segments.isEmpty())
         {
            metadata.setLeadingSlash(true);
         }

         String trimmedUrl = trimSurroundingSlashes(path);
         String[] newSegments = trimmedUrl.split("/");

         this.segments.addAll(Arrays.asList(newSegments));
      }
      else
      {
         throw new IllegalArgumentException("URL cannot be null.");
      }
      return this;
   }

   public URLBuilder addQueryParameters(final String query)
   {
      this.query.addParameters(query);
      return this;
   }

   /**
    * Return a decoded form of this URL.
    */
   public URLBuilder decode()
   {
      return new URLBuilder(getDecodedSegments(), metadata, query.decode());
   }

   /**
    * Decodes a segment using the {@link URI} class.
    * 
    * @param segment The segment to decode
    * @return the decoded segment
    */
   private String decodeSegment(final String segment)
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
    * Return an encoded form of this URL.
    */
   public URLBuilder encode()
   {
      return new URLBuilder(getEncodedSegments(), metadata, query.encode());
   }

   /**
    * Encodes a segment using the {@link URI} class.
    * 
    * @param segment The segment to encode
    * @return the encoded segment
    */
   private String encodeSegment(final String segment)
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

   private List<String> getDecodedSegments()
   {
      List<String> result = new ArrayList<String>();
      for (String segment : segments)
      {
         result.add(decodeSegment(segment));
      }
      return result;
   }

   private List<String> getEncodedSegments()
   {
      List<String> resultSegments = new ArrayList<String>();
      for (String segment : segments)
      {
         resultSegments.add(encodeSegment(segment));
      }
      return resultSegments;
   }

   /**
    * Get the character encoding of this URL (default UTF-8)
    */
   public String getEncoding()
   {
      return metadata.getEncoding();
   }

   /*
    * Getters & Setters
    */

   /**
    * Get the {@link Metadata} object for this URL
    */
   public Metadata getMetadata()
   {
      return metadata;
   }

   public QueryStringBuilder getQueryStringBuilder()
   {
      return query;
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
    * Get the number of segments (separated by '/') in this URL
    */
   public int numSegments()
   {
      return segments.size();
   }

   /**
    * Set the character encoding of this URL (default UTF-8)
    */
   public void setEncoding(final String encoding)
   {
      metadata.setEncoding(encoding);
   }

   /**
    * Set the {@link Metadata} object for this URL
    * 
    * @return
    */
   public URLBuilder setMetadata(final Metadata metadata)
   {
      this.metadata = metadata;
      return this;
   }

   public String toPath()
   {
      return metadata.buildURLFromSegments(segments);
   }

   /**
    * Return a String representation of this URL
    */
   @Override
   public String toString()
   {
      return toURL();
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

   /**
    * Return a String representation of this URL
    */
   public String toURL()
   {
      return toPath() + query.toQueryString();
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
}
