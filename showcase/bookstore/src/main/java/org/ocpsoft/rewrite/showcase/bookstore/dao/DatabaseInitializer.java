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
package org.ocpsoft.rewrite.showcase.bookstore.dao;

import java.io.InputStream;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

@Singleton
@Startup
public class DatabaseInitializer
{

   @Resource(mappedName = "java:jboss/datasources/ExampleDS")
   private DataSource dataSource;

   @PostConstruct
   public void init()
   {

      try {

         // get a connection to the in-memory database
         IDatabaseConnection connection = new DatabaseDataSourceConnection(dataSource);
         connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());

         // read the dataset
         InputStream datasetStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("dataset.xml");
         IDataSet dataset = new FlatXmlDataSetBuilder().build(datasetStream);

         // delete everything and do a new insert
         DatabaseOperation.CLEAN_INSERT.execute(connection, dataset);

      }
      catch (SQLException e) {
         throw new IllegalArgumentException(e);
      }
      catch (DataSetException e) {
         throw new IllegalArgumentException(e);
      }
      catch (DatabaseUnitException e) {
         throw new IllegalArgumentException(e);
      }

   }

}
