/*
 * (c) 2016 Rothsmith, LLC, All Rights Reserved
 */
package com.rothsmith.presidents.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

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
 * Test the DbUtils Generic DAO for the STATE table.
 * 
 * @author drothauser
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@Transactional(TransactionMode.ROLLBACK)
public class StateDaoTest {

	/**
	 * Logger for StateDaoTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(StateDaoTest.class);

	/**
	 * Stub {@link StateDto} object.
	 */
	private StateDto stubDto;

	/**
	 * {@link DbUtilsJdbcDao} to test.
	 */
	private DbUtilsJdbcDao<StateDto, StateDto> stateDao;

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
	 * Initializes the StateDao bean from a properties file.
	 * 
	 * @throws java.lang.Exception
	 *             possible exception
	 */
	@Before
	public final void setUp() throws Exception {

		stateDao =
		    new DbUtilsJdbcDao<StateDto, StateDto>("/statedao.properties");

		stubDto = createStubDto();

		Object id = stateDao.insert(stubDto);

		LOGGER.info("statedao.insert(stubDto) = " + id);

		stubDto = fetchMaxId();

	}

	/**
	 * Stub up a {@link StateDto} object.
	 * 
	 * @return a StateDto object populated with test data.
	 */
	private StateDto createStubDto() {

		StateDto stateDto = new StateDto();

		stateDto.setId(Integer.valueOf(1));

		stateDto.setName("~");

		stateDto.setAbbreviation("~");

		return stateDto;

	}

	/**
	 * Test updating a state record.
	 */
	@Test
	public final void testUpdate() {

		StateDto params = new StateDto();

		params.setId(stubDto.getId());

		List<StateDto> stateDtos = stateDao.select(params);

		StateDto stateDto = stateDtos.get(0);
		// Alter a field value:
		// stateDto.set...

		int numRecs = stateDao.update(stateDto);

		assertEquals("Expected number of records updated to = 1", 1, numRecs);

	}

	/**
	 * Test creating a state record.
	 */
	@Test
	public final void testCreate() {

		StateDto params = new StateDto();

		params.setId(0);
		params.setName("District of Columbia");
		params.setAbbreviation("DC");

		int id = stateDao.insert(params);

		List<StateDto> stateDtos = stateDao.select("SELECT * FROM TEST.STATE "
		    + "WHERE ID = (SELECT MAX(ID) FROM TEST.STATE)");

		assertSame(id, stateDtos.get(0).getId());

	}

	/**
	 * Test deleting a state record.
	 */
	@Test
	public final void testDelete() {

		StateDto params = new StateDto();

		params.setId(stubDto.getId());

		int numRecs = stateDao.delete(params);

		assertEquals("Expected number of records updated to = 1", 1, numRecs);

	}

	/**
	 * Test select by primary key from state.
	 */
	@Test
	public final void testSelectByPrimaryKey() {

		StateDto params = new StateDto();

		params.setId(stubDto.getId());

		List<StateDto> stateDtos = stateDao.select(params);

		if (stateDtos != null && stateDtos.size() == 1) {
			StateDto stateDto = stateDtos.get(0);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Selected = " + stateDto.toString());
			}
		} else {
			fail("Expected select to return 1 record");
		}

	}

	/**
	 * Test select by state abbreviation from state.
	 */
	@Test
	public final void testSelectByAbbr() {

		StateDto params = new StateDto();

		params.setAbbreviation("FL");

		List<StateDto> stateDtos =
		    stateDao.selectByStatement("query.selectByAbbr", params);

		if (stateDtos != null && stateDtos.size() == 1) {
			StateDto stateDto = stateDtos.get(0);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Selected = " + stateDto.toString());
			}
		} else {
			fail("Expected select to return 1 record");
		}

	}

	/**
	 * Fetch the maximum ID from state.
	 *
	 * @return the StateDto that contains the maximum ID value.
	 */
	private StateDto fetchMaxId() {
		List<StateDto> stateDtos =
		    stateDao.selectByStatement("query.selectMaxId");

		if (stateDtos.isEmpty()) {
			throw new IllegalStateException(
			    "No results returned. Is the DB populated?");
		}

		return stateDtos.get(0);
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
