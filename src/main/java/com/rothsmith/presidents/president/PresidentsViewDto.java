/*
 * Copyright (c) 2016 Rothsmith, LLC, All rights reserved.
 */
package com.rothsmith.presidents.president;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * DTO generated from SQL statement.
 *
 * SELECT * FROM PRESIDENTS_VIEW .
 *
 * @author drothauser
 */
public final class PresidentsViewDto implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = -4065109031261094713L;

	/**
	 * Member variable represents database field ID.
	 */
	private Integer id;

	/**
	 * Member variable represents database field LASTNAME.
	 */
	private String lastname;

	/**
	 * Member variable represents database field FIRSTNAME.
	 */
	private String firstname;

	/**
	 * Member variable represents database field INAUGURATED_YEAR.
	 */
	private Integer inauguratedYear;

	/**
	 * Member variable represents database field YEARS.
	 */
	private BigDecimal years;

	/**
	 * Member variable represents database field STATE.
	 */
	private String state;

	/**
	 * Member variable represents database field PARTY.
	 */
	private String party;

	/**
	 * Default constructor.
	 */
	public PresidentsViewDto() {
		// Default constructor
	}

	/**
	 * Private constructor used by newInstance factory method to create a copy
	 * of another PresidentsViewDto instance.
	 *
	 * @param presidentsViewDto
	 *            an instance of another {@link PresidentsViewDto} object.
	 */
	private PresidentsViewDto(final PresidentsViewDto presidentsViewDto) {

		id = presidentsViewDto.getId();
		lastname = presidentsViewDto.getLastname();
		firstname = presidentsViewDto.getFirstname();
		inauguratedYear = presidentsViewDto.getInauguratedYear();
		years = presidentsViewDto.getYears();
		state = presidentsViewDto.getState();
		party = presidentsViewDto.getParty();

	}

	/**
	 * Factory method to return a copy of the given PresidentsViewDto instance.
	 *
	 * @param presidentsViewDto
	 *            an instance of another {@link PresidentsViewDto} object.
	 *
	 * @return a copy of presidentsViewDto.
	 */
	public static PresidentsViewDto newInstance(
	    final PresidentsViewDto presidentsViewDto) {
		return new PresidentsViewDto(presidentsViewDto);
	}

	/**
	 * @return the president id.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the president id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Accessor for lastname.
	 *
	 * @return lastname String to get
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * Mutator for lastname.
	 *
	 * @param varLastname
	 *            String to set
	 */
	public void setLastname(final String varLastname) {
		lastname = varLastname;
	}

	/**
	 * Accessor for firstname.
	 *
	 * @return firstname String to get
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * Mutator for firstname.
	 *
	 * @param varFirstname
	 *            String to set
	 */
	public void setFirstname(final String varFirstname) {
		firstname = varFirstname;
	}

	/**
	 * Accessor for inauguratedYear.
	 *
	 * @return inauguratedYear Integer to get
	 */
	public Integer getInauguratedYear() {
		return inauguratedYear;
	}

	/**
	 * Mutator for inauguratedYear.
	 *
	 * @param varInauguratedYear
	 *            Integer to set
	 */
	public void setInauguratedYear(final Integer varInauguratedYear) {
		inauguratedYear = varInauguratedYear;
	}

	/**
	 * Accessor for years.
	 *
	 * @return years BigDecimal to get
	 */
	public BigDecimal getYears() {
		return years;
	}

	/**
	 * Mutator for years.
	 *
	 * @param varYears
	 *            BigDecimal to set
	 */
	public void setYears(final BigDecimal varYears) {
		years = varYears;
	}

	/**
	 * Accessor for state.
	 *
	 * @return state String to get
	 */
	public String getState() {
		return state;
	}

	/**
	 * Mutator for state.
	 *
	 * @param varState
	 *            String to set
	 */
	public void setState(final String varState) {
		state = varState;
	}

	/**
	 * Accessor for party.
	 *
	 * @return party String to get
	 */
	public String getParty() {
		return party;
	}

	/**
	 * Mutator for party.
	 *
	 * @param varParty
	 *            String to set
	 */
	public void setParty(final String varParty) {
		party = varParty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
		    ToStringStyle.MULTI_LINE_STYLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
