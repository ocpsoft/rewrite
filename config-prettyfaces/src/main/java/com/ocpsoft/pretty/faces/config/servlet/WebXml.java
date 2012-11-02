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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class WebXml
{
    private final List<ServletDefinition> servlets = new ArrayList<ServletDefinition>();
    private final List<ServletMapping> servletMappings = new ArrayList<ServletMapping>();

    public void addServlet(final ServletDefinition servlet)
    {
        servlets.add(servlet);
    }

    public void addServletMapping(final ServletMapping mapping)
    {
        servletMappings.add(mapping);
    }

    public List<ServletDefinition> getServlets()
    {
        return servlets;
    }

    public List<ServletMapping> getServletMappings()
    {
        return servletMappings;
    }

    @Override
    public String toString()
    {
        return "WebXml [servletMappings=" + servletMappings + ", servlets=" + servlets + "]";
    }
}
