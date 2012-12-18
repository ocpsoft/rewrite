package org.ocpsoft.urlbuilder.util;

public enum CaptureType
{
   BRACE('{', '}'), BRACKET('[', ']'), PAREN('(', ')'), REGEX('/', '/');

   private char begin;
   private char end;

   private CaptureType(final char begin, final char end)
   {
      this.begin = begin;
      this.end = end;
   }

   public char getBegin()
   {
      return begin;
   }

   public char getEnd()
   {
      return end;
   }
}