/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.state;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.persistence.jaxb.BeanValidationMode;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.validation.ValidationError;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rothsmith.dao.dbutils.DbUtilsJdbcDao;
import com.rothsmith.presidents.RestApp;
import com.rothsmith.presidents.init.DbInitializer;
import com.rothsmith.presidents.init.JndiInitalizer;
import com.rothsmith.presidents.president.PresidentDto;
import com.rothsmith.presidents.state.StateDto;
import com.rothsmith.presidents.state.StateResource;

/**
 * Test U.S. States REST service.
 * 
 * @author drothauser
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
public class StateResourceTest
        extends JerseyTest {

	/**
	 * SLF4J Logger for PartyRestServiceTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(StateResourceTest.class);

	/**
	 * {@link DbUtilsJdbcDao} to test.
	 */
	private final DbUtilsJdbcDao<StateDto, StateDto> stateDao =
	    new DbUtilsJdbcDao<StateDto, StateDto>("/statedao.properties");

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Application configure() {
		set(TestProperties.LOG_TRAFFIC, true);
		set(TestProperties.DUMP_ENTITY, true);

		return new RestApp()
		    .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
		    .property(
		        ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK,
		        true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureClient(final ClientConfig config) {

		super.configureClient(config);

		config.register(MoxyJsonFeature.class);
		// Turn off BV otherwise the entities on client would be validated as
		// well.
		config.register(new MoxyJsonConfig()
		    .property(MarshallerProperties.BEAN_VALIDATION_MODE,
		        BeanValidationMode.NONE)
		    .resolver());

	}

	/**
	 * Create database objects for testing.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {

		new JndiInitalizer().init();
		new DbInitializer().init();
	}

	/**
	 * Test method for {@link StateResource#create(StateDto)} .
	 */
	@Test
	public void testCreate() {

		StateDto stateDto = new StateDto();
		stateDto.setId(0);
		stateDto.setName("District of Columbia");
		stateDto.setAbbreviation("DC");

		displayJSON(stateDto);

		Response response =
		    target().path("/state/").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(stateDto, MediaType.APPLICATION_JSON_TYPE));

		StateDto stateDtoNew = response.readEntity(StateDto.class);

		assertThat(stateDtoNew.getId(), greaterThan(0));

	}

	/**
	 * Test method for {@link StateResource#create(StateDto)} with too short a
	 * name.
	 */
	@Test
	public void testCreateErrorShortName() {

		StateDto stateDto = new StateDto();
		stateDto.setId(0);
		stateDto.setName("PR");
		stateDto.setAbbreviation("PR");

		Response response =
		    target().path("/state/").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(stateDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{state.wrong.name}"));
		}
	}

	/**
	 * Test method for {@link StateResource#create(StateDto)} with too short an
	 * abbreviation.
	 */
	@Test
	public void testCreateErrorShortAbbr() {

		StateDto stateDto = new StateDto();
		stateDto.setId(0);
		stateDto.setName("Guam");
		stateDto.setAbbreviation("G");

		Response response =
		    target().path("/state/").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(stateDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{state.abbr.wrong.length}"));
		}
	}

	/**
	 * Test method for {@link StateResource#create(StateDto)} with too long an
	 * abbreviation.
	 */
	@Test
	public void testCreateErrorLongAbbr() {

		StateDto stateDto = new StateDto();
		stateDto.setId(0);
		stateDto.setName("Puerto Rico");
		stateDto.setAbbreviation("PTR");

		Response response =
		    target().path("/state/").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(stateDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{state.abbr.wrong.length}"));
		}
	}

	/**
	 * Test method for {@link StateResource#update(StateDto)} changing the state
	 * name.
	 */
	@Test
	public void testUpdateName() {

		StateDto params = new StateDto();
		params.setAbbreviation("MA");
		List<StateDto> stateDtos =
		    stateDao.selectByStatement("query.selectByAbbr", params);

		StateDto stateDto = stateDtos.get(0);
		stateDto.setName("Commonwealth of Massachusetts");

		displayJSON(stateDto);

		StateDto updStateDto = target("/state/").request().put(
		    Entity.entity(stateDto, MediaType.APPLICATION_JSON),
		    StateDto.class);

		assertThat(updStateDto.getName(), is(updStateDto.getName()));

	}

	/**
	 * Test method for {@link StateResource#update(StateDto)} updating the
	 * abbreviation to one that is already used by another state.
	 */
	@Test
	public void testUpdateErrorDupAbbr() {

		StateDto params = new StateDto();
		params.setAbbreviation("MA");
		List<StateDto> stateDtos =
		    stateDao.selectByStatement("query.selectByAbbr", params);

		StateDto stateDto = stateDtos.get(0);
		stateDto.setAbbreviation("MT");

		displayJSON(stateDto);

		Response response =
		    target("/state/").request(MediaType.APPLICATION_JSON_TYPE).put(
		        Entity.entity(stateDto, MediaType.APPLICATION_JSON_TYPE),
		        Response.class);

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{state.exists}"));
		}

	}

	/**
	 * Test method for {@link StateResource#update(StateDto)} updating the name
	 * to one that is already used by another state.
	 */
	@Test
	public void testUpdateErrorDupName() {

		StateDto params = new StateDto();
		params.setAbbreviation("MA");
		List<StateDto> stateDtos =
		    stateDao.selectByStatement("query.selectByAbbr", params);

		StateDto stateDto = stateDtos.get(0);
		stateDto.setName("Montana");

		displayJSON(stateDto);

		Response response =
		    target("/state/").request(MediaType.APPLICATION_JSON_TYPE).put(
		        Entity.entity(stateDto, MediaType.APPLICATION_JSON_TYPE),
		        Response.class);

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{state.exists}"));
		}

	}

	/**
	 * Test method for {@link StateResource#update(StateDto)} with too short an
	 * abbreviation.
	 */
	@Test
	public void testUpdateErrorShortAbbr() {

		StateDto stateDto = new StateDto();
		stateDto.setId(0);
		stateDto.setName("Puerto Rico");
		stateDto.setAbbreviation("P");

		Response response =
		    target("/state/").request(MediaType.APPLICATION_JSON_TYPE).put(
		        Entity.entity(stateDto, MediaType.APPLICATION_JSON_TYPE),
		        Response.class);

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{state.abbr.wrong.length}"));
		}
	}

	/**
	 * Test method for {@link StateResource#delete(String)}. Note that this test
	 * first deletes the president(s) that are from this state because not doing
	 * so would cause a foreign key constraint error.
	 * 
	 */
	@Test
	@SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
	public void testDelete() {

		DbUtilsJdbcDao<PresidentDto, PresidentDto> presidentDao =
		    new DbUtilsJdbcDao<PresidentDto, PresidentDto>(
		        "/presidentdao.properties");

		PresidentDto prezParams = new PresidentDto();
		prezParams.setLastname("Wilson");
		prezParams.setFirstname("Woodrow");
		List<PresidentDto> presidentDtos =
		    presidentDao.selectByStatement("query.selectByName", prezParams);

		PresidentDto presidentDto = presidentDtos.get(0);

		Response presDelResp = target("/president/" + presidentDto.getId())
		    .request(MediaType.APPLICATION_JSON).delete();

		assertThat(presDelResp.getStatus(),
		    is(Response.Status.NO_CONTENT.getStatusCode()));

		StateDto stateParams = new StateDto();
		stateParams.setAbbreviation("NJ");
		List<StateDto> stateDtos =
		    stateDao.selectByStatement("query.selectByAbbr", stateParams);

		StateDto stateDto = stateDtos.get(0);

		Response response = target("/state/" + stateDto.getId())
		    .request(MediaType.APPLICATION_JSON).delete();

		assertThat(response.getStatus(),
		    is(Response.Status.NO_CONTENT.getStatusCode()));

	}

	/**
	 * Test method for {@link StateResource#delete(String)} with a state that is
	 * assigned to one or more presidents. This will result in a constraint
	 * error.
	 */
	@Test
	public void testDeleteErrorHasPres() {

		StateDto params = new StateDto();
		params.setAbbreviation("VA");
		List<StateDto> stateDtos =
		    stateDao.selectByStatement("query.selectByAbbr", params);

		StateDto stateDto = stateDtos.get(0);

		Response response = target("/state/" + stateDto.getId())
		    .request(MediaType.APPLICATION_JSON).delete();

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (final ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{state.has.presidents}"));
		}

	}

	/**
	 * Test method for
	 * {@link com.rothsmith.presidents.state.StateResource#delete(String)} with a
	 * state that the doesn't exist.
	 */
	@Test
	public void testDeleteErrorBadId() {

		int bogusStateId = -1;

		Response response = target("/state/" + bogusStateId)
		    .request(MediaType.APPLICATION_JSON).delete();

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (final ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{state.does.not.exist}"));
		}

	}

	/**
	 * Test method for {@link com.rothsmith.presidents.state.StateResource#list()}.
	 */
	@Test
	public void testList() {

		Response response = target("/state/").request()
		    .accept(MediaType.APPLICATION_JSON).get();

		List<StateDto> states =
		    response.readEntity(new GenericType<List<StateDto>>() {
		    });

		assertThat(CollectionUtils.isEmpty(states), is(false));

		for (StateDto stateDto : states) {
			LOGGER.info(stateDto.toString());
		}

	}

	/**
	 * Test method for {@link StateResource#read(String)}.
	 */
	@Test
	public void testRead() {

		StateDto params = new StateDto();
		params.setName("Florida");
		List<StateDto> stateDtos =
		    stateDao.selectByStatement("query.selectByName", params);

		StateDto testDto = stateDtos.get(0);

		Response response = target("/state/" + testDto.getId())
		    .request(MediaType.APPLICATION_JSON).get(Response.class);

		StateDto stateDto = response.readEntity(StateDto.class);

		assertThat(stateDto, is(testDto));

	}

	/**
	 * Test method for {@link StateResource#read(String)} using an invalid id.
	 */
	@Test
	public void testReadError() {

		int id = -1;

		Response response = target("/state/" + id)
		    .request(MediaType.APPLICATION_JSON).get(Response.class);

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (final ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{state.does.not.exist}"));
		}

	}

	/**
	 * This method displays the given DTO (data transfer object) as a JSON
	 * string.
	 * 
	 * @param <T>
	 *            Generic for a data transfer object.
	 * @param dto
	 *            The instance of the DTO to render
	 */
	private <T> void displayJSON(T dto) {
		LOGGER.info(String.format("Marshalling DTO to JSON:%n%s%n", dto));
		try {
			JAXBContext jc = JAXBContext.newInstance(dto.getClass());
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(MarshallerProperties.MEDIA_TYPE,
			    MediaType.APPLICATION_JSON);
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT,
			    false);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			marshaller.marshal(dto, baos);
			String json = baos.toString();
			LOGGER.info(String.format("Marshalled JSON:%n%s", json));
		} catch (JAXBException e) {
			LOGGER.warn("Exception caught marshalling object: " + e.toString());
		}
	}

	/**
	 * Retrieve the validation errors.
	 * 
	 * @param response
	 *            {@link Response} from REST service call.
	 * @return a {@link List} of {@link ValidationError} objects.
	 */
	private List<ValidationError> getValidationErrorList(Response response) {
		return response.readEntity(new GenericType<List<ValidationError>>() {
		});
	}

	/**
	 * Retrieve the message validation templates given a list of validation
	 * error objects.
	 * 
	 * @param errors
	 *            {@link List} of {@link ValidationError} objects.
	 * @return {@link Set} of message validation templates
	 */
	private Set<String> getValidationMessageTemplates(
	    List<ValidationError> errors) {
		Set<String> templates = new HashSet<>();
		for (final ValidationError error : errors) {
			templates.add(error.getMessageTemplate());
		}
		return templates;
	}

	/**
	 * Retrieve the message validation templates given a REST service call
	 * response.
	 * 
	 * @param response
	 *            {@link Response} from REST service call.
	 * @return {@link Set} of message validation templates
	 */
	@SuppressWarnings("unused")
	private Set<String> getValidationMessageTemplates(Response response) {
		return getValidationMessageTemplates(getValidationErrorList(response));
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
