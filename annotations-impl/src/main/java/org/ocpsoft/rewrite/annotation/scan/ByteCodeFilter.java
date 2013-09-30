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
package org.ocpsoft.rewrite.annotation.scan;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.ocpsoft.logging.Logger;

/**
 * <p>
 * This class reads Java class files and checks whether they contain references to specific annotations. This allows to
 * check classes for the presence of annotations before instantiating them.
 * </p>
 *
 * <p>
 * The filter is inspired by the <code>ClassByteCodeAnnotationFilter</code> of Apache MyFaces 2.0. Many thanks go out to
 * Leonardo Uribe for his great work on this class.
 * </p>
 *
 * @see http://docs.oracle.com/javase/specs/jvms/se5.0/html/ClassFile.doc.html
 * @see http://en.wikipedia.org/wiki/Class_%28file_format%29
 *
 * @author Christian Kaltepoth
 */
public class ByteCodeFilter
{

   private final static Logger log = Logger.getLogger(ByteCodeFilter.class);

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
    * The strings to look for in the constants table
    */
   private final Set<String> fieldDescriptors = new LinkedHashSet<String>();

   /**
    * The filter must be initialized with a list of types to look for when scanning the class files. If a class files
    * contains a reference to one of these types, the filter will accept the class.
    *
    * @param types A Set of types to look for
    */
   public ByteCodeFilter(Set<Class<? extends Annotation>> types)
   {
      for (Class<? extends Annotation> type : types) {
         fieldDescriptors.add("L" + type.getName().replace('.', '/'));
      }
   }

   /**
    * <p>
    * Checks whether that supplied {@link InputStream} contains a Java class file that might contain references to
    * specific annotation types.
    * </p>
    * <p>
    * The caller of this method is responsible to close the supplied {@link InputStream}. This method won't do it!
    * </p>
    *
    * @param classFileStream The stream to read the class file from.
    * @return <code>true</code> for files that contain at least one reference to one of the "interesting" annotations
    * @throws IOException for any kind of IO problem
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
            if (containsFieldDescriptor(str))
            {
               if (log.isTraceEnabled())
               {
                  log.trace("Found interesting annotation reference in constant pool: " + str);
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

      return false;

   }

   /**
    * Returns true if the given string contains a field descriptor of one of the annotations we are looking for.
    */
   private boolean containsFieldDescriptor(String str)
   {
      for (String descriptor : fieldDescriptors) {
         if (str.contains(descriptor)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString()
   {
      return "ByteCodeFilter [fieldDescriptors=" + fieldDescriptors + "]";
   }

}
