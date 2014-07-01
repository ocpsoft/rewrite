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
package org.ocpsoft.rewrite.servlet.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.ocpsoft.common.util.Strings;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.RewriteWrappedRequest;
import org.ocpsoft.rewrite.servlet.config.encodequery.Base64EncodingStrategy;
import org.ocpsoft.rewrite.servlet.config.encodequery.ChecksumStrategy;
import org.ocpsoft.rewrite.servlet.config.encodequery.EncodingStrategy;
import org.ocpsoft.rewrite.servlet.config.encodequery.HashCodeChecksumStrategy;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.util.QueryStringBuilder;
import org.ocpsoft.rewrite.servlet.util.URLBuilder;
import org.ocpsoft.rewrite.util.Maps;
import org.ocpsoft.urlbuilder.Address;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * An {@link Operation} that encodes any or many {@link Address} query-parameters into a single parameter using the
 * given {@link ChecksumStrategy} and {@link EncodingStrategy}. This can be used to effectively encrypt or obfuscate
 * inbound and outbound query-parameters. Additionally, encoded parameters contain a checksum which can be used to
 * reveal tampering, allowing for appropriate action to be taken on checksum verification failure.
 * 
 * <p>
 * For example:<br/>
 * <code>?c=LTg1NDM0OTA1OSM&lang=en_US</code>
 * <p>
 * The above query string contains multiple parameters. The value of parameter 'c' has been encoded using
 * {@link EncodeQuery#to(String)} and has specified that <code>lang</code> be excluded via {@link #excluding(String...)}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@SuppressWarnings("deprecation")
public class EncodeQuery implements Operation
{
   private String tokenName;
   private ChecksumStrategy checksumStrategy = new HashCodeChecksumStrategy();
   private EncodingStrategy encodingStrategy = new Base64EncodingStrategy();
   private final List<String> params = new ArrayList<String>();
   private final List<String> excludedParams = new ArrayList<String>();
   private boolean inboundCorrection = true;
   private Operation onfailure;

   private EncodeQuery(final String[] params)
   {
      if ((params != null) && (params.length > 0))
         this.params.addAll(Arrays.asList(params));
   }

   /**
    * Create a new {@link EncodeQuery} operation for the given query-parameter names. Only encode those given
    * parameters. If no parameter names are supplied, this will encode all query-parameters found in the {@link Address}
    * .
    */
   public static EncodeQuery params(final String... params)
   {
      return new EncodeQuery(params);
   }

   /**
    * Exclude the given query-parameter names from encoding.
    */
   public EncodeQuery excluding(final String... params)
   {
      if ((params != null) && (params.length > 0))
         this.excludedParams.addAll(Arrays.asList(params));
      return this;
   }

   /**
    * Use the given {@link EncodingStrategy} when performing encoding.
    */
   public EncodeQuery withEncodingStrategy(final EncodingStrategy strategy)
   {
      this.encodingStrategy = strategy;
      return this;
   }

   /**
    * Use the given {@link ChecksumStrategy} when verifying and embedding checksums.
    */
   public EncodeQuery withChecksumStrategy(final ChecksumStrategy strategy)
   {
      this.checksumStrategy = strategy;
      return this;
   }

   /**
    * Redirect inbound requests to an {@link Address} containing matching query-parameters to the encoded
    * {@link Address}.
    */
   public EncodeQuery withInboundCorrection(final boolean enable)
   {
      inboundCorrection = enable;
      return this;
   }

   /**
    * {@link Operation} to be performed when the current {@link ChecksumStrategy} detects an inbound checksum failure.
    */
   public EncodeQuery onChecksumFailure(final Operation operation)
   {

      this.onfailure = operation;
      return this;
   }

   /**
    * The name of the composite query-parameter to hold the encoded parameters.
    */
   public EncodeQuery to(final String param)
   {
      this.tokenName = param;
      return this;
   }

   @Override
   public void perform(final Rewrite event, final EvaluationContext context)
   {
      if ((event instanceof HttpInboundServletRewrite) && DispatchType.isRequest().evaluate(event, context))
      {
         HttpInboundServletRewrite in = (HttpInboundServletRewrite) event;

         QueryStringBuilder query = QueryStringBuilder.createNew();
         query.addParameters(in.getInboundAddress().getQuery());

         String token = query.decode().getParameter(tokenName);
         if (token != null)
         {
            String decoded = encodingStrategy.decode(token);

            if (checksumStrategy.checksumValid(decoded))
            {
               decoded = checksumStrategy.removeChecksum(decoded);
               query.removeParameter(tokenName);
               QueryStringBuilder queryParams = QueryStringBuilder.createFromEncoded(decoded);
               RewriteWrappedRequest request = RewriteWrappedRequest.getCurrentInstance(in.getRequest());
               for (Entry<String, List<String>> param : queryParams.getParameterMap().entrySet()) {
                  for (String value : param.getValue()) {
                     Maps.addArrayValue(request.getModifiableParameters(), param.getKey(), value);
                  }
               }
            }
            else if (onfailure != null)
            {
               onfailure.perform(event, context);
            }
         }
         else if (!query.isEmpty() && inboundCorrection)
         {
            in.redirectTemporary(in.getAddress().getPathAndQuery());
         }
      }

      else if (event instanceof HttpOutboundServletRewrite)
      {
         HttpOutboundServletRewrite out = (HttpOutboundServletRewrite) event;

         String outboundURL = out.getOutboundAddress().toString();
         URLBuilder url = URLBuilder.createFrom(outboundURL);

         url.getQueryStringBuilder().removeParameter(tokenName);

         QueryStringBuilder newQuery = QueryStringBuilder.createNew();
         for (String param : excludedParams) {
            newQuery.addParameter(param, url.getQueryStringBuilder().removeParameter(param).toArray(new String[] {}));
         }

         if (!params.isEmpty())
         {
            for (String param : url.getQueryStringBuilder().getParameterNames())
            {
               if (!params.contains(param))
               {
                  newQuery.addParameter(param,
                           url.getQueryStringBuilder().removeParameter(param).toArray(new String[] {}));
               }
            }
         }

         if (outboundURL.contains("?") && (outboundURL.startsWith(out.getContextPath()) || outboundURL.startsWith("/")))
         {
            if (!url.getQueryStringBuilder().isEmpty())
            {
               String encoded = checksumStrategy.embedChecksum(url.getQueryStringBuilder().toQueryString());
               encoded = encodingStrategy.encode(encoded);

               newQuery.addParameter(tokenName, encoded);
               out.setOutboundAddress(AddressBuilder.create(url.toPath() + newQuery.toQueryString()));
            }
         }
      }
   }

   @Override
   public String toString()
   {
      return "EncodeQuery.params(\"" + Strings.join(params, "\", \"") + "\")";
   }

}
