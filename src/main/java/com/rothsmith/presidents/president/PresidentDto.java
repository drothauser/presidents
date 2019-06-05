/*
 * Copyright (c) 2016 Rothsmith, LLC, All rights reserved.
 */
package com.rothsmith.presidents.president;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * DTO generated from SQL statement.
 *
 * select ID , FIRSTNAME , LASTNAME , STATE_ID , PARTY_ID , INAUGURATED_YEAR ,
 * YEARS from TEST.PRESIDENT.
 *
 * @author drothauser
 */
@SuppressWarnings("checkstyle:magicnumber")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class PresidentDto implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -4222395978380053453L;

	/**
	 * Member variable represents database field ID.
	 */
	@NotNull(message = "{president.wrong.id}")
	@DecimalMin(value = "0", message = "{president.wrong.id}")
	private Integer id;

	/**
	 * Member variable represents database field FIRSTNAME.
	 */
	@NotNull(message = "{president.required.firstname}")
	@Length(min = 2, max = 40, message = "{president.wrong.firstname}")
	private String firstname;

	/**
	 * Member variable represents database field LASTNAME.
	 */
	@NotNull(message = "{president.required.lastname}")
	@Length(min = 2, max = 40, message = "{president.wrong.firstname}")
	private String lastname;

	/**
	 * Member variable represents database field STATE_ID.
	 */
	@NotNull(message = "{president.wrong.stateid}")
	@DecimalMin(value = "0", message = "{president.wrong.stateid}")
	private Integer stateId;

	/**
	 * Member variable represents database field PARTY_ID.
	 */
	@XmlElement(nillable = true)
	@DecimalMin(value = "0", message = "{president.wrong.partyid}")
	private Integer partyId;

	/**
	 * Member variable represents database field INAUGURATED_YEAR.
	 */
	@NotNull(message = "{president.required.year}")
	@Range(min = 1789, max = 9999,
	        message = "{president.wrong.inaugurated.year}")
	private Integer inauguratedYear;

	/**
	 * Member variable represents database field YEARS.
	 */
	@NotNull(message = "{president.required.years}")
	@Range(min = 1, max = 12, message = "{president.wrong.years}")
	private BigDecimal years;

	/**
	 * Default constructor.
	 */
	public PresidentDto() {
		// Default constructor
	}

	/**
	 * Private constructor used by newInstance factory method to create a copy
	 * of another PresidentDto instance.
	 *
	 * @param presidentDto
	 *            an instance of another {@link PresidentDto} object.
	 */
	private PresidentDto(final PresidentDto presidentDto) {

		id = presidentDto.getId();
		firstname = presidentDto.getFirstname();
		lastname = presidentDto.getLastname();
		stateId = presidentDto.getStateId();
		partyId = presidentDto.getPartyId();
		inauguratedYear = presidentDto.getInauguratedYear();
		years = presidentDto.getYears();

	}

	/**
	 * Factory method to return a copy of the given PresidentDto instance.
	 *
	 * @param presidentDto
	 *            an instance of another {@link PresidentDto} object.
	 *
	 * @return a copy of presidentDto.
	 */
	public static PresidentDto newInstance(final PresidentDto presidentDto) {
		return new PresidentDto(presidentDto);
	}

	/**
	 * Accessor for id.
	 *
	 * @return id Integer to get
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Mutator for id.
	 *
	 * @param varId
	 *            Integer to set
	 */
	public void setId(final Integer varId) {
		id = varId;
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
	 * Accessor for stateId.
	 *
	 * @return stateId Integer to get
	 */
	public Integer getStateId() {
		return stateId;
	}

	/**
	 * Mutator for stateId.
	 *
	 * @param varStateId
	 *            Integer to set
	 */
	public void setStateId(final Integer varStateId) {
		stateId = varStateId;
	}

	/**
	 * Accessor for partyId.
	 *
	 * @return partyId Integer to get
	 */
	public Integer getPartyId() {
		return partyId;
	}

	/**
	 * Mutator for partyId.
	 *
	 * @param varPartyId
	 *            Integer to set
	 */
	public void setPartyId(final Integer varPartyId) {
		partyId = varPartyId;
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
