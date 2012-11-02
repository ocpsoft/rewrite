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
public class QueryParameter extends RequestParameter
{
    private String validatorIds = "";
    private PrettyExpression validatorExpression;
    private String onError = "";
    private boolean onPostback = true;

    public QueryParameter()
    {
        super();
    }

    public QueryParameter(final String name, final String value)
    {
        super(name, value);
    }

    public QueryParameter(final String name, final String value, final PrettyExpression expression)
    {
        super(name, value, expression);
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
     * Extra setter method creating a {@link ConstantExpression} for the
     * validatorExpression. Used only for Digester only.
     */
    public void setValidator(String validator)
    {
       this.validatorExpression = new ConstantExpression(validator);
    }

    public boolean isOnPostback()
    {
       return onPostback;
    }

    public void setOnPostback(boolean onPostback)
    {
       this.onPostback = onPostback;
    }

}
