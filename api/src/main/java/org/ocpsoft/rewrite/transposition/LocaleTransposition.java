/*
 * Copyright 2014 Université de Montréal
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
package org.ocpsoft.rewrite.transposition;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.Parameters;
import org.ocpsoft.rewrite.param.Transposition;

/**
 * A {@link Transposition} responsible for translating values from their matching translation from a
 * {@link ResourceBundle}. The lookup in the properties file is case-sensitive.
 * 
 * Requires inverted properties files in the form of translated_name=name_to_bind.
 * 
 * TODO Should we allow to load the properties file in a different encoding? TODO Allow to enable case insensitive
 * and/or ascii-folding on property lookup
 * 
 * @author Christian Gendreau
 */
public class LocaleTransposition implements Transposition<String>
{

   // shared thread safe map between a String(representing the language) and a ResourceBundle.
   private static Map<String, ResourceBundle> bundleMap = new ConcurrentHashMap<String, ResourceBundle>();

   private final String languageParam;
   private final String bundleName;

   private LocaleTransposition(final String languageParam, final String bundleName)
   {
      this.languageParam = languageParam;
      this.bundleName = bundleName;
   }

   /**
    * Create a {@link Binding} to a {@link ResourceBundle}, where the initial bound {@link Parameter} value is used as
    * the bundle lookup key. The resultant value of the bundle lookup is stored as the new bound {@link Parameter}
    * value.
    * <p>
    * For example, consider the following URL-based rule:
    * 
    * <pre>
    * Configuration config = ConfigurationBuilder.begin()
    *          .addRule(Join.path(&quot;/{lang}/{path}&quot;).to(&quot;/{path}&quot;))
    *          .where(&quot;path&quot;).transposedBy(LocaleTransposition.bundle(&quot;org.example.Paths&quot;, &quot;lang&quot;));
    * </pre>
    * <p>
    * In the above scenario, "org.example.Paths" is the resource bundle name. The value of the {@link Parameter} "lang" is used
    * as the bundle {@link Locale}. The initial value of the {@link Parameter} "path" is used as the lookup key, and is
    * transposed to the value of the corresponding resource bundle entry. Once transposition has occurred, after rule
    * evaluation, subsequent references to the "path" {@link Parameter} will return the value from the
    * {@link ResourceBundle} entry.
    * <p>
    * When this example is applied to a URL of: "/de/bibliotek", assuming a bundle called "org.example.Paths_de" exists
    * and contains the an entry "bibliotek=library", the rule will forward to the new URL: "/library", because the value
    * of "path" has been transposed by {@link LocaleTransposition}.
    * 
    * 
    * @param bundleName Fully qualified name of the {@link ResourceBundle}
    * @param localeParam The name of the {@link Parameter} that contains the {@link Locale} code.
    * 
    * @return new instance of LocaleBinding
    */
   public static LocaleTransposition bundle(final String bundleName, final String localeParam)
   {
      return new LocaleTransposition(localeParam, bundleName);
   }

   @Override
   public String transpose(Rewrite event, EvaluationContext context, String value)
   {
      String transposedValue = null;

      // FIXME this is currently failing due to the absence of the languageParam parameter in the context
      // Retrieve the value of lang from the context
      String targetLang = (String) Parameters.retrieve(context, this.languageParam);
      if (value != null)
      {
         if (!bundleMap.containsKey(targetLang))
         {
            Locale locale = new Locale(targetLang);
            try
            {
               ResourceBundle loadedBundle = ResourceBundle.getBundle(bundleName, locale);
               bundleMap.put(targetLang, loadedBundle);
            }
            catch (MissingResourceException e)
            {
               throw new RewriteException("Error occurred during Locale transposition of parameter value [" + value
                        + "] via bundle [" + bundleName + "_" + targetLang + "]", e);
            }
         }

         if (bundleMap.containsKey(targetLang)) {
            try
            {
               // can we received more than one path section? e.g./search/service/
               transposedValue = bundleMap.get(targetLang).getString(value);
            }
            catch (MissingResourceException mrEx)
            {
               // if not found, do not translate and keep original value
               transposedValue = value;
            }
         }
         else
         {
            // if language is not defined, do not translate and keep original value
            transposedValue = value;
         }
      }
      return transposedValue;
   }
}
