package org.ocpsoft.urlbuilder;

import java.util.Arrays;
import java.util.List;

import org.ocpsoft.urlbuilder.util.Encoder;

class Parameter
{

   private final CharSequence name;
   private final List<Object> values;
   private final boolean encode;

   private Parameter(CharSequence name, boolean encode, List<Object> values)
   {
      this.name = name;
      this.encode = encode;
      this.values = values;
   }

   public static Parameter create(CharSequence name, Object... values)
   {
      return new Parameter(name, true, Arrays.asList(values));
   }

   public static Parameter create(CharSequence name, boolean encode, Object... values)
   {
      return new Parameter(name, encode, Arrays.asList(values));
   }

   public CharSequence getName()
   {
      return name;
   }

   public boolean isEncode()
   {
      return encode;
   }

   public boolean hasValues()
   {
      return !values.isEmpty();
   }

   public int getValueCount()
   {
      return values.size();
   }

   public String getValueAsPathParam(int index)
   {
      if (encode) {
         return Encoder.path(values.get(index).toString());
      }
      else {
         return values.get(index).toString();
      }
   }

   public String getValueAsQueryParam(int index)
   {
      Object value = values.get(index);
      if (encode) {
         return Encoder.query(value == null ? null : value.toString());
      }
      else {
         return value == null ? null : value.toString();
      }
   }

}
