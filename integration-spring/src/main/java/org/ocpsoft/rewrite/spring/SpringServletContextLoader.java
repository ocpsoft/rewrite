/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ocpsoft.rewrite.servlet.spi.ContextListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

/**
 * Thread-safe {@link ServletContext} loader implementation for Spring.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class SpringServletContextLoader implements ContextListener
{
    private static final Map<ClassLoader, ServletContext> contextMap = new ConcurrentHashMap<>(1);

    @Override
    public void contextInitialized(ServletContextEvent event)
    {
        ServletContext servletContext = event.getServletContext();
        contextMap.put(Thread.currentThread().getContextClassLoader(), servletContext);
        contextMap.put(servletContext.getClassLoader(), servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event)
    {
        ServletContext context = event.getServletContext();
        contextMap.entrySet().removeIf(entry -> entry.getValue() == context);
    }

    public static ServletContext findCurrentServletContext()
    {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest().getServletContext();
        }

        return contextMap.get(Thread.currentThread().getContextClassLoader());
    }

    public static WebApplicationContext findCurrentApplicationContext()
    {
        ServletContext currentServletContext = findCurrentServletContext();

        if (currentServletContext != null) {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils
                        .findWebApplicationContext(currentServletContext);
            if (webApplicationContext != null) {
                return webApplicationContext;
            }
        }

        return ContextLoader.getCurrentWebApplicationContext();
    }

    @Override
    public int priority()
    {
        return 0;
    }

}
