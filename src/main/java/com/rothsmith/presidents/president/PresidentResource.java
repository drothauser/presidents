/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.president;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * United States president maintenance REST resource.
 * 
 * @author drothauser
 *
 */
@Path("/president")
@Produces(MediaType.APPLICATION_JSON)
public class PresidentResource {

	/**
	 * Service for maintaining U.S. President data.
	 */
	private final PresidentService presidentService;

	/**
	 * Constructor that sets the {@link PresidentService}.
	 */
	public PresidentResource() {

		this.presidentService = new PresidentService();
	}

	/**
	 * Create a record.
	 * 
	 * @param presidentDto
	 *            {@link PresidentDto} instance
	 * @return the given {@link PresidentDto} including the new id to marshal by
	 *         Jersey
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public PresidentDto create(@NotNull @Valid PresidentDto presidentDto) {

		return presidentService.create(presidentDto);

	}

	/**
	 * Delete record for the given id.
	 * 
	 * @param id
	 *            the president Id of the record to delete
	 */
	@DELETE
	@Path("{id}")
	public void delete(@Valid @PathParam("id") @HasId(
	        message = "{president.does.not.exist}") Integer id) {

		presidentService.delete(id);

	}

	/**
	 * Retrieve all president records.
	 * 
	 * @return {@link List} of {@link PresidentsViewDto}s to marshal by Jersey
	 */
	@GET
	public List<PresidentsViewDto> list() {

		return presidentService.list();

	}

	/**
	 * Return a PRESIDENT record for the given id.
	 * 
	 * @param id
	 *            the president Id of the record to delete
	 * @return {@link PresidentDto} to marshal by Jersey
	 */
	@GET
	@Path("{id}")
	public PresidentDto read(@Valid @PathParam("id") @HasId Integer id) {

		return presidentService.read(id);

	}

	/**
	 * Update president data.
	 * 
	 * @param presidentDto
	 *            {@link PresidentDto} instance from UI
	 * @return updated {@link PresidentDto} to marshal by Jersey
	 * 
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public PresidentDto update(@NotNull @Valid PresidentDto presidentDto) {

		return presidentService.update(presidentDto);

	}

}
