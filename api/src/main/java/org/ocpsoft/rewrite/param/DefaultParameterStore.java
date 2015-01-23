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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Assert;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.spi.GlobalParameterProvider;
import org.ocpsoft.rewrite.util.ServiceLogger;

/**
 * {@link Parameter} store which retains the order, bindings, and names of parameters contained within.
 */
public class DefaultParameterStore implements ParameterStore
{
    private final Map<String, Parameter<?>> parameters = new LinkedHashMap<String, Parameter<?>>();
    private static List<GlobalParameterProvider> providers;
    private static final Logger log = Logger.getLogger(DefaultParameterStore.class);

    @SuppressWarnings("unchecked")
    public DefaultParameterStore()
    {
        if (providers == null)
        {
            providers = Iterators.asList(ServiceLoader.load(GlobalParameterProvider.class));
            ServiceLogger.logLoadedServices(log, GlobalParameterProvider.class, providers);
        }

        for (GlobalParameterProvider provider : providers)
        {
            Set<Parameter<?>> params = provider.getParameters();
            if (params != null)
            {
                for (Parameter<?> parameter : params)
                    store(parameter);
            }
        }

    }

    @Override
    public Parameter<?> get(final String name, Parameter<?> deflt)
    {
        Parameter<?> parameter = null;
        if (parameters.get(name) != null)
        {
            parameter = parameters.get(name);
        }
        else
        {
            parameter = deflt;
            parameters.put(name, parameter);
        }

        if (parameter == null)
            throw new IllegalArgumentException("No such parameter [" + name + "] exists in parameter store.");

        return parameter;
    }

    @Override
    public Parameter<?> get(String name)
    {
        if (!parameters.containsKey(name))
            throw new IllegalArgumentException("No such parameter [" + name + "] exists in parameter store.");
        return parameters.get(name);
    }

    @Override
    public boolean isEmpty()
    {
        return parameters.isEmpty();
    }

    public Parameter<?> store(Parameter<?> value)
    {
        Assert.notNull(value, "Parameter to store must not be null.");
        return parameters.put(value.getName(), value);
    }

    @Override
    public int size()
    {
        return parameters.size();
    }

    @Override
    public Iterator<Entry<String, Parameter<?>>> iterator()
    {
        return parameters.entrySet().iterator();
    }

    @Override
    public boolean contains(String name)
    {
        return parameters.containsKey(name);
    }

    @Override
    public String toString()
    {
        return parameters.keySet().toString();
    }

    /**
     * Retrieve the current {@link ParameterStore} from the given {@link EvaluationContext} instance.
     * 
     * @throws IllegalStateException If the {@link ParameterValueStore} could not be located.
     */
    public static ParameterStore getInstance(EvaluationContext context) throws IllegalStateException
    {
        ParameterStore store = (ParameterStore) context.get(ParameterStore.class);
        if (store == null)
        {
            throw new IllegalStateException("Could not retrieve " + ParameterStore.class.getName() + " from "
                        + EvaluationContext.class.getName() + ". Has the " + EvaluationContext.class.getSimpleName()
                        + " been set up properly?");
        }
        return store;
    }
}
