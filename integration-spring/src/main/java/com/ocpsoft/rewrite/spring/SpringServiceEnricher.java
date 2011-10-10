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
package com.ocpsoft.rewrite.spring;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.ocpsoft.common.spi.ServiceEnricher;
import com.ocpsoft.logging.Logger;

/**
 * {@link ServiceEnricher} implementation for Spring.
 * 
 * @author Christian Kaltepoth
 */
public class SpringServiceEnricher implements ServiceEnricher {

    private final Logger log = Logger.getLogger(SpringServiceEnricher.class);

    @Override
    public <T> T enrich(T service) {

        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(service);

        log.debug("Enriched instance of service [" + service.getClass().getName() + "]");

        return service;

    }

    @Override
    public <T> T produce(Class<T> service) {
        return null;
    }

}
