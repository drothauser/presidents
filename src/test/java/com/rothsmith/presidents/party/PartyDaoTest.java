/*
 * (c) 2016 Rothsmith, LLC, All Rights Reserved
 */
package com.rothsmith.presidents.party;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
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

/**
 * Test the DbUtils Generic DAO for the PARTY table.
 * 
 * @author drothauser
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@Transactional(TransactionMode.ROLLBACK)
public class PartyDaoTest {

	/**
	 * Logger for PartyDaoTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(PartyDaoTest.class);

	/**
	 * Stub {@link PartyDto} object.
	 */
	private PartyDto stubDto;

	/**
	 * {@link DbUtilsJdbcDao} to test.
	 */
	private DbUtilsJdbcDao<PartyDto, PartyDto> partyDao;

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
	 * Initializes the PartyDao bean from a properties file.
	 * 
	 * @throws java.lang.Exception
	 *             possible exception
	 */
	@Before
	public final void setUp() throws Exception {

		partyDao =
		    new DbUtilsJdbcDao<PartyDto, PartyDto>("/partydao.properties");

		stubDto = createStubDto();

		Integer maxId = fetchMaxId().getId();

		stubDto.setId(maxId);

		Object id = partyDao.insert(stubDto);

		LOGGER.info("groupsDao.insert(stubDto) = " + id);

		stubDto = fetchMaxId();

	}

	/**
	 * Stub up a {@link PartyDto} object.
	 * 
	 * @return a PartyDto object populated with test data.
	 */
	private PartyDto createStubDto() {

		PartyDto partyDto = new PartyDto();

		partyDto.setId(Integer.valueOf(1));

		partyDto.setName("~");

		// CHECKSTYLE:OFF Magic numbers ok here
		partyDto.setFoundedYear(new Random().nextInt(2100 - 1800) + 1800);
		// CHECKSTYLE:ON

		partyDto.setEndYear(null);

		return partyDto;

	}

	/**
	 * Test updating a party record.
	 */
	@Test
	public final void testUpdate() {

		PartyDto params = new PartyDto();

		params.setId(stubDto.getId());

		List<PartyDto> partyDtos = partyDao.select(params);

		PartyDto partyDto = partyDtos.get(0);
		// Alter a field value:
		// partyDto.set...

		int numRecs = partyDao.update(partyDto);

		assertEquals("Expected number of records updated to = 1", 1, numRecs);

	}

	/**
	 * Test deleting a party record.
	 */
	@Test
	public final void testDelete() {

		PartyDto params = new PartyDto();

		params.setId(stubDto.getId());

		int numRecs = partyDao.delete(params);

		assertEquals("Expected number of records updated to = 1", 1, numRecs);

	}

	/**
	 * Test select by primary key from party.
	 */
	@Test
	public final void testSelectByPrimaryKey() {

		PartyDto params = new PartyDto();

		params.setId(stubDto.getId());

		List<PartyDto> partyDtos = partyDao.select(params);

		if (partyDtos != null && partyDtos.size() == 1) {
			PartyDto partyDto = partyDtos.get(0);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Selected = " + partyDto.toString());
			}
		} else {
			fail("Expected select to return 1 record");
		}

	}

	/**
	 * Fetch the maximum ID from party.
	 *
	 * @return the PartyDto that contains the maximum ID value.
	 */
	private PartyDto fetchMaxId() {
		List<PartyDto> partyDtos =
		    partyDao.selectByStatement("query.selectMaxId");

		if (partyDtos.isEmpty()) {
			throw new IllegalStateException(
			    "No results returned. Is the DB populated?");
		}

		return partyDtos.get(0);
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
