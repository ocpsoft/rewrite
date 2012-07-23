package org.ocpsoft.rewrite.servlet.config;

import java.io.InputStream;

import javax.servlet.ServletResponse;

/**
 * A piece of work to be done on the fully buffered {@link ServletResponse#getOutputStream()} before flushing to the
 * client, once the control of the application has been returned to Rewrite.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface OutputBuffer
{
   /**
    * Perform any manipulation of the fully buffered {@link ServletResponse#getOutputStream()} contents. 
    */
   InputStream execute(InputStream input);
}
