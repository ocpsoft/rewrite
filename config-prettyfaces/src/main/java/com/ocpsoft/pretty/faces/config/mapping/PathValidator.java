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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ocpsoft.pretty.faces.el.ConstantExpression;
import com.ocpsoft.pretty.faces.el.PrettyExpression;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PathValidator
{
    private int index;
    private String validatorIds = "";
    private PrettyExpression validatorExpression = null;
    private String onError = "";

    public PathValidator()
    {}

    public PathValidator(final int index, final String validatorIds, final String onError)
    {
        this.index = index;
        this.validatorIds = validatorIds;
        this.onError = onError;
    }

    public boolean hasValidators()
    {
        return validatorIds.trim().length() > 0;
    }

    public List<String> getValidatorIdList()
    {
        List<String> result = new ArrayList<String>();
        if (hasValidators())
        {
            String[] ids = validatorIds.split(" ");
            Collections.addAll(result, ids);
        }
        return result;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(final int index)
    {
        this.index = index;
    }

    public String getValidatorIds()
    {
        return validatorIds;
    }

    public void setValidatorIds(final String validatorIds)
    {
        this.validatorIds = validatorIds;
    }

    public String getOnError()
    {
        return onError;
    }

    public void setOnError(final String onError)
    {
        this.onError = onError;
    }

    public PrettyExpression getValidatorExpression()
    {
       return validatorExpression;
    }

    public void setValidatorExpression(PrettyExpression validatorExpression)
    {
       this.validatorExpression = validatorExpression;
    }

    /**
     * Extra setter method creating a {@link ConstantExpression}.
     * Used only for Digester only.
     */
    public void setValidator(final String validator)
    {
       this.validatorExpression = new ConstantExpression(validator);
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (onError == null ? 0 : onError.hashCode());
        result = prime * result + index;
        result = prime * result + (validatorIds == null ? 0 : validatorIds.hashCode());
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
        PathValidator other = (PathValidator) obj;
        if (onError == null)
        {
            if (other.onError != null)
            {
                return false;
            }
        }
        else if (!onError.equals(other.onError))
        {
            return false;
        }
        if (index != other.index)
        {
            return false;
        }
        if (validatorIds == null)
        {
            if (other.validatorIds != null)
            {
                return false;
            }
        }
        else if (!validatorIds.equals(other.validatorIds))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "PathValidator [index=" + index + ", onError=" + onError + ", validatorIds=" + validatorIds + "]";
    }
}
