package org.ocpsoft.rewrite.param;

import java.util.List;
import java.util.Map;

import org.ocpsoft.rewrite.bind.Binding;

/**
 * {@link Parameter} for {@link ParameterizedPattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ParameterizedPatternParameter<IMPLTYPE extends ParameterizedPatternParameter<IMPLTYPE, PARENTTYPE>, PARENTTYPE extends ParameterizedPattern<PARENTTYPE, IMPLTYPE>>
         extends ParameterBuilder<IMPLTYPE, String> implements ParameterizedPattern<PARENTTYPE, IMPLTYPE>
{
   protected final PARENTTYPE parent;

   private String pattern;
   private String name;

   public ParameterizedPatternParameter(PARENTTYPE parent, String name)
   {
      this.parent = parent;
      this.name = name;
   }

   /**
    * Get the pattern to which this {@link ParameterizedPatternParameter} must mach.
    */
   public String getPattern()
   {
      return pattern;
   }

   @Override
   public IMPLTYPE getParameter(String string)
   {
      return parent.getParameter(string);
   }

   @Override
   public Map<String, IMPLTYPE> getParameterMap()
   {
      return parent.getParameterMap();
   }

   @Override
   public List<String> getParameterNames()
   {
      return parent.getParameterNames();
   }

   /**
    * Set the pattern to which this {@link ParameterizedPatternParameter} must match.
    */
   @SuppressWarnings("unchecked")
   public IMPLTYPE matches(String pattern)
   {
      this.pattern = pattern;
      return (IMPLTYPE) this;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public IMPLTYPE where(String param)
   {
      return parent.where(param);
   }

   @Override
   public IMPLTYPE where(String param, Binding binding)
   {
      return parent.where(param, binding);
   }

   @Override
   public String toString()
   {
      return "ParameterizedPatternParameter [name=" + name + ", pattern=" + pattern + "]";
   }
}
