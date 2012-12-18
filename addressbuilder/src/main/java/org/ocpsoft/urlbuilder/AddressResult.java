package org.ocpsoft.urlbuilder;

import java.util.Map;

import org.ocpsoft.urlbuilder.util.CaptureType;
import org.ocpsoft.urlbuilder.util.CapturingGroup;
import org.ocpsoft.urlbuilder.util.Encoder;
import org.ocpsoft.urlbuilder.util.ParseTools;

class AddressResult implements Address
{
   private final String protocol;
   private final String host;
   private final Integer port;
   private final String path;
   private final String query;
   private final String anchor;
   private CharSequence result;

   public AddressResult(AddressBuilder parent)
   {
      if (isSet(parent.protocol))
         protocol = parameterize(parent.parameters, parent.protocol).toString();
      else
         protocol = null;

      if (isSet(parent.host))
         host = parameterize(parent.parameters, parent.host).toString();
      else
         host = null;

      if (isSet(parent.port))
         port = parent.port;
      else
         port = null;

      if (isSet(parent.path))
      {
         CharSequence path = parameterize(parent.parameters, parent.path);
         if (path.charAt(0) != '/')
            path = new StringBuilder('/').append(path);
         this.path = path.toString();
      }
      else
         path = null;

      if (isSet(parent.queries))
         query = toQuery(parent.queries).toString();
      else
         query = null;

      if (isSet(parent.anchor))
         anchor = parameterize(parent.parameters, parent.anchor).toString();
      else
         anchor = null;
   }

   private CharSequence toQuery(Map<CharSequence, Parameter> queries)
   {
      StringBuilder result = new StringBuilder();
      boolean first = true;
      for (CharSequence name : queries.keySet()) {
         Parameter parameter = queries.get(name);

         if (!first)
            result.append('&');
         else
            first = false;

         result.append(name);

         if (parameter.getValueCount() > 0)
         {
            for (int i = 0; i < parameter.getValueCount(); i++) {
               String value = parameter.getValueAsQueryParam(i);

               if (value != null)
                  result.append('=').append(value);

               if (i < parameter.getValueCount() - 1)
               {
                  result.append('&').append(name);
               }
            }
         }
      }
      return result;
   }

   @Override
   public String toString()
   {
      if (this.result == null)
      {
         StringBuilder result = new StringBuilder();

         if (isProtocolSet())
            result.append(getProtocol()).append("://");

         if (isHostSet())
            result.append(getHost());

         if (isPortSet())
            result.append(":").append(getPort());

         if (isPathSet())
            result.append(getPath());

         if (isQuerySet())
            result.append('?').append(getQuery());

         if (isAnchorSet())
            result.append('#').append(getAnchor());

         this.result = result;
      }

      return this.result.toString();
   }

   private CharSequence parameterize(Map<CharSequence, Parameter> parameters, CharSequence sequence)
   {
      StringBuilder result = new StringBuilder();
      int cursor = 0;
      int lastEnd = 0;
      while (cursor < sequence.length())
      {
         switch (sequence.charAt(cursor))
         {
         case '{':
            result.append(Encoder.path(sequence.subSequence(lastEnd, cursor)));

            int startPos = cursor;
            CapturingGroup group = ParseTools.balancedCapture(sequence, startPos, sequence.length() - 1,
                     CaptureType.BRACE);
            cursor = group.getEnd();
            lastEnd = group.getEnd() + 1;

            String name = group.getCaptured().toString();

            Parameter parameter = parameters.get(name);
            if (parameter == null || !parameter.hasValues())
               throw new IllegalStateException("No parameter [" + name + "] was set in the pattern [" + sequence
                        + "]. Call address.set(\"" + name + "\", value); or remove the parameter from the pattern.");

            result.append(parameter.getValueAsPathParam(0));

            break;

         default:
            break;
         }

         cursor++;
      }

      if (cursor >= lastEnd)
         result.append(Encoder.path(sequence.subSequence(lastEnd, cursor)));
      return result;
   }

   private boolean isSet(Integer port)
   {
      return port != null;
   }

   private boolean isSet(Map<?, ?> map)
   {
      return map != null && !map.isEmpty();
   }

   private boolean isSet(CharSequence value)
   {
      return value != null && value.length() > 0;
   }

   /*
    * Inspectors
    */

   @Override
   public String getAnchor()
   {
      return anchor;
   }

   @Override
   public boolean isAnchorSet()
   {
      return isSet(anchor);
   }

   @Override
   public String getPath()
   {
      return path;
   }

   @Override
   public String getPathAndQuery()
   {
      StringBuilder result = new StringBuilder();
      if (isPathSet())
         result.append(getPath());
      if (isQuerySet())
         result.append('?').append(getQuery());
      return result.toString();
   }

   @Override
   public boolean isPathSet()
   {
      return isSet(path);
   }

   @Override
   public Integer getPort()
   {
      return port;
   }

   @Override
   public boolean isPortSet()
   {
      return isSet(port);
   }

   @Override
   public String getHost()
   {
      return host;
   }

   @Override
   public boolean isHostSet()
   {
      return isSet(host);
   }

   @Override
   public String getProtocol()
   {
      return protocol;
   }

   @Override
   public boolean isProtocolSet()
   {
      return isSet(protocol);
   }

   @Override
   public String getQuery()
   {
      return query;
   }

   @Override
   public boolean isQuerySet()
   {
      return isSet(query);
   }
}
