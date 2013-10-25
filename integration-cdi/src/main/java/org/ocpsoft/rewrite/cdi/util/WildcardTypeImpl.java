package org.ocpsoft.rewrite.cdi.util;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Implementation of WildcardType
 * 
 * @author Bryn Cooke
 */
public class WildcardTypeImpl implements WildcardType
{

   private final Type[] upperBounds;
   private final Type[] lowerBounds;

   public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds)
   {

      this.upperBounds = upperBounds;
      this.lowerBounds = lowerBounds;
   }

   @Override
   public Type[] getUpperBounds()
   {
      return upperBounds;
   }

   @Override
   public Type[] getLowerBounds()
   {
      return lowerBounds;
   }
}
