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
package org.ocpsoft.rewrite.servlet.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ocpsoft.common.util.Strings;
import org.ocpsoft.urlbuilder.AddressBuilder;
import org.ocpsoft.urlbuilder.util.Decoder;
import org.ocpsoft.urlbuilder.util.Encoder;

/**
 * Utility for building URL strings. Also manages the URL query string with the help of {@link QueryStringBuilder}.
 * 
 * @deprecated Use {@link AddressBuilder} instead. May be removed in subsequent releases.
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Deprecated
public class URLBuilder
{
   private Metadata metadata = new Metadata();

   private final List<String> segments = new ArrayList<String>();

   private QueryStringBuilder query = QueryStringBuilder.createNew();

   /**
    * Return a new instance of {@link URLBuilder}. Until modified. This URL will be blank.
    */
   public static URLBuilder createNew()
   {
      return new URLBuilder();
   }

   /**
    * Create a new instance of {@link URLBuilder} from the given URL segments, {@link Metadata}, and
    * {@link QueryStringBuilder}.
    */
   public static URLBuilder createFrom(final List<String> segments, final Metadata metadata,
            final QueryStringBuilder query)
   {
      return URLBuilder.createNew().appendPathSegments(segments).setMetadata(metadata).setQueryString(query);
   }

   /**
    * Create a new instance of {@link URLBuilder} from the given URL path segments and {@link Metadata}.
    */
   public static URLBuilder createFrom(final List<String> segments, final Metadata metadata)
   {
      return URLBuilder.createNew().appendPathSegments(segments).setMetadata(metadata);
   }

   /**
    * Create a new instance of {@link URLBuilder} from the given URL path segments.
    */
   public static URLBuilder createFrom(final String segments)
   {
      if (segments == null)
      {
         throw new IllegalArgumentException("URL cannot be null.");
      }
      if (segments.contains("?"))
      {
         String[] parts = segments.split("\\?", -1);
         String path = parts[0];
         String query = parts[1];
         if (parts.length > 2)
         {
            query = Strings.join(Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length)), "?");
         }
         return new URLBuilder().appendPathSegments(path).addQueryParameters(query);
      }
      return new URLBuilder().appendPathSegments(segments);
   }

   /*
    * Constructors
    */
   private URLBuilder()
   {}

   private URLBuilder(final List<String> segments, final Metadata metadata, final QueryStringBuilder query)
   {
      this.segments.addAll(segments);
      this.metadata = metadata.copy();
      this.query = query;
   }

   /*
    * End Constructors
    */

   /**
    * Append additional path segments to the end of this URL. When called the first time, this method will also
    * initialize the {@link Metadata} for this {@link URLBuilder} based on the parsed segments given.
    */
   public URLBuilder appendPathSegments(final List<String> segments)
   {
      this.segments.addAll(segments);
      return this;
   }

   /**
    * Append additional path segments to the end of this URL. When called the first time, this method will also
    * initialize the {@link Metadata} for this {@link URLBuilder} based on the parsed segments given.
    */
   public URLBuilder appendPathSegments(final String segments)
   {
      if (segments != null)
      {
         String temp = segments.trim();

         // Only initialize the leading slash when adding the first path segments
         if (temp.startsWith("/") && this.segments.isEmpty())
         {
            metadata.setLeadingSlash(true);
         }
         if (temp.endsWith("/"))
         {
            metadata.setTrailingSlash(true);
         }

         String trimmedUrl = trimSurroundingSlashes(segments);

         // We reproduce this when building the URL by storing a single empty segment
         if (!trimmedUrl.isEmpty() || "//".equals(segments))
         {
            String[] newSegments = trimmedUrl.split("/", -1);
            this.segments.addAll(Arrays.asList(newSegments));
         }
      }
      else
      {
         throw new IllegalArgumentException("URL cannot be null.");
      }
      return this;
   }

   /**
    * Parse and add more query parameters to this {@link URLBuilder}
    */
   public URLBuilder addQueryParameters(final String parameters)
   {
      this.query.addParameters(parameters);
      return this;
   }

   /**
    * Return this {@link URLBuilder} after path segments and query parameters have been decoded.
    */
   public URLBuilder decode()
   {
      return new URLBuilder(getDecodedSegments(), metadata, query.decode());
   }

   /**
    * Return this {@link URLBuilder} after path segments and query parameters have been encoded.
    */
   public URLBuilder encode()
   {
      return new URLBuilder(getEncodedSegments(), metadata, query.encode());
   }

   private List<String> getDecodedSegments()
   {
      /*
       * Normal loop for performance reasons
       */
      List<String> result = new ArrayList<String>();
      for (int i = 0; i < segments.size(); i++) {
         result.add(Decoder.path(segments.get(i)));
      }
      return result;
   }

   private List<String> getEncodedSegments()
   {
      /*
       * Normal loop for performance reasons
       */
      List<String> result = new ArrayList<String>();
      for (int i = 0; i < segments.size(); i++) {
         result.add(Encoder.path(segments.get(i)));
      }
      return result;
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

   /**
    * Get the {@link QueryStringBuilder} object for this URL
    */
   public QueryStringBuilder getQueryStringBuilder()
   {
      return query;
   }

   /**
    * Set the {@link QueryStringBuilder} object for this URL
    */
   public URLBuilder setQueryString(final QueryStringBuilder query)
   {
      this.query = query;
      return this;
   }

   /**
    * Return all segments (separated by '/') in this URL
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
    */
   public URLBuilder setMetadata(final Metadata metadata)
   {
      this.metadata = metadata;
      return this;
   }

   /**
    * Return the portion of this URL representing the path.
    */
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

   /**
    * Return a URI representation of this URL including path and query string
    */
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
    * Return a String representation of this URL including path and query string
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
