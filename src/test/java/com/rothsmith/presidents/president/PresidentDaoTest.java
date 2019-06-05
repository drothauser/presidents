/*
 * (c) 2016 Rothsmith, LLC, All Rights Reserved
 */
package com.rothsmith.presidents.president;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
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
import com.rothsmith.presidents.president.PresidentDto;

/**
 * Test the DbUtils Generic DAO for the PRESIDENT table.
 * 
 * @author drothauser
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@Transactional(TransactionMode.ROLLBACK)
public class PresidentDaoTest {

	/**
	 * Logger for PresidentDaoTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(PresidentDaoTest.class);

	/**
	 * Stub {@link PresidentDto} object.
	 */
	private PresidentDto stubDto;

	/**
	 * {@link DbUtilsJdbcDao} to test.
	 */
	private DbUtilsJdbcDao<PresidentDto, PresidentDto> presidentDao;

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
	 * Initializes the PresidentDao bean from a properties file.
	 * 
	 * @throws java.lang.Exception
	 *             possible exception
	 */
	@Before
	public final void setUp() throws Exception {

		presidentDao = new DbUtilsJdbcDao<PresidentDto, PresidentDto>(
		    "/presidentdao.properties");

		stubDto = createStubDto();

		Integer maxId = fetchMaxId().getId();

		stubDto.setId(maxId);

		Object id = presidentDao.insert(stubDto);

		LOGGER.info("presidentDao.insert(stubDto) = " + id);

		stubDto = fetchMaxId();

	}

	/**
	 * Stub up a {@link PresidentDto} object.
	 * 
	 * @return a PresidentDto object populated with test data.
	 */
	private PresidentDto createStubDto() {

		PresidentDto presidentDto = new PresidentDto();

		presidentDto.setId(Integer.valueOf(1));

		presidentDto.setFirstname("~");

		presidentDto.setLastname("~");

		presidentDto.setStateId(Integer.valueOf(1));

		presidentDto.setPartyId(Integer.valueOf(1));

		// CHECKSTYLE:OFF Magic numbers ok here
		presidentDto
		    .setInauguratedYear(new Random().nextInt(2100 - 1800) + 1800);
		// CHECKSTYLE:ON

		presidentDto.setYears(BigDecimal.ONE);

		return presidentDto;

	}

	/**
	 * Test updating a president record.
	 */
	@Test
	public final void testUpdate() {

		PresidentDto params = new PresidentDto();

		params.setId(stubDto.getId());
		params.setStateId(stubDto.getStateId());

		List<PresidentDto> presidentDtos = presidentDao.select(params);

		PresidentDto presidentDto = presidentDtos.get(0);
		// Alter a field value:
		// presidentDto.set...

		int numRecs = presidentDao.update(presidentDto);

		assertEquals("Expected number of records updated to = 1", 1, numRecs);

	}

	/**
	 * Test deleting a president record.
	 */
	@Test
	public final void testDelete() {

		PresidentDto params = new PresidentDto();

		params.setId(stubDto.getId());
		params.setStateId(stubDto.getStateId());

		int numRecs = presidentDao.delete(params);

		assertEquals("Expected number of records updated to = 1", 1, numRecs);

	}

	/**
	 * Test select by primary key from president.
	 */
	@Test
	public final void testSelectByPrimaryKey() {

		PresidentDto params = new PresidentDto();

		params.setId(stubDto.getId());
		params.setStateId(stubDto.getStateId());

		List<PresidentDto> presidentDtos = presidentDao.select(params);

		if (presidentDtos != null && presidentDtos.size() == 1) {
			PresidentDto presidentDto = presidentDtos.get(0);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Selected = " + presidentDto.toString());
			}
		} else {
			fail("Expected select to return 1 record");
		}

	}

	/**
	 * Test select presidents of a particular party.
	 */
	@Test
	public final void testSelectByParty() {

		PresidentDto params = new PresidentDto();
		Integer federalistPartyId = 1;
		params.setPartyId(federalistPartyId);

		List<PresidentDto> presidentDtos =
		    presidentDao.selectByStatement("query.selectByParty", params);

		Integer count = 0;
		if (!CollectionUtils.isEmpty(presidentDtos)) {
			count = presidentDtos.size();
		}

		assertThat(count, greaterThan(0));

	}

	/**
	 * Test select presidents of a particular state.
	 */
	@Test
	public final void testSelectByState() {

		PresidentDto params = new PresidentDto();
		Integer georgiaId = 10;
		params.setStateId(georgiaId);

		List<PresidentDto> presidentDtos =
		    presidentDao.selectByStatement("query.selectByState", params);

		Integer count = 0;
		if (!CollectionUtils.isEmpty(presidentDtos)) {
			count = presidentDtos.size();
		}

		assertThat(count, greaterThan(0));

	}

	/**
	 * Fetch the maximum ID from president.
	 *
	 * @return the PresidentDto that contains the maximum ID value.
	 */
	private PresidentDto fetchMaxId() {
		List<PresidentDto> presidentDtos =
		    presidentDao.selectByStatement("query.selectMaxId");

		if (presidentDtos.isEmpty()) {
			throw new IllegalStateException(
			    "No results returned. Is the DB populated?");
		}

		return presidentDtos.get(0);
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
	 * @throws java.lang.Exception possible failure
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		DbInitializer dbInitializer = new DbInitializer();
		dbInitializer.tearDown();
	}

}
