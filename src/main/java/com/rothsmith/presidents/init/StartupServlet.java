package com.rothsmith.presidents.init;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet that initializes the database.
 * 
 * @author drothauser
 */
@WebServlet(name = "startup", loadOnStartup = 2,
        urlPatterns = { "/StartupServlet" })
public final class StartupServlet
        extends HttpServlet {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 2870499899294177247L;

	/**
	 * SLF4J Logger for StartupServlet.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(StartupServlet.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("PMD.CloseResource")
	public void init(ServletConfig config) throws ServletException {

		LOGGER.info("Initializing the database.");

		DbInitializer dbInitializer = new DbInitializer();

		dbInitializer.init();

		LOGGER.info("Database initializing completed.");

	}

}
