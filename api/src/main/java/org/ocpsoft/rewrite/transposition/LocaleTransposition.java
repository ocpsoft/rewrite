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
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.Parameters;
import org.ocpsoft.rewrite.param.Transposition;

/**
 * A {@link Transposition} and/or {@link Constraint} responsible for translating values from their matching translation
 * from a {@link ResourceBundle}. The lookup in the properties file is case-sensitive. The properties file should use
 * ISO-8859-1 encoding as defined by {@link PropertyResourceBundle}.
 * 
 * Requires inverted properties files in the form of translated_name=name_to_bind.
 * 
 * TODO Should we allow to load the properties file in a different encoding? TODO Allow to enable case insensitive
 * and/or ascii-folding on property lookup?
 * 
 * @author Christian Gendreau
 */
public class LocaleTransposition implements Transposition<String>, Constraint<String>
{
   // shared thread safe map between a String(representing the language) and a ResourceBundle.
   private static Map<String, ResourceBundle> bundleMap = new ConcurrentHashMap<String, ResourceBundle>();

   private final String languageParam;
   private final String bundleName;

   private Operation onFailureOperation;

   private LocaleTransposition(final String languageParam, final String bundleName)
   {
      Assert.notNull(languageParam, "Language must not be null.");
      Assert.notNull(bundleName, "Bundle must not be null.");

      // TODO ensure that we can find a resource bundle with the provided name in the classpath

      this.languageParam = languageParam;
      this.bundleName = bundleName;

   }

   /**
    * Translate a value into the matching one from a resource bundle in specified language.
    * 
    * @param lang
    * @param value
    * @return translated value or null if no bundle can be found for the specified language or no entries in the bundle
    *         match the given value.
    */
   private String translate(String lang, String value)
   {
      String translatation = null;
      if (value != null)
      {
         if (!bundleMap.containsKey(lang))
         {
            Locale locale = new Locale(lang);
            try
            {
               ResourceBundle loadedBundle = ResourceBundle.getBundle(bundleName, locale,
                        ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT));

               bundleMap.put(lang, loadedBundle);
            }
            catch (MissingResourceException e)
            {
               return null;
            }
         }

         try
         {
            // can we received more than one path section? e.g./search/service/
            translatation = bundleMap.get(lang).getString(value);
         }
         catch (MissingResourceException mrEx)
         {
            // ignore
         }
      }
      return translatation;
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
    * In the above scenario, "org.example.Paths" is the resource bundle name. The value of the {@link Parameter} "lang"
    * is extracted from the inbound request URL, and used as the bundle {@link Locale}. The initial value of the
    * {@link Parameter} "path" is used as the lookup key, and is transposed to the value of the corresponding resource
    * bundle entry. Once transposition has occurred, after rule evaluation, subsequent references to the "path"
    * {@link Parameter} will return the value from the {@link ResourceBundle} entry.
    * <p>
    * When this example is applied to a URL of: "/de/bibliotek", assuming a bundle called "org.example.Paths_de" exists
    * and contains the an entry "bibliotek=library", the rule will forward to the new URL: "/library", because the value
    * of "path" has been transposed by {@link LocaleTransposition}.
    * 
    * 
    * @param bundleName Fully qualified name of the {@link ResourceBundle}
    * @param localeParam The name of the {@link Parameter} that contains the ISO 639-1 language code to be used with
    *           {@link Locale}.
    * 
    * @return new instance of LocaleBinding
    */
   public static LocaleTransposition bundle(final String bundleName, final String localeParam)
   {
      return new LocaleTransposition(localeParam, bundleName);
   }

   /**
    * Specify an {@link Operation} to be added as a preOperation in case the {@link Transposition} failed. Failure
    * occurs when no {@link ResourceBundle} can be found for the requested language or when a value can not be
    * transposed due to a missing key in the resource bundle.
    * 
    * @param onFailureOperation
    * @return
    */
   public LocaleTransposition onTranspositionFailed(Operation onFailureOperation)
   {
      this.onFailureOperation = onFailureOperation;
      return this;
   }

   @Override
   public String transpose(Rewrite event, EvaluationContext context, String value)
   {
      // Retrieve the value of lang from the context
      String targetLang = (String) Parameters.retrieve(context, this.languageParam);
      String transposedValue = translate(targetLang, value);

      if (transposedValue == null)
      {
         if (onFailureOperation != null)
         {
            context.addPreOperation(onFailureOperation);
         }
         // if language is not defined, do not translate and keep original value.
         transposedValue = value;
      }
      return transposedValue;
   }

   @Override
   public boolean isSatisfiedBy(Rewrite event, EvaluationContext context, String value)
   {
      // Retrieve the value of lang from the context
      String targetLang = (String) Parameters.retrieve(context, this.languageParam);
      String translation = translate(targetLang, value);

      return (translation != null);
   }
}
