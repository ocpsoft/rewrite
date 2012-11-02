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
package com.ocpsoft.pretty.faces.config.annotation;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

/**
 * <p>
 * This class reads Java class files and checks whether they contain references
 * to PrettyFaces annotations. This allows to check classes for the presence
 * of annotations before instantiating them.
 * </p>
 * 
 * <p>
 * The filter is inspired by the <code>ClassByteCodeAnnotationFilter</code>
 * of Apache MyFaces 2.0. Many thanks go out to Leonardo Uribe for
 * his great work on this class.
 * </p>
 * 
 * @see http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html
 * @see http://en.wikipedia.org/wiki/Class_%28file_format%29
 * 
 * @author Christian Kaltepoth
 */
public class ByteCodeAnnotationFilter
{

   /**
    * Logger
    */
   private final static Log log = LogFactory.getLog(ByteCodeAnnotationFilter.class);

   /**
    * The string to search for in the constant pool.
    */
   private final static String SEARCH_STRING = 
      "L"+URLMapping.class.getPackage().getName().replace('.', '/');
   
   /**
    * The magic to identify class files.
    */
   private final static int CLASS_FILE_MAGIC = 0xCAFEBABE;

   /*
    * Tag values for the Constant Pool:
    * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#20080
    */
   private final static int CONSTANT_Class = 7;
   private final static int CONSTANT_Fieldref = 9;
   private final static int CONSTANT_Methodref = 10;
   private final static int CONSTANT_InterfaceMethodref = 11;
   private final static int CONSTANT_String = 8;
   private final static int CONSTANT_Integer = 3;
   private final static int CONSTANT_Float = 4;
   private final static int CONSTANT_Long = 5;
   private final static int CONSTANT_Double = 6;
   private final static int CONSTANT_NameAndType = 12;
   private final static int CONSTANT_Utf8 = 1;

   /**
    * <p>
    * Checks whether that supplied {@link InputStream} contains a Java class
    * file that might contain PrettyFaces annotations.
    * </p>
    * <p>
    * The caller of this method is responsible to close the supplied
    * {@link InputStream}. This method won't do it!
    * </p>
    * 
    * @param classFileStream
    *           The stream to read the class file from.
    * @return <code>true</code> for files that should be further checked for
    *         annotations
    * @throws IOException
    *            for any kind of IO problem
    */
   @SuppressWarnings("unused")
   public boolean accept(InputStream classFileStream) throws IOException
   {

      // open a DataInputStream
      DataInputStream in = new DataInputStream(classFileStream);

      // read magic and abort if it doesn't match
      int magic = in.readInt();
      if (magic != CLASS_FILE_MAGIC)
      {
         if (log.isDebugEnabled())
         {
            log.debug("Magic not found! Not a valid class file!");
         }
         return false;
      }

      // check for at least JDK 1.5
      int minor = in.readUnsignedShort();
      int major = in.readUnsignedShort();
      if (major < 49)
      {
         // JDK 1.4 or less
         if (log.isTraceEnabled())
         {
            log.trace("Not a JDK5 class! It cannot contain annotations!");
         }
         return false;
      }

      // this values is equal to the number entries in the constants pool + 1
      int constantPoolEntries = in.readUnsignedShort() - 1;

      // loop over all entries in the constants pool
      for (int i = 0; i < constantPoolEntries; i++)
      {

         // the tag to identify the record type
         int tag = in.readUnsignedByte();

         // process record according to its type
         switch (tag)
         {

         case CONSTANT_Class:
            /*
             * CONSTANT_Class_info { 
             *   u1 tag; 
             *   u2 name_index; 
             * }
             */
            in.readUnsignedShort();
            break;

         case CONSTANT_Fieldref:
         case CONSTANT_Methodref:
         case CONSTANT_InterfaceMethodref:
            /*
             * CONSTANT_[Fieldref|Methodref|InterfaceMethodref]_info { 
             *   u1 tag; 
             *   u2 class_index; 
             *   u2 name_and_type_index; 
             * }
             */
            in.readUnsignedShort();
            in.readUnsignedShort();
            break;

         case CONSTANT_String:
            /*
             * CONSTANT_String_info { 
             *   u1 tag; 
             *   u2 string_index; 
             * }
             */
            in.readUnsignedShort();
            break;

         case CONSTANT_Integer:
         case CONSTANT_Float:
            /*
             * CONSTANT_[Integer|Float]_info { 
             *   u1 tag; 
             *   u4 bytes; 
             * }
             */
            in.readInt();
            break;

         case CONSTANT_Long:
         case CONSTANT_Double:

            /*
             * CONSTANT_Long_info { 
             *   u1 tag; 
             *   u4 high_bytes; 
             *   u4 low_bytes; 
             * }
             */
            in.readLong();
            
            /*
             * We must increase the constant pool index because this tag
             * type takes two entries
             */
            i++;
            
            break;

         case CONSTANT_NameAndType:
            /*
             * CONSTANT_NameAndType_info { 
             *   u1 tag; 
             *   u2 name_index; 
             *   u2 descriptor_index; 
             * }
             */
            in.readUnsignedShort();
            in.readUnsignedShort();
            break;

         case CONSTANT_Utf8:
            /*
             * CONSTANT_Utf8_info { 
             *   u1 tag; 
             *   u2 length; 
             *   u1 bytes[length]; 
             * }
             */
            String str = in.readUTF();

            // check if this string sounds interesting
            if (str.contains(SEARCH_STRING))
            {
               if (log.isTraceEnabled())
               {
                  log.trace("Found PrettyFaces annotation reference in constant pool: " + str);
               }
               return true;
            }
            break;

         default:
            /*
             * Unknown tag! Should not happen! We will scan the class in this case.
             */
            if (log.isDebugEnabled())
            {
               log.debug("Unknown constant pool tag found: " + tag);
            }
            return true;
         }
      }

      /*
       * We are finished with reading the interesting parts of the class file.
       * We stop here because the file doesn't seem to be interesting.
       */
      if (log.isTraceEnabled())
      {
         log.trace("No reference to PrettyFaces annotations found!");
      }
      return false;

   }

}
