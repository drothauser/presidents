/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.init;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rothsmith.presidents.init.DbInitializer;
import com.rothsmith.utils.database.JDBCServiceLocator;

/**
 * Tests for {@link DbInitializer} that executes the DDL to create and load the
 * in-memory database.
 * 
 * @author drothauser
 *
 */
public class DbInitializerTest {

	/**
	 * SLF4J Logger for DbInitializerTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(DbInitializerTest.class);

	/**
	 * Sets up the simple-jndi naming context.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		new JndiInitalizer().init();
	}

	/**
	 * Test {@link DbInitializer#init()}.
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 * @throws SQLException
	 *             possible SQL error
	 */
	@Test
	public void testSimpleJndi() throws NamingException, SQLException {
		DbInitializer dbInitializer = new DbInitializer();
		dbInitializer.init();
		DataSource ds = JDBCServiceLocator.getInstance()
		    .getDataSource("java:/comp/env/jdbc/PresidentsDS");
		try (Connection conn = ds.getConnection()) {
			String dbVendor = conn.getMetaData().getDatabaseProductName();
			LOGGER.info(dbVendor);
			assertThat(dbVendor, is("Apache Derby"));
		}
	}
	
	/**
	 * Sets up the simple-jndi naming context.
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		DbInitializer dbInitializer = new DbInitializer();
		dbInitializer.tearDown();
	}

}
