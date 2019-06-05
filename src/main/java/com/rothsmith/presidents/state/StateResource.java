/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.state;

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
 * United States state maintenance REST resource.
 * 
 * @author drothauser
 *
 */
@Path("/state")
@Produces(MediaType.APPLICATION_JSON)
public class StateResource {

	/**
	 * Service for maintaining the STATE table.
	 */
	private final StateService stateService;

	/**
	 * Constructor that sets the {@link StateService}.
	 */
	public StateResource() {
		this.stateService = new StateService();
	}

	/**
	 * Create a state record.
	 * 
	 * @param stateDto
	 *            {@link StateDto} instance
	 * @return the given {@link StateDto} including the new id to marshal by
	 *         Jersey
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public StateDto create(@NotNull @Valid @DupCheck StateDto stateDto) {

		return stateService.create(stateDto);

	}

	/**
	 * Delete state record for the given id.
	 * 
	 * @param id
	 *            the state Id of the record to delete
	 */
	@DELETE
	@Path("{id}")
	public void delete(
	    @Valid @PathParam("id") @HasId @HasPresidentsCheck Integer id) {

		stateService.delete(id);

	}

	/**
	 * Retrieve all state records.
	 * 
	 * @return {@link List} of {@link StateDto}s to marshal by Jersey
	 */
	@GET
	public List<StateDto> list() {

		return stateService.list();

	}

	/**
	 * Return a STATE record for the given id.
	 * 
	 * @param id
	 *            the state Id of the record to delete
	 * @return {@link StateDto} to marshal by Jersey
	 * 
	 */
	@GET
	@Path("{id}")
	public StateDto read(@Valid @PathParam("id") @HasId Integer id) {

		return stateService.read(id);

	}

	/**
	 * Update state data.
	 * 
	 * @param stateDto
	 *            {@link StateDto} instance
	 * @return updated {@link StateDto} to marshal by Jersey
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public StateDto update(@Valid @NotNull @DupCheck StateDto stateDto) {

		return stateService.update(stateDto);

	}

}
