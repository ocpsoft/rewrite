/*
 * Copyright 2014 Christian Gendreau
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
package org.ocpsoft.rewrite.servlet.config.bind;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Parameter;

/**
 * A {@link Binding} responsible for storing and retrieving {@link Parameter} values with their matching
 * translation from a ResourceBundle file.
 * 
 * Requires inverted property files in the form of translated_name=name_to_bind.
 * 
 * @author Christian Gendreau
 */
public class LocaleBinding implements Binding {

	// shared thread safe map between a String(representing the language) and a ResourceBundle.
	private static Map<String, ResourceBundle> bundleMap = new ConcurrentHashMap<String, ResourceBundle>();

	private final String languageParam;
	private final String bundleName;
	private String bindedValue;

	private LocaleBinding(final String languageParam, final String bundleName)
	{
		this.languageParam = languageParam;
		this.bundleName = bundleName;
	}
	
	/**
	 * Create a {@link Binding} to a resource bundle.
	 * @param languageParam name of the {@link Parameter} that contains the language.
	 * @param bundleName name of the {@link ResourceBundle}
	 * @return new instance of LocaleBinding
	 */
	public static LocaleBinding bundle(final String languageParam, final String bundleName)
	{
		return new LocaleBinding(languageParam, bundleName);
	}

	@Override
	public Object retrieve(Rewrite event, EvaluationContext context)
	{
		return bindedValue;
	}

	@Override
	public Object submit(Rewrite event, EvaluationContext context, Object value)
	{
		String targetLang = (String) Evaluation.property(this.languageParam).retrieve(event, context);
		if (value != null)
		{
			if (!bundleMap.containsKey(targetLang))
			{
				Locale locale = new Locale(targetLang);
				try
				{
					ResourceBundle loadedBundle = ResourceBundle.getBundle(bundleName, locale);
					bundleMap.put(targetLang, loadedBundle);
				} catch (MissingResourceException mrEx)
				{
					// ignore for now
				}
			}
			
			if (bundleMap.containsKey(targetLang)) {
				try
				{
					// can we received more than one path section? e.g./search/service/
					bindedValue = bundleMap.get(targetLang).getString(value.toString());
				} catch (MissingResourceException mrEx)
				{
					// if not found, do not translate and keep original value
					bindedValue = value.toString();
				}
			}
			else
			{
				// if language is not defined, do not translate and keep original value
				bindedValue = value.toString();
			}
			//Should we store the bindedValue in the context?
		}
		return null;
	}

	@Override
	public boolean supportsRetrieval()
	{
		return true;
	}

	@Override
	public boolean supportsSubmission()
	{
		return true;
	}
}
