/*
 * Copyright 2010 Lincoln Baxter, III
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
package com.ocpsoft.pretty.faces.config.mapping;

import com.ocpsoft.pretty.faces.el.ConstantExpression;
import com.ocpsoft.pretty.faces.el.PrettyExpression;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public abstract class RequestParameter
{
    private PrettyExpression expression;
    private String value = "";
    private String name = "";

    public RequestParameter()
    {}

    public RequestParameter(final String name, final String value)
    {
        super();
        this.name = name;
        this.value = value;
    }

    public RequestParameter(final String name, final String value, final PrettyExpression expression)
    {
        super();
        this.name = name;
        this.value = value;
        this.expression = expression;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public PrettyExpression getExpression()
    {
        return expression;
    }

    public String getValue()
    {
        return value;
    }

    public void setExpression(final PrettyExpression expression)
    {
        this.expression = expression;
    }

    /**
     * Extra setter method creating a {@link ConstantExpression}.
     * Used only for Digester only.
     */
    public void setExpression(final String expression)
    {
        this.expression = new ConstantExpression(expression);
    }

    public void setValue(final String value)
    {
        this.value = value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (expression == null ? 0 : expression.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        RequestParameter other = (RequestParameter) obj;
        if (expression == null)
        {
            if (other.expression != null)
            {
                return false;
            }
        }
        else if (!expression.equals(other.expression))
        {
            return false;
        }
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "RequestParameter [expression=" + expression + ", name=" + name + ", value=" + value + "]";
    }
}
