package org.ocpsoft.rewrite.transform.minify;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.ocpsoft.rewrite.transform.Transformer;

/**
 * A {@link Transformer} implementation that can perform various minification tasks.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Minify implements Transformer
{
   private Charset charset = StandardCharsets.UTF_8;

   /**
    * Specify the {@link Charset} to be used during minification.
    * 
    * @param charset the {@link Charset} to be used.
    */
   public Minify usingCharset(Charset charset)
   {
      this.charset = charset;
      return this;
   }

   public static JsMinify js()
   {
      return new JsMinify();
   }

   public static CssMinify css()
   {
      return new CssMinify();
   }

   /**
    * Get the {@link Charset} to be used during minification.
    */
   protected Charset getCharset()
   {
      return charset;
   }
}
