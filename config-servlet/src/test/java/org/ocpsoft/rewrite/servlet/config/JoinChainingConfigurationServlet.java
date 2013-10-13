package org.ocpsoft.rewrite.servlet.config;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(urlPatterns="/servlet")
public class JoinChainingConfigurationServlet extends HttpServlet
{

   private static final long serialVersionUID = 1L;

   private ServletConfig config;

   @Override
   public void init(ServletConfig config) throws ServletException
   {
      this.config = config;
   }

   @Override
   public ServletConfig getServletConfig()
   {
      return this.config;
   }

   @Override
   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException
   {
      req.getRequestDispatcher("/1").forward(req, res);
   }

   @Override
   public String getServletInfo()
   {
      return "JoinChainingConfigurationServlet";
   }

   @Override
   public void destroy()
   {
   }

}
