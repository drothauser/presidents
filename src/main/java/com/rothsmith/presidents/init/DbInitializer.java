/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.init;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rothsmith.utils.database.JDBCServiceLocator;

/**
 * Class for initializing and loading the Derby database.
 * 
 * @author drothauser
 *
 */
public class DbInitializer {

	/**
	 * SLF4J Logger for DbInitializer.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(DbInitializer.class);

	/**
	 * Service Locator for JDBC datasources.
	 */
	private final JDBCServiceLocator jdbcServiceLocator =
	    JDBCServiceLocator.getInstance();

	/**
	 * presidents application properties.
	 */
	private final Properties props = initProps("presidents.properties");

	/**
	 * Initializes the PRESIDENT database.
	 */
	@SuppressWarnings("PMD.CloseResource")
	public void init() {

		try {

			DataSource ds = jdbcServiceLocator
			    .getDataSource(props.getProperty("db.jndi.url"));

			try (Connection conn = ds.getConnection()) {

				String createSql = fetchSql(props, "db.create.sql");

				String populateSql = fetchSql(props, "db.populate.sql");

				String initSql = createSql + populateSql;

				if (StringUtils.isBlank(initSql)) {
					throw new IllegalStateException(
					    "Expected initialization SQL.");
				}

				String[] sqlStmts = initSql.split("(?<!\\-{2}.{0,100});");
				for (String sql : sqlStmts) {
					Statement stmt = conn.createStatement();
					LOGGER.info(String.format("%nExecuting %s", sql));
					stmt.executeUpdate(sql);
					LOGGER.info("\nDone!");
					DbUtils.closeQuietly(stmt);
				}
			}

		} catch (NamingException | IOException | SQLException e) {
			throw new IllegalStateException(
			    String.format("Exception caught initializing database: %s", e));
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
			    .getResourceAsStream(propsFile));
		} catch (IOException e) {
			throw new IllegalStateException(String.format(
			    "I/O error caught loading properties from \"%s\": %s",
			    propsFile, e));
		}
		return props;
	}

	/**
	 * Drops tables in the PRESIDENT database.
	 */
	@SuppressWarnings("PMD.CloseResource")
	public void tearDown() {
	
		try {
	
			DataSource ds = jdbcServiceLocator
			    .getDataSource(props.getProperty("db.jndi.url"));
	
			try (Connection conn = ds.getConnection()) {
	
				String dropSql = fetchSql(props, "db.drop.sql");
	
				if (StringUtils.isBlank(dropSql)) {
					throw new IllegalStateException(
					    "Expected initialization SQL.");
				}
	
				String[] sqlStmts = dropSql.split("(?<!\\-{2}.{0,100});");
				for (String sql : sqlStmts) {
					Statement stmt = conn.createStatement();
					LOGGER.info(String.format("%nExecuting %s", sql));
					stmt.executeUpdate(sql);
					LOGGER.info("\nDone!");
					DbUtils.closeQuietly(stmt);
				}
			}
	
		} catch (NamingException | IOException | SQLException e) {
			throw new IllegalStateException(
			    String.format("Exception caught initializing database: %s", e));
		}
	
	}

	/**
	 * Method to read the sql files specified in the given property of the
	 * properties file and concatenate their contents into a string.
	 * 
	 * @param props
	 *            {@link Properties} object that contains sql parameters
	 * @param sqlProperty
	 *            Name of the property in the property file that contains names
	 *            of SQL files.
	 * @return String containing the contents of all the SQL files
	 * @throws IOException
	 *             thrown if there is problem accessing a SQL file
	 */
	private static String fetchSql(Properties props, String sqlProperty)
	        throws IOException {

		String sqlFileNames =
		    StringUtils.defaultString(props.getProperty(sqlProperty));

		StringBuilder sb = new StringBuilder();

		for (String sqlFileName : StringUtils
		    .split(StringUtils.deleteWhitespace(sqlFileNames), ',')) {

			String sql =
			    StringUtils.trim(IOUtils.toString(Thread.currentThread()
			        .getContextClassLoader().getResource(sqlFileName)));

			sb.append(sql);

		}

		return sb.toString();

	}

}
