/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.unitils.database.DatabaseUnitils;

/**
 * Utility class for initializing simple-jndi to serve up JNDI JDBC datasources
 * for unit testing.
 * 
 * @author drothauser
 *
 */
public class JndiInitalizer {

	/**
	 * JNDI properties for application.
	 */
	private static final String JNDI_PROPERTIES = "presidents.properties";

	/**
	 * presidents application properties.
	 */
	private final Properties props = initProps(JNDI_PROPERTIES);

	/**
	 * Sets the JNDI context to use the simple-jndi factory -
	 * <code>org.osjava.sj.memory.MemoryContextFactory</code>.
	 */
	public void init() {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
		    "org.osjava.sj.memory.MemoryContextFactory");
		System.setProperty("org.osjava.sj.jndi.shared", "true");
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			DataSource dataSource = DatabaseUnitils.getDataSource();
			ctx.rebind(props.getProperty("db.jndi.url"), dataSource);
		} catch (NamingException e) {
			throw new IllegalStateException(
			    String.format("Error in DbInitializer contstuctor: %s", e), e);
		}

	}

	/**
	 * Load the web application's properties file into a {@link Properties}
	 * object.
	 * 
	 * @param propsFile
	 *            webapp properties file
	 * @return Properties object
	 */
	private Properties initProps(String propsFile) {

		InputStream inputStream = Thread.currentThread().getContextClassLoader()
		    .getResourceAsStream(propsFile);
		if (inputStream == null) {
			throw new IllegalStateException(String.format(
			    "Unable to load properties from \"%s\". Does it exist?",
			    propsFile));
		}
		Properties props = new Properties();
		try {
			props.load(Thread.currentThread().getContextClassLoader()
			    .getResourceAsStream(JNDI_PROPERTIES));
		} catch (IOException e) {
			throw new IllegalStateException(String.format(
			    "I/O error caught loading properties from \"%s\": %s",
			    propsFile, e));
		}
		return props;
	}

}
