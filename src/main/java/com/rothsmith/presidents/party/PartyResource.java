/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.party;

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
 * Political party maintenance REST resource.
 * 
 * @author drothauser
 *
 */
@Path("/party")
@Produces(MediaType.APPLICATION_JSON)
public class PartyResource {

	/**
	 * Service for maintaining the PARTY table.
	 */
	private final PartyService partyService;

	/**
	 * Constructor that sets the {@link PartyService}.
	 */
	public PartyResource() {
		this.partyService = new PartyService();
	}

	/**
	 * Create a new party.
	 * 
	 * @param partyDto
	 *            {@link PartyDto} instance
	 * @return the given {@link PartyDto} including the new id to marshal by
	 *         Jersey
	 * 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public PartyDto create(@NotNull @Valid @DupCheck PartyDto partyDto) {

		return partyService.create(partyDto);

	}

	/**
	 * Delete record for the given id.
	 * 
	 * @param id
	 *            the party Id of the record to delete
	 * 
	 */
	@DELETE
	@Path("{id}")
	public void delete(
	    @Valid @PathParam("id") @HasId @HasPresidentsCheck Integer id) {

		partyService.delete(id);

	}

	/**
	 * Retrieve all party records.
	 * 
	 * @return {@link List} of {@link PartyDto}s to marshal by Jersey
	 */
	@GET
	public List<PartyDto> list() {

		return partyService.list();

	}

	/**
	 * Return party data for the given id.
	 * 
	 * @param id
	 *            the party Id of the record to read.
	 * @return {@link PartyDto} to marshal by Jersey
	 * 
	 */
	@GET
	@Path("{id}")
	public PartyDto read(@Valid @PathParam("id") @HasId Integer id) {

		return partyService.read(id);

	}

	/**
	 * Update party data.
	 * 
	 * @param partyDto
	 *            {@link PartyDto} instance
	 * @return updated {@link PartyDto} to marshal by Jersey
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public PartyDto update(@NotNull @Valid @DupCheck PartyDto partyDto) {

		return partyService.update(partyDto);

	}

}
