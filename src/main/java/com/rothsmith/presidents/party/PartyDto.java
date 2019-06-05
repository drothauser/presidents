/*
 * Copyright (c) 2016 Rothsmith, LLC, All rights reserved.
 */
package com.rothsmith.presidents.party;

import java.io.Serializable;

import javax.validation.GroupSequence;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
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
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

/**
 * DTO generated from SQL statement.
 *
 * select ID , NAME , FOUNDED_YEAR , END_YEAR from TEST.PARTY
 *
 * @author drothauser
 */
@SuppressWarnings("checkstyle:magicnumber")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@PartyYearCompare(year1 = "foundedYear", year2 = "endYear",
        message = "{party.wrong.end.year}",
        groups = { ComplexValidation.class })
@GroupSequence({ PartyDto.class, BasicValidation.class,
    ComplexValidation.class })
public final class PartyDto implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -2957259947738713430L;

	/**
	 * Member variable represents database field ID.
	 */
	@NotNull(message = "{party.wrong.id}")
	@DecimalMin(value = "0", message = "{party.wrong.id}")
	private Integer id;

	/**
	 * Member variable represents database field NAME.
	 */
	@NotBlank(message = "{party.required.name}",
	        groups = { BasicValidation.class })
	@Length(min = 3, max = 255, message = "{party.wrong.name}",
	        groups = { ComplexValidation.class })
	private String name;

	/**
	 * Member variable represents database field FOUNDED_YEAR.
	 */
	@XmlElement(nillable = true)
	@NotNull(message = "{party.required.founded.year}",
	        groups = { BasicValidation.class })
	@Min(value = 1, message = "{party.required.founded.year}",
	        groups = { BasicValidation.class })
	@Range(min = 1789, max = 9999, message = "{party.wrong.founded.year}",
	        groups = { ComplexValidation.class })
	private Integer foundedYear;

	/**
	 * Member variable represents database field END_YEAR.
	 */
	@XmlElement(nillable = true)
	// @Range(min = 1791, max = 9999, message = "{party.wrong.end.year}",
	// groups = { ComplexValidation.class })
	private Integer endYear;

	/**
	 * Default constructor.
	 */
	public PartyDto() {
		// Default constructor
	}

	/**
	 * Private constructor used by newInstance factory method to create a copy
	 * of another PartyDto instance.
	 *
	 * @param partyDto
	 *            an instance of another {@link PartyDto} object.
	 */
	private PartyDto(final PartyDto partyDto) {

		id = partyDto.getId();
		name = partyDto.getName();
		foundedYear = partyDto.getFoundedYear();
		endYear = partyDto.getEndYear();

	}

	/**
	 * Factory method to return a copy of the given PartyDto instance.
	 *
	 * @param partyDto
	 *            an instance of another {@link PartyDto} object.
	 *
	 * @return a copy of partyDto.
	 */
	public static PartyDto newInstance(final PartyDto partyDto) {
		return new PartyDto(partyDto);
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
	 * Accessor for name.
	 *
	 * @return name String to get
	 */
	public String getName() {
		return name;
	}

	/**
	 * Mutator for name.
	 *
	 * @param varName
	 *            String to set
	 */
	public void setName(final String varName) {
		name = varName;
	}

	/**
	 * Accessor for foundedYear.
	 *
	 * @return foundedYear Integer to get
	 */
	public Integer getFoundedYear() {
		return foundedYear;
	}

	/**
	 * Mutator for foundedYear.
	 *
	 * @param varFoundedYear
	 *            Integer to set
	 */
	public void setFoundedYear(final Integer varFoundedYear) {
		foundedYear = varFoundedYear;
	}

	/**
	 * Accessor for endYear.
	 *
	 * @return endYear Integer to get
	 */
	public Integer getEndYear() {
		return endYear;
	}

	/**
	 * Mutator for endYear. Note that 0 is interpreted as no end year and as
	 * such convert to a null.
	 *
	 * @param varEndYear
	 *            Integer to set
	 */
	@SuppressWarnings("PMD.NullAssignment")
	public void setEndYear(final Integer varEndYear) {

		endYear = varEndYear != null && varEndYear == 0 ? null : varEndYear;
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
