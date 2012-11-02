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

package com.ocpsoft.pretty.faces.config.servlet;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class ServletMapping
{
    private String servletName;
    private String urlPattern;

    public String getServletName()
    {
        return servletName;
    }

    public void setServletName(final String servletName)
    {
        this.servletName = servletName;
        if (this.servletName != null)
        {
            this.servletName = this.servletName.trim();
        }
    }

    public String getUrlPattern()
    {
        return urlPattern;
    }

    public void setUrlPattern(final String urlPattern)
    {
        this.urlPattern = urlPattern;
        if (this.urlPattern != null)
        {
            this.urlPattern = this.urlPattern.trim();
        }
    }

    @Override
    public String toString()
    {
        return "ServletMapping [servletName=" + servletName + ", urlPattern=" + urlPattern + "]";
    }
}
