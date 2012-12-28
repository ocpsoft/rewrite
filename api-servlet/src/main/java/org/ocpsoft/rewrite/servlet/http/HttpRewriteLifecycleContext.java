package org.ocpsoft.rewrite.servlet.http;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.servlet.RewriteLifecycleContext;

/**
 * An extension of {@link RewriteLifecycleContext} specialized for the {@link Servlet} environment.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface HttpRewriteLifecycleContext extends RewriteLifecycleContext<ServletContext>
{
}
