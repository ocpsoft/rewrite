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
package org.ocpsoft.rewrite.config.tuckey;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.UrlRewriter;

import org.ocpsoft.rewrite.config.Configuration;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class TuckeyConfigurationProvider extends HttpConfigurationProvider
{
   Logger log = Logger.getLogger(TuckeyConfigurationProvider.class);
   private UrlRewriter urlRewriter;

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      loadUrlRewriteConfig(context);
      ConfigurationBuilder config = ConfigurationBuilder.begin();
      if (urlRewriter != null)
      {
         config.addRule(new TuckeyRewriteRuleAdaptor(urlRewriter));
      }
      return config;
   }

   private void loadUrlRewriteConfig(final ServletContext context)
   {
      String confPath = "/WEB-INF/urlrewrite.xml";
      InputStream inputStream = context.getResourceAsStream(confPath);
      // attempt to retrieve from location other than local WEB-INF
      if (inputStream == null) {
         inputStream = ClassLoader.getSystemResourceAsStream(confPath);
      }
      URL confUrl = null;
      try {
         confUrl = context.getResource(confPath);
      }
      catch (MalformedURLException e) {
         log.debug("Error", e);
      }
      String confUrlStr = null;
      if (confUrl != null) {
         confUrlStr = confUrl.toString();
      }
      if (inputStream == null) {
         log.error("unable to find urlrewrite conf file at " + confPath);
         // set the writer back to null
         if (urlRewriter != null) {
            log.error("unloading existing conf");
            urlRewriter = null;
         }

      }
      else {
         Conf conf = new Conf(context, inputStream, confPath, confUrlStr, false);
         checkConfLocal(conf);
      }
   }

   private void checkConfLocal(final Conf conf)
   {
      if (conf.isOk() && conf.isEngineEnabled()) {
         urlRewriter = new UrlRewriter(conf);
         log.debug("Tuckey UrlRewriteFilter configuration loaded (Status: OK)");
      }
      else {
         if (!conf.isOk()) {
            log.error("Tuckey UrlRewriteFilter configuration failed (Status: ERROR)");
         }
         if (!conf.isEngineEnabled()) {
            log.warn("Tuckey UrlRewriteFilter engine explicitly disabled in configuration");
         }
         if (urlRewriter != null) {
            log.debug("Tuckey UrlRewriteFilter configuration unloaded");
            urlRewriter = null;
         }
      }
   }

   @Override
   public int priority()
   {
      return 100;
   }

}
