/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.party;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.hamcrest.Matchers;
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
import com.rothsmith.presidents.party.PartyDto;
import com.rothsmith.presidents.party.PartyService;
import com.rothsmith.presidents.president.PresidentDto;

/**
 * Tests for the Party REST service resource for accessing political party data.
 * 
 * @author drothauser
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods",
    "PMD.GodClass", "checkstyle:magicnumber" })
public class PartyResourceTest
        extends JerseyTest {

	/**
	 * SLF4J Logger for PartyRestServiceTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(PartyResourceTest.class);

	/**
	 * {@link DbUtilsJdbcDao} to aid in testing.
	 */
	private final DbUtilsJdbcDao<PartyDto, PartyDto> partyDao =
	    new DbUtilsJdbcDao<PartyDto, PartyDto>("/partydao.properties");

	/**
	 * {@link PartyService} to aid in testing.
	 */
	private final PartyService partyService = new PartyService();

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
	 * Test method for {@link PartyService#list()}.
	 */
	@Test
	public void testList() {

		Response response = target("/party/").request()
		    .accept(MediaType.APPLICATION_JSON).get();

		List<PartyDto> parties =
		    response.readEntity(new GenericType<List<PartyDto>>() {
		    });

		assertThat(CollectionUtils.isEmpty(parties), is(false));

		for (PartyDto partyDto : parties) {
			LOGGER.info(partyDto.toString());
		}

	}

	/**
	 * Test method for {@link PartyService#read(String)}.
	 */
	@Test
	public void testRead() {

		PartyDto params = new PartyDto();
		params.setName("Democratic Party");
		List<PartyDto> partyDtos =
		    partyDao.selectByStatement("query.selectByName", params);

		PartyDto testDto = partyDtos.get(0);

		Response response = target("/party/" + testDto.getId())
		    .request(MediaType.APPLICATION_JSON).get(Response.class);

		PartyDto partytDto = response.readEntity(PartyDto.class);

		assertThat(partytDto.getName(), is(testDto.getName()));

	}

	/**
	 * Test method for {@link PartyService#read(String)} using an invalid id.
	 */
	@Test
	public void testReadError() {

		int id = -1;

		Response response = target("/party/" + id)
		    .request(MediaType.APPLICATION_JSON).get(Response.class);

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (final ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{party.does.not.exist}"));
		}

	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} .
	 */
	@Test
	public void testCreate() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("Socialist Party of America");
		partyDto.setFoundedYear(1901);
		partyDto.setEndYear(1972);

		displayJSON(partyDto);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		PartyDto partyDtoNew = response.readEntity(PartyDto.class);

		assertThat(partyDtoNew.getId(), greaterThan(0));
	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} with a null name.
	 */
	@Test
	public void testCreateErrorNullName() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName(null);
		partyDto.setFoundedYear(1901);
		partyDto.setEndYear(1972);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{party.required.name}"));
		}
	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} with a name
	 * containing empty string.
	 */
	@Test
	public void testCreateErrorEmptyName() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("");
		partyDto.setFoundedYear(1901);
		partyDto.setEndYear(1972);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{party.required.name}"));
		}
	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} with a name
	 * containing a bunch of spaces.
	 */
	@Test
	public void testCreateErrorBlankName() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("     ");
		partyDto.setFoundedYear(1901);
		partyDto.setEndYear(1972);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{party.required.name}"));
		}
	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} using a party that
	 * already exists.
	 */
	@Test
	public void testCreateDuplicate() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("Democratic Party");
		partyDto.setFoundedYear(1901);
		partyDto.setEndYear(1972);

		displayJSON(partyDto);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{party.exists}"));
		}
	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} using an empty name
	 * and null years.
	 */
	@Test
	public void testCreateNullData() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName(null);
		partyDto.setFoundedYear(null);
		partyDto.setEndYear(null);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);

		List<String> messages = validationErrorList.stream()
		    .map(ValidationError::getMessageTemplate)
		    .collect(Collectors.toCollection(ArrayList::new));

		messages.forEach(item -> LOGGER.info(item));

		assertThat(messages,
		    is(Matchers.containsInAnyOrder("{party.required.name}",
		        "{party.required.founded.year}")));

	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} using an empty name
	 * and years that are zero.
	 */
	@Test
	public void testCreateEmptyData() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("");
		partyDto.setFoundedYear(0);
		partyDto.setEndYear(0);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);

		List<String> messages = validationErrorList.stream()
		    .map(ValidationError::getMessageTemplate)
		    .collect(Collectors.toCollection(ArrayList::new));

		messages.forEach(item -> LOGGER.info(item));

		assertThat(messages,
		    is(Matchers.containsInAnyOrder("{party.required.name}",
		        "{party.required.founded.year}")));

	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} with a party that
	 * hasn't dissolved yet.
	 */
	@Test
	public void testCreateNoEndYear() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("Libertarian Party");
		partyDto.setFoundedYear(1971);
		partyDto.setEndYear(null);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		PartyDto partyDtoNew = response.readEntity(PartyDto.class);
		assertThat(partyDtoNew.getName(), is("Libertarian Party"));
	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} with a party that
	 * has a founded year and an end year of zero.
	 */
	@Test
	public void testCreateZeroEndYear() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("Constitution Party");
		partyDto.setFoundedYear(1991);
		partyDto.setEndYear(0);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		PartyDto partyDtoNew = response.readEntity(PartyDto.class);
		assertThat(partyDtoNew.getName(), is("Constitution Party"));
	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} with a party that
	 * has an invalid founded year and an end year that is prior to the founded
	 * year.
	 */
	@Test
	public void testCreateErrorFoundedYearEndYear() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("National Republican Party");
		partyDto.setFoundedYear(1770);
		partyDto.setEndYear(1700);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);

		List<String> messages = validationErrorList.stream()
		    .map(ValidationError::getMessageTemplate)
		    .collect(Collectors.toCollection(ArrayList::new));

		messages.forEach(item -> LOGGER.info(item));

		assertThat(messages,
		    is(Matchers.containsInAnyOrder("{party.wrong.founded.year}",
		        "{party.wrong.end.year}")));
	}

	/**
	 * Test method for {@link PartyService#create(PartyDto)} with a null end
	 * year.
	 */
	@Test
	public void testCreateNullEndYear() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("Pool Party");
		partyDto.setFoundedYear(1901);
		partyDto.setEndYear(null);

		displayJSON(partyDto);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		PartyDto partyDtoNew = response.readEntity(PartyDto.class);

		assertThat(partyDtoNew.getId(), greaterThan(0));
	}

	/**
	 * Test method for {@link PartyService#create(PresidentDto)} with a null
	 * founded year.
	 */
	@Test
	public void testCreateErrorNullFoundedYear() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("Grand Very Old Party");
		partyDto.setFoundedYear(null);
		partyDto.setEndYear(1872);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{party.required.founded.year}"));
		}
	}

	/**
	 * Test method for {@link PartyService#create(PresidentDto)} with a founded
	 * year less than the minimum - 1789.
	 */
	@Test
	public void testCreateErrorBadFoundedYear() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("Grand Very Old Party");
		partyDto.setFoundedYear(1788);
		partyDto.setEndYear(1872);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{party.wrong.founded.year}"));
		}
	}

	/**
	 * Test method for {@link PartyService#create(PresidentDto)} with an end
	 * year less than the founded year's minimum.
	 */
	@Test
	public void testCreateErrorEndLTFounded2() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("Grand Very Old Party");
		partyDto.setFoundedYear(2016);
		partyDto.setEndYear(1);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{party.wrong.end.year}"));
		}
	}

	/**
	 * Test method for {@link PartyService#create(PresidentDto)} with an end
	 * year less than the minimum.
	 */
	@Test
	public void testCreateErrorEndLTFounded() {

		PartyDto partyDto = new PartyDto();
		partyDto.setId(0);
		partyDto.setName("Grand Very Old Party");
		partyDto.setFoundedYear(1790);
		partyDto.setEndYear(1789);

		Response response =
		    target().path("/party").request(MediaType.APPLICATION_JSON_TYPE)
		        .post(Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE));

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{party.wrong.end.year}"));
		}
	}

	/**
	 * Test method for {@link PartyService#update(PartyDto)}.
	 */
	@Test
	public void testUpdateName() {

		PartyDto params = new PartyDto();
		params.setName("Whig Party");
		List<PartyDto> partyDtos =
		    partyDao.selectByStatement("query.selectByName", params);

		PartyDto partyDto = partyDtos.get(0);
		partyDto.setName("Anti-Masonic Party");

		displayJSON(partyDto);

		PartyDto updPartyDto =
		    target("/party/").request(MediaType.APPLICATION_JSON_TYPE).put(
		        Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE),
		        PartyDto.class);

		assertThat(updPartyDto.getEndYear(), is(partyDto.getEndYear()));

	}

	/**
	 * Test method for {@link PartyService#update(PartyDto)} with a null end
	 * date. Test should succeed as an empty end date is permitted.
	 */
	@Test
	public void testUpdateNullEndDate() {

		PartyDto params = new PartyDto();
		params.setName("Federalist Party");
		List<PartyDto> partyDtos =
		    partyDao.selectByStatement("query.selectByName", params);

		PartyDto partyDto = partyDtos.get(0);
		partyDto.setEndYear(null);

		displayJSON(partyDto);

		PartyDto updPartyDto =
		    target("/party/").request(MediaType.APPLICATION_JSON_TYPE).put(
		        Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE),
		        PartyDto.class);

		assertThat(updPartyDto.getEndYear(), is(partyDto.getEndYear()));

	}

	/**
	 * Test method for {@link PartyService#update(PartyDto)} that causes a
	 * unique constraint error because the changed name already exists in
	 * another record.
	 */
	@Test
	public void testUpdateErrorDuplicate() {

		PartyDto params = new PartyDto();
		params.setName("Democratic-Republican Party");
		List<PartyDto> partyDtos =
		    partyDao.selectByStatement("query.selectByName", params);

		PartyDto partyDto = partyDtos.get(0);
		partyDto.setName("Democratic Party");

		displayJSON(partyDto);

		Response response =
		    target("/party/").request(MediaType.APPLICATION_JSON_TYPE).put(
		        Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE),
		        Response.class);

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{party.exists}"));
		}

	}

	/**
	 * Test method for {@link PartyService#update(PartyDto)} that causes a
	 * duplicate key error.
	 */
	@Test
	public void testUpdateErrorShortName() {

		PartyDto params = new PartyDto();
		params.setName("Democratic-Republican Party");
		List<PartyDto> partyDtos =
		    partyDao.selectByStatement("query.selectByName", params);

		PartyDto partyDto = partyDtos.get(0);
		partyDto.setName("DP");

		Response response =
		    target("/party/").request(MediaType.APPLICATION_JSON_TYPE).put(
		        Entity.entity(partyDto, MediaType.APPLICATION_JSON_TYPE),
		        Response.class);

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (ValidationError validationError : validationErrorList) {
			LOGGER.info(validationError.getMessage());
			assertThat(validationError.getMessageTemplate(),
			    is("{party.wrong.name}"));
		}

	}

	/**
	 * Test method for {@link PartyService#delete(Integer)}.
	 */
	@Test
	public void testDelete() {

		String testParty = "Green Party";

		PartyDto testDto = new PartyDto();
		testDto.setId(0);
		testDto.setName(testParty);
		testDto.setFoundedYear(2001);
		testDto.setEndYear(null);

		partyService.create(testDto);

		PartyDto partyDto = partyService.read(testDto.getId());

		Response response = target("/party/" + partyDto.getId())
		    .request(MediaType.APPLICATION_JSON).delete();

		assertThat(response.getStatus(),
		    is(Response.Status.NO_CONTENT.getStatusCode()));

	}

	/**
	 * Test method for {@link PartyService#delete(String)} using a party id that
	 * the doesn't exist.
	 */
	@Test
	public void testDeleteErrorBadId() {

		int bogusPartyId = -1;

		Response response = target("/party/" + bogusPartyId)
		    .request(MediaType.APPLICATION_JSON).delete();

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (final ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{party.does.not.exist}"));
		}

	}

	/**
	 * Test method for {@link PartyService#delete(String)} using a party id that
	 * has one or more presidents relationally tied to it.
	 */
	@Test
	public void testDeleteErrorHasPres() {

		Integer federalistPartyId = 1;

		Response response = target("/party/" + federalistPartyId)
		    .request(MediaType.APPLICATION_JSON).delete();

		List<ValidationError> validationErrorList =
		    getValidationErrorList(response);
		for (final ValidationError validationError : validationErrorList) {
			assertThat(validationError.getMessageTemplate(),
			    is("{party.has.presidents}"));
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
