package org.ocpsoft.rewrite.param;

/**
 * A default implementation of {@link Parameter}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DefaultParameter extends ParameterBuilder<DefaultParameter> implements Parameter<DefaultParameter>
{
   public DefaultParameter(String name)
   {
      super(name);
   }
}
