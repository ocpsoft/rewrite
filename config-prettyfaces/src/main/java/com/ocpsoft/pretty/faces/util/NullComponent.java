package com.ocpsoft.pretty.faces.util;

import javax.faces.component.UIComponentBase;

/**
 * This component exists only to provide Path and Query Validators with a component for which they should not throw
 * {@link NullPointerException}s
 * 
 * @author lb3
 */
public class NullComponent extends UIComponentBase
{
   @Override
   public String getFamily()
   {
      return "com.ocpsoft.pretty.NullComponent";
   }
}