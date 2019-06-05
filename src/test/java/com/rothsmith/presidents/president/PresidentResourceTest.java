/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.president;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
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
import com.rothsmith.presidents.president.PresidentResource;
import com.rothsmith.presidents.president.PresidentsViewDto;

/**
 * Test U.S. Presidents REST service.
 * 
 * @author drothauser
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods",
    "checkstyle:magicnumber" })
public class PresidentResourceTest
        extends
        JerseyTest {

	/**
	 * SLF4J Logger for PartyRestServiceTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(PresidentResourceTest.class);

	/**
	 * DAO to maintain PRESIDENT table.
	 */
	private final DbUtilsJdbcDao<PresidentDto, PresidentDto> presidentDao =
	    new DbUtilsJdbcDao<PresidentDto, PresidentDto>(
	        "/presidentdao.properties");

	/**
	 * Create database objects for testing.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {

		new JndiInitalizer().init();
		new DbInitializer().init();
	}

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
	 * Test method for {@link PresidentResource#create(PresidentDto)} .
	 */
	@Test
	public void testCreate() {

		PresidentDto presidentDto = new PresidentDto();
		presidentDto.setId(0);
		presidentDto.setFirstname("Donald");
		presidentDto.setLastname("Trump");
		presidentDto.setInauguratedYear(2017);
		presidentDto.setYears(BigDecimal.ONE);
		presidentDto.setPartyId(5);
		presidentDto.setStateId(32);

		displayJSON(presidentDto);

		Response response = target().path("/president/")
		    .request(MediaType.APPLICATION_JSON_TYPE)
		    .post(Entity.entity(presidentDto, MediaType.APPLICATION_JSON_TYPE));

		PresidentDto presidentDtoNew = response.readEntity(PresidentDto.class);

		assertThat(presidentDtoNew.getId(), greaterThan(0));
	}

	/**
	 * Test method for {@link PresidentResource#create(PresidentDto)} with an
	 * invalid inauguration year.
	 */
	@Test
	public void testCreateErrorBadInaugYear() {

		PresidentDto presidentDto = new PresidentDto();
		presidentDto.setId(0);
		presidentDto.setFirstname("Hillary");
		presidentDto.setLastname("Clinton");
		presidentDto.setInauguratedYear(1700);
		presidentDto.setYears(BigDecimal.ONE);
		presidentDto.setPartyId(5);
		presidentDto.setStateId(32);

		Response response = target().path("/president/")
		    .request(MediaType.APPLICATION_JSON_TYPE)
		    .post(Entity.entity(presidentDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{president.wrong.inaugurated.year}"));
		}
	}

	/**
	 * Test method for {@link PresidentResource#create(PresidentDto)} with an a
	 * null first name.
	 */
	@Test
	public void testCreateNullFirstName() {

		PresidentDto presidentDto = new PresidentDto();
		presidentDto.setId(0);
		presidentDto.setFirstname(null);
		presidentDto.setLastname("Clinton");
		presidentDto.setInauguratedYear(2016);
		presidentDto.setYears(BigDecimal.ONE);
		presidentDto.setPartyId(5);
		presidentDto.setStateId(32);

		Response response = target().path("/president/")
		    .request(MediaType.APPLICATION_JSON_TYPE)
		    .post(Entity.entity(presidentDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (final ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{president.required.firstname}"));
		}

	}

	/**
	 * Test method for {@link PresidentResource#create(PresidentDto)} with an a
	 * null first name.
	 */
	@Test
	public void testCreateShortFirstName() {

		PresidentDto presidentDto = new PresidentDto();
		presidentDto.setId(0);
		presidentDto.setFirstname("H");
		presidentDto.setLastname("Clinton");
		presidentDto.setInauguratedYear(2016);
		presidentDto.setYears(BigDecimal.ONE);
		presidentDto.setPartyId(5);
		presidentDto.setStateId(32);

		Response response = target().path("/president/")
		    .request(MediaType.APPLICATION_JSON_TYPE)
		    .post(Entity.entity(presidentDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (final ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{president.wrong.firstname}"));
		}

	}

	/**
	 * Test method for {@link PresidentResource#delete(Integer)}.
	 */
	@Test
	public void testDelete() {

		PresidentDto params = new PresidentDto();
		params.setLastname("Adams");
		params.setFirstname("John");
		List<PresidentDto> presidentDtos =
		    presidentDao.selectByStatement("query.selectByName", params);

		PresidentDto presidentDto = presidentDtos.get(0);

		Response response = target("/president/" + presidentDto.getId())
		    .request(MediaType.APPLICATION_JSON).delete();

		assertThat(response.getStatus(),
		    is(Response.Status.NO_CONTENT.getStatusCode()));

	}

	/**
	 * Test method for {@link PresidentRestService#delete(String))} with a
	 * president that the doesn't exist.
	 * 
	 */
	@Test
	public void testDeleteErrorBadId() {

		int bogusPresidentId = -1;

		Response response = target("/president/" + bogusPresidentId)
		    .request(MediaType.APPLICATION_JSON).delete();

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (final ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{president.does.not.exist}"));
		}
	}

	/**
	 * Test method for {@link PresidentRestService#delete(String))} without
	 * specifying an id.
	 */
	@Test
	public void testDeleteErrorNoId() {

		Response response =
		    target("/president/").request(MediaType.APPLICATION_JSON).delete();

		assertThat(response.getStatus(), is(405));

	}

	/**
	 * Test method for {@link PresidentResource#list()}.
	 */
	@Test
	public void testList() {

		Response response = target("/president/").request()
		    .accept(MediaType.APPLICATION_JSON).get();

		List<PresidentsViewDto> presidents =
		    response.readEntity(new GenericType<List<PresidentsViewDto>>() {
		    });

		assertThat(CollectionUtils.isEmpty(presidents), is(false));

		for (PresidentsViewDto presidentsViewDto : presidents) {
			LOGGER.info(presidentsViewDto.toString());
		}
	}

	/**
	 * Test method for {@link PresidentResource#read(String)}.
	 */
	@Test
	public void testRead() {

		PresidentDto params = new PresidentDto();
		params.setLastname("Madison");
		params.setFirstname("James");
		List<PresidentDto> presidentDtos =
		    presidentDao.selectByStatement("query.selectByName", params);

		PresidentDto testDto = presidentDtos.get(0);

		Response response = target("/president/" + testDto.getId())
		    .request(MediaType.APPLICATION_JSON).get(Response.class);

		PresidentDto presidentDto = response.readEntity(PresidentDto.class);

		assertThat(presidentDto.getLastname(), is(testDto.getLastname()));

	}

	/**
	 * Test method for {@link PresidentResource#read(String)} using an invalid
	 * id.
	 */
	@Test
	public void testReadError() {

		int id = -1;

		Response response = target("/president/" + id)
		    .request(MediaType.APPLICATION_JSON).get(Response.class);

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (final ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{president.does.not.exist}"));
		}

	}

	/**
	 * Test method for {@link PresidentResource#update(PresidentDto)}.
	 * 
	 */
	@Test
	public void testUpdateFirstName() {

		PresidentDto params = new PresidentDto();
		params.setLastname("Jefferson");
		params.setFirstname("Thomas");
		List<PresidentDto> presidentDtos =
		    presidentDao.selectByStatement("query.selectByName", params);

		PresidentDto presidentDto = presidentDtos.get(0);
		presidentDto.setFirstname("Tom");

		displayJSON(presidentDto);

		PresidentDto updPresidentDto =
		    target("/president/").request(MediaType.APPLICATION_JSON_TYPE).put(
		        Entity.entity(presidentDto, MediaType.APPLICATION_JSON_TYPE),
		        PresidentDto.class);

		assertThat(updPresidentDto.getFirstname(),
		    is(presidentDto.getFirstname()));

	}

	/**
	 * Test method for {@link PresidentResource#update(PresidentDto)} that
	 * causes a SQL constraint error.
	 */
	@Test
	public void testUpdateErrorBadInaugYear() {

		PresidentDto params = new PresidentDto();
		params.setLastname("Washington");
		params.setFirstname("George");
		List<PresidentDto> presidentDtos =
		    presidentDao.selectByStatement("query.selectByName", params);

		PresidentDto presidentDto = presidentDtos.get(0);
		presidentDto.setInauguratedYear(1787);

		Response response =
		    target("/president/").request(MediaType.APPLICATION_JSON_TYPE).post(
		        Entity.entity(presidentDto, MediaType.APPLICATION_JSON_TYPE),
		        Response.class);

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{president.wrong.inaugurated.year}"));
		}

	}

	/**
	 * Test method for {@link PresidentResource#update(PresidentDto)} with a
	 * null party. Note that a president is allowed to have no party (e.g.
	 * George Washington).
	 * 
	 */
	@Test
	public void testUpdateNullParty() {

		PresidentDto params = new PresidentDto();
		params.setLastname("Monroe");
		params.setFirstname("James");
		List<PresidentDto> presidentDtos =
		    presidentDao.selectByStatement("query.selectByName", params);

		PresidentDto presidentDto = presidentDtos.get(0);
		presidentDto.setPartyId(null);

		displayJSON(presidentDto);

		PresidentDto updPresidentDto =
		    target("/president/").request(MediaType.APPLICATION_JSON_TYPE).put(
		        Entity.entity(presidentDto, MediaType.APPLICATION_JSON_TYPE),
		        PresidentDto.class);

		assertThat(updPresidentDto.getPartyId(), is(presidentDto.getPartyId()));

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
