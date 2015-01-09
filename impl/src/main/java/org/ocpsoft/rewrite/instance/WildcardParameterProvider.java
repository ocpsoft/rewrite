/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.ocpsoft.rewrite.instance;

import java.util.LinkedHashSet;
import java.util.Set;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.ImmutableParameter;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.RegexConstraint;
import org.ocpsoft.rewrite.spi.GlobalParameterProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class WildcardParameterProvider implements GlobalParameterProvider
{

    @Override
    public int priority()
    {
        return 0;
    }

    @Override
    public Set<Parameter<?>> getParameters()
    {
        Set<Parameter<?>> result = new LinkedHashSet<Parameter<?>>();
        result.add(new ImmutableParameter(new DefaultParameter("*").constrainedBy(new RegexConstraint(".*"))));
        return result;
    }

    @Override
    public boolean isValid(Rewrite event, EvaluationContext context, Parameter<?> param, String value)
    {
        return value.matches(".*");
    }

    @Override
    public boolean supportsSubmission(Rewrite event, EvaluationContext context, Parameter<?> param)
    {
        return false;
    }
}
