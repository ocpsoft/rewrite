package org.ocpsoft.rewrite.showcase.transform;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.Header;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.RequestParameter;
import org.ocpsoft.rewrite.servlet.config.Response;

public class GZipConfigurationProvider extends HttpConfigurationProvider {
	@Override
	public Configuration getConfiguration(ServletContext context) {
		return ConfigurationBuilder
				.begin()
				/*
				 * Set up compression.
				 */
				.addRule()
				.when(Header.matches("{Accept-Encoding}", "{gzip}").andNot(
						RequestParameter.exists("nogzip")))
				.perform(Response.gzipStreamCompression())

				.where("Accept-Encoding").matches("(?i)Accept-Encoding")
				.where("gzip").matches("(?i).*\\bgzip\\b.*");
	}

	@Override
	public int priority() {
		/*
		 * Very high priority.
		 */
		return -100000;
	}
}