/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.party;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rothsmith.dao.dbutils.DbUtilsJdbcDao;

/**
 * Service for maintaining the U.S. Party data.
 * 
 * @author drothauser
 *
 */
public class PartyService {

	/**
	 * SLF4J Logger for PartyRestService.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(PartyService.class);

	/**
	 * {@link DbUtilsJdbcDao} to test.
	 */
	private final DbUtilsJdbcDao<PartyDto, PartyDto> partyDao;

	/**
	 * Constructor that sets the party DAO.
	 */
	public PartyService() {
		partyDao =
		    new DbUtilsJdbcDao<PartyDto, PartyDto>("/partydao.properties");
	}

	/**
	 * Create a PARTY record.
	 * 
	 * @param partyDto
	 *            {@link PartyDto} instance containing the data for the new
	 *            PARTY record
	 * @return {@link PartyDto} instance with the id attribute that was
	 *         generated by the SQL insert operation
	 * 
	 */
	public PartyDto create(final PartyDto partyDto) {

		LOGGER
		    .debug(String.format("Creating party with the following data:%n%s",
		        partyDto.toString()));

		Integer id = partyDao.insert(partyDto);
		LOGGER.info(String.format("Created party with party id = %d for:%n %s",
		    id, partyDto));

		partyDto.setId(id);

		String msg =
		    String.format("Successfully added party: %s.", partyDto.getName());
		LOGGER.info(msg);

		return partyDto;

	}

	/**
	 * Delete record for the given id.
	 * 
	 * @param id
	 *            the party Id of the record to delete
	 * @return the number of records deleted. If successful, should equal 1.
	 * 
	 */
	public Integer delete(Integer id) {

		PartyDto partyDto = new PartyDto();

		partyDto.setId(id);

		int count = partyDao.delete(partyDto);

		String msg = String.format("Deleted %d party.", count);
		LOGGER.info(msg);

		return count;

	}

	/**
	 * Retrieve all party records.
	 * 
	 * @return {@link List} of {@link PartyDto}s.
	 */
	public List<PartyDto> list() {

		return partyDao.selectByStatement("query.selectAll");

	}

	/**
	 * Return a PARTY record for the given id.
	 * 
	 * @param id
	 *            the state Id of the record to read
	 * @return the {@link PartyDto} for the given id.
	 * 
	 */
	public PartyDto read(final Integer id) {

		PartyDto params = new PartyDto();
		params.setId(id);

		List<PartyDto> partyDtos = partyDao.select(params);
		PartyDto partyDto = partyDtos.get(0);
		LOGGER.info(String.format("Party data:%n%s", partyDto));

		return partyDto;

	}

	/**
	 * Update party data.
	 * 
	 * @param partyDto
	 *            {@link PartyDto} instance
	 * @return the {@link PartyDto} with the updated data
	 */
	public PartyDto update(final PartyDto partyDto) {

		LOGGER.debug(String.format("Updating  with the following data:%n%s",
		    partyDto.toString()));

		int count = partyDao.update(partyDto);
		LOGGER.debug(String.format("Updated %d PARTY record(s).", count));

		String msg = String.format("Updated %s", partyDto.getName());
		LOGGER.info(msg);

		return partyDto;

	}

}
