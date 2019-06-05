/*
 * (c) 2016 Rothsmith, LLC, All Rights Reserved
 */
package com.rothsmith.presidents.president;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;

import com.rothsmith.dao.dbutils.DbUtilsJdbcDao;
import com.rothsmith.presidents.init.DbInitializer;
import com.rothsmith.presidents.president.PresidentsViewDto;

/**
 * Test the DbUtils Generic DAO for the PRESIDENTS_VIEW.
 * 
 * @author drothauser
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@Transactional(TransactionMode.ROLLBACK)
public class PresidentsViewDaoTest {

	/**
	 * Logger for PresidentsViewDaoTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(PresidentsViewDaoTest.class);

	/**
	 * {@link DbUtilsJdbcDao} to test.
	 */
	private DbUtilsJdbcDao<PresidentsViewDto, PresidentsViewDto> presidentsViewDao;

	/**
	 * Create database objects for testing.
	 * 
	 * @throws IOException
	 *             possible problem loading the properties file
	 * @throws SQLException
	 *             possible SQL error
	 * @throws NamingException
	 *             possible error initializing JNDI
	 *
	 */
	@BeforeClass
	public static void setUpBeforeClass()
	        throws IOException, SQLException, NamingException {

		Properties props = new Properties();
		String propsFile = "presidents.properties";
		InputStream inputStream = Thread.currentThread().getContextClassLoader()
		    .getResourceAsStream(propsFile);

		if (inputStream != null) {
			props.load(Thread.currentThread().getContextClassLoader()
			    .getResourceAsStream(propsFile));

			System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
			    "org.osjava.sj.memory.MemoryContextFactory");
			System.setProperty("org.osjava.sj.jndi.shared", "true");

			InitialContext ctx = new InitialContext();

			DataSource dataSource = DatabaseUnitils.getDataSource();

			// ctx.rebind(jdbcSubcontext + dbAlias, dataSource);
			ctx.rebind("java:/comp/env/jdbc/PresidentsDS", dataSource);

			try (Connection conn = dataSource.getConnection()) {

				String createSql = fetchSql(props, "db.create.sql");

				String populateSql = fetchSql(props, "db.populate.sql");

				String initSql = createSql + populateSql;

				if (!"".equals(initSql)) {
					String[] sqlStmts = initSql.split("(?<!\\-{2}.{0,100});");
					for (String sql : sqlStmts) {
						try (Statement stmt = conn.createStatement()) {
							LOGGER.info(String.format("%nExecuting %s", sql));
							stmt.executeUpdate(sql);
							LOGGER.info("\nDone!");
						} catch (SQLException e) {
							String msg = String.format(
							    "Initialization error running SQL statement: %s: %s",
							    sql, e);
							LOGGER.error(msg, e);
							fail("Initialization error running SQL statements: "
							    + e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * Initializes the Sql1Dao bean from a properties file.
	 * 
	 * @throws java.lang.Exception
	 *             possible exception
	 */
	@Before
	public final void setUp() throws Exception {

		presidentsViewDao =
		    new DbUtilsJdbcDao<PresidentsViewDto, PresidentsViewDto>(
		        "/presidentsviewdao.properties");

		createStubDto();
	}

	/**
	 * Stub up a {@link PresidentsViewDto} object.
	 * 
	 * @return a PresidentsViewDto object populated with test data.
	 */
	private PresidentsViewDto createStubDto() {

		PresidentsViewDto presidentsViewDto = new PresidentsViewDto();

		presidentsViewDto.setLastname("~");

		presidentsViewDto.setFirstname("~");

		// CHECKSTYLE:OFF Magic numbers ok here.
		presidentsViewDto
		    .setInauguratedYear(new Random().nextInt(2100 - 1800) + 1800);
		// CHECKSTYLE:ON

		presidentsViewDto.setYears(BigDecimal.ONE);

		presidentsViewDto.setState("~");

		presidentsViewDto.setParty("~");

		return presidentsViewDto;

	}

	/**
	 * Test the following query: <code>SELECT * FROM PRESIDENTS_VIEW</code>.
	 */
	@Test
	public final void testSelect() {

		List<PresidentsViewDto> presidentsViewDtos =
		    presidentsViewDao.selectByStatement("query.selectAll");

		assertThat(CollectionUtils.isEmpty(presidentsViewDtos), is(false));

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

	/**
	 * Drops all the tables in the TEST database.
	 * 
	 * @throws java.lang.Exception
	 *             possible failure
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		DbInitializer dbInitializer = new DbInitializer();
		dbInitializer.tearDown();
	}

}
