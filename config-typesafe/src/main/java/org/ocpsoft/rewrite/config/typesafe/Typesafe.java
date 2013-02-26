package org.ocpsoft.rewrite.config.typesafe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.spi.InstanceProvider;
import org.ocpsoft.rewrite.util.Instances;
import org.ocpsoft.rewrite.util.ValueHolderUtil;

public class Typesafe implements Operation, Parameterized
{
   private ParameterStore store;
   private final List<String> argNames = new ArrayList<String>();

   private Method method;
   private Object[] args;

   public static Typesafe method()
   {
      return new Typesafe();
   }

   @SuppressWarnings("unchecked")
   public <T> T invoke(Class<T> type)
   {
      try {
         Object o = Enhancer.create(type, new TypesafeMethodInterceptor(this));
         return (T) o;
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private static class TypesafeMethodInterceptor implements MethodInterceptor
   {
      private final Typesafe typesafe;

      public TypesafeMethodInterceptor(Typesafe typesafe)
      {
         this.typesafe = typesafe;
      }

      @Override
      public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
      {
         this.typesafe.method = method;
         this.typesafe.args = args;
         return null;
      }
   }

   @Override
   public void perform(Rewrite event, EvaluationContext context)
   {
      performInvoke(buildArguments(event, context));
   }

   private Object[] buildArguments(Rewrite event, EvaluationContext context)
   {
      Class<?> type = method.getDeclaringClass();

      if (argNames.size() != args.length)
      {
         throw new IllegalStateException("Invalid number of parameters specified in "
                  + Typesafe.class.getSimpleName() + " method invocation [" + buildSignature(type, method)
                  + "]. Expected [" + args.length + "] but got [" + argNames.size() + "]");
      }

      Object[] values = new Object[args.length];

      for (int i = 0; i < args.length; i++) {
         String arg = argNames.get(i);
         if (arg != null) {
            Parameter<?> parameter = store.get(arg);
            values[i] = ValueHolderUtil.convert(
                     event, context, parameter, Evaluation.property(arg).retrieve(event, context, parameter)
                     );
         }
         else
            values[i] = args[i];
      }
      return values;
   }

   public Object performInvoke(Object[] values)
   {
      Class<?> type = method.getDeclaringClass();

      if (values.length != args.length)
      {
         throw new IllegalStateException("Invalid number of parameters provided in "
                  + Typesafe.class.getSimpleName() + " method invocation [" + buildSignature(type, method)
                  + "]. Expected [" + args.length + "] but got [" + values.length + "]");
      }

      Object instance = Instances.lookup(type);

      if (instance == null)
         throw new IllegalStateException("Cannot invoke method [" + buildSignature(type, method)
                  + "] because no instance of type [" + type.getName()
                  + "] could be provided by any configured " + InstanceProvider.class.getSimpleName());

      try {
         return method.invoke(instance, values);
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public <T> T param(Class<T> type)
   {
      return param(type, null);
   }

   @SuppressWarnings("unchecked")
   public <T> T param(Class<T> type, String name)
   {
      T result = null;

      if (type != null && (boolean.class.isAssignableFrom(type)))
         result = (T) Boolean.FALSE;

      if (type != null && (byte.class.isAssignableFrom(type)))
         result = (T) new Byte("0");

      if (type != null && (char.class.isAssignableFrom(type)))
         result = (T) new Character('0');

      if (type != null && (double.class.isAssignableFrom(type)))
         result = (T) new Double(0);

      if (type != null && (float.class.isAssignableFrom(type)))
         result = (T) new Float(0);

      if (type != null && (int.class.isAssignableFrom(type)))
         result = (T) new Integer(0);

      if (type != null && (long.class.isAssignableFrom(type)))
         result = (T) new Long(0);

      if (type != null && (short.class.isAssignableFrom(type)))
         result = (T) new Short("0");

      this.argNames.add(name);
      return result;
   }

   /*
    * Helpers
    */
   private String buildSignature(Class<?> type, Method method2)
   {
      String result = type.getSimpleName() + "." + method.getName();

      result += "(";
      Class<?>[] types = method.getParameterTypes();

      boolean first = true;
      for (Class<?> paramType : types) {
         if (!first)
         {
            result += ", ";
         }
         result += paramType.getSimpleName();
         first = false;
      }

      result += ")";
      return result;
   }

   public Method getMethod()
   {
      return method;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      return new HashSet<String>(argNames);
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      this.store = store;
   }
}