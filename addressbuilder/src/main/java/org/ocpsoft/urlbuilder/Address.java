package org.ocpsoft.urlbuilder;

public interface Address
{
   String getAnchor();

   boolean isAnchorSet();

   String getPath();

   String getPathAndQuery();

   boolean isPathSet();

   Integer getPort();

   boolean isPortSet();

   String getHost();

   boolean isHostSet();

   String getProtocol();

   boolean isProtocolSet();

   String getQuery();

   boolean isQuerySet();
}
