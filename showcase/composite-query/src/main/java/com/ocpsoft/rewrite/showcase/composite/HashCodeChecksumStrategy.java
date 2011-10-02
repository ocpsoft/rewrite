/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.ocpsoft.rewrite.showcase.composite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HashCodeChecksumStrategy implements ChecksumStrategy
{
   private static final String CHECKSUM_DELIM = "#";

   @Override
   public boolean checksumValid(final String token)
   {
      if (token.contains(CHECKSUM_DELIM))
      {
         int hashCode = token.substring(token.indexOf(CHECKSUM_DELIM) + 1).hashCode();
         Integer storedHashCode = Integer.valueOf(token.substring(0, token.indexOf(CHECKSUM_DELIM)));
         return hashCode == storedHashCode;
      }
      return false;
   }

   @Override
   public String embedChecksum(final String token)
   {
      String result = token.hashCode() + CHECKSUM_DELIM + token;
      return result;
   }

   @Override
   public String removeChecksum(final String token)
   {
      String result = token.substring(token.indexOf(CHECKSUM_DELIM) + 1);
      return result;
   }

}
