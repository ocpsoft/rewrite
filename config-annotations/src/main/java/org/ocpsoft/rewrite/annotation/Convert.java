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
package org.ocpsoft.rewrite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ocpsoft.rewrite.param.Converter;

/**
 * <p>
 * Tells Rewrite to use a converter to convert the value of a specific parameter binding.
 * </p>
 * 
 * There are various ways to specify the kind of converter you want to use. You could for example reference a converter
 * class this way:
 * 
 * <pre>
 * {@literal @}ParameterBinding
 * {@literal @}Convert(with = MyLocaleConverter.class)
 * private Locale locale;
 * </pre>
 * 
 * <p>
 * You can always refer to {@link Converter} implementations this way. Integration modules like the JSF integration
 * module provide support for frameworks specific converters on top of that.
 * </p>
 * 
 * <p>
 * You can also refer to a converter using an unique ID like this:
 * </p>
 * 
 * <pre>
 * {@literal @}ParameterBinding
 * {@literal @}Convert(id = "com.example.MyConverterId")
 * private Locale locale;
 * </pre>
 * 
 * <p>
 * Without any attributes set, Rewrite will try to find a converter by the type of the annotated field.
 * </p>
 * 
 * <pre>
 * {@literal @}ParameterBinding
 * {@literal @}Convert
 * private Locale locale;
 * </pre>
 * 
 * <p>
 * Please note that which of the three ways works in your case highly depends on the integration modules that you have
 * added to your application.
 * </p>
 * 
 * @author Christian Kaltepoth
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Convert
{

   /**
    * Can be used to refer to a converter class that should be used for conversion.
    */
   Class<?> with() default Object.class;

   /**
    * Can be used to refer to a converter by some kind of unique ID.
    */
   String id() default "";

}
