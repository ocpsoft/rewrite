/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.param;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Default implementation of {@link ParameterValueStore}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DefaultParameterValueStore implements ParameterValueStore, Iterable<Entry<Parameter<?>, List<String>>>
{
    Map<Parameter<?>, List<String>> map = new LinkedHashMap<Parameter<?>, List<String>>();

    /**
     * Create a new, empty {@link DefaultParameterValueStore} instance.
     */
    public DefaultParameterValueStore()
    {
    }

    /**
     * Create a new {@link DefaultParameterValueStore} instance, copying all {@link Parameter} and value pairs from the
     * given instance.
     */
    public DefaultParameterValueStore(DefaultParameterValueStore instance)
    {
        for (Entry<Parameter<?>, List<String>> entry : instance)
        {
            List<String> values = new ArrayList<String>();
            values.addAll(entry.getValue());
            map.put(entry.getKey(), values);
        }
    }

    @Override
    public String retrieve(Parameter<?> parameter)
    {
        List<String> strings = map.get(parameter);
        if (strings == null || strings.size() == 0)
            return null;
        if (strings.size() > 1)
            throw new IllegalStateException("Parameter [" + parameter.getName()
                        + "] is not a singleton: more than one value exists.");
        return strings.get(0);
    }

    @Override
    public boolean submit(Rewrite event, EvaluationContext context, Parameter<?> param, String value)
    {
        boolean result = false;
        List<String> strings = map.get(param);

        String stored = null;
        if (strings != null)
        {
            if (strings.size() > 0)
                stored = strings.get(0);

            if (strings.size() > 1)
                throw new IllegalStateException("Parameter [" + param.getName()
                            + "] is not a singleton: more than one value exists.");
        }

        if ("*".equals(param.getName()))
        {
            result = true;
        }
        else if (stored == value || (stored != null && stored.equals(value)))
        {
            result = true;
        }
        else if (stored == null)
        {
            result = _submit(event, context, param, value);
        }

        return result;
    }

    private boolean _submit(Rewrite event, EvaluationContext context, Parameter<?> param, String value)
    {
        boolean result = true;
        for (Constraint<String> constraint : param.getConstraints())
        {
            if (!constraint.isSatisfiedBy(event, context, value))
            {
                result = false;
            }
        }

        // FIXME Transposition processing will break multi-conditional matching
        if (result)
        {
            for (Transposition<String> transposition : param.getTranspositions())
            {
                value = transposition.transpose(event, context, value);
            }

            List<String> values = map.get(param);
            if (values == null)
            {
                values = new ArrayList<String>();
                map.put(param, values);
            }

            values.add(value);

            result = true;
        }
        return result;
    }

    @Override
    public Iterator<Entry<Parameter<?>, List<String>>> iterator()
    {
        return map.entrySet().iterator();
    }

    @Override
    public String toString()
    {
        return map.keySet().toString();
    }

    /**
     * Retrieve the current {@link ParameterValueStore} from the given {@link EvaluationContext} instance.
     * 
     * @throws IllegalStateException If the {@link ParameterValueStore} could not be located.
     */
    public static ParameterValueStore getInstance(EvaluationContext context) throws IllegalStateException
    {
        ParameterValueStore valueStore = (ParameterValueStore) context.get(ParameterValueStore.class);
        if (valueStore == null)
        {
            throw new IllegalStateException("Could not retrieve " + ParameterValueStore.class.getName() + " from "
                        + EvaluationContext.class.getName() + ". Has the " + EvaluationContext.class.getSimpleName()
                        + " been set up properly?");
        }
        return valueStore;
    }
}
