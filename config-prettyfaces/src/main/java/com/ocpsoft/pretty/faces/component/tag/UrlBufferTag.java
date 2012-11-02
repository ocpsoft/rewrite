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
package com.ocpsoft.pretty.faces.component.tag;

import javax.faces.component.UIComponent;

import com.ocpsoft.pretty.faces.component.PrettyTagBase;
import com.ocpsoft.pretty.faces.component.UrlBuffer;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class UrlBufferTag extends PrettyTagBase
{
    private String var;
    private String mappingId;
    private String relative;

    @Override
    public String getComponentType()
    {
        return UrlBuffer.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType()
    {
        return UrlBuffer.RENDERER_TYPE;
    }

    @Override
    public void release()
    {
        super.release();
        var = null;
    }

    @Override
    protected void setProperties(final UIComponent component)
    {
        super.setProperties(component);
        setAttributeProperites(component, "var", var);
        setAttributeProperites(component, "mappingId", mappingId);
        setAttributeProperites(component, "relative", relative);

    }

    public String getVar()
    {
        return var;
    }

    public void setVar(final String var)
    {
        this.var = var;
    }

    public String getMappingId()
    {
        return mappingId;
    }

    public void setMappingId(final String mappingId)
    {
        this.mappingId = mappingId;
    }

    public String getRelative()
    {
       return relative;
    }

    public void setRelative(final String relative)
    {
      this.relative = relative;
    }
}
