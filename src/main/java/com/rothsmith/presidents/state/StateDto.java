/*
 * Copyright (c) 2016 Rothsmith, LLC, All rights reserved.
 */
package com.rothsmith.presidents.state;

import java.io.Serializable;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;

/**
 * DTO generated from SQL statement.
 *
 * select ID , NAME , ABBREVIATION from TEST.STATE .
 *
 * @author drothauser
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class StateDto implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 7181559842413658020L;

	/**
	 * Member variable represents database field ID.
	 */
	@NotNull(message = "{state.wrong.id}")
	@DecimalMin(value = "0", message = "{state.wrong.id}")
	private Integer id;

	// CHECKSTYLE:OFF Magic numbers ok in annotations.
	/**
	 * Member variable represents database field NAME.
	 */
	@NotNull(message = "{state.required.name}")
	@Length(min = 3, max = 255, message = "{state.wrong.name}")
	private String name;

	/**
	 * Member variable represents database field ABBREVIATION.
	 */
	@NotNull(message = "{state.required.abbr}")
	@Length(min = 2, max = 2, message = "{state.abbr.wrong.length}")
	private String abbreviation;

	// CHECKSTYLE:ON

	/**
	 * Default constructor.
	 */
	public StateDto() {
		// Default constructor
	}

	/**
	 * Private constructor used by newInstance factory method to create a copy
	 * of another StateDto instance.
	 *
	 * @param stateDto
	 *            an instance of another {@link StateDto} object.
	 */
	private StateDto(final StateDto stateDto) {

		id = stateDto.getId();
		name = stateDto.getName();
		abbreviation = stateDto.getAbbreviation();

	}

	/**
	 * Factory method to return a copy of the given StateDto instance.
	 *
	 * @param stateDto
	 *            an instance of another {@link StateDto} object.
	 *
	 * @return a copy of stateDto.
	 */
	public static StateDto newInstance(final StateDto stateDto) {
		return new StateDto(stateDto);
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
	public void setId(Integer varId) {
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
	public void setName(String varName) {
		name = varName;
	}

	/**
	 * Accessor for abbreviation.
	 *
	 * @return abbreviation String to get
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * Mutator for abbreviation.
	 *
	 * @param varAbbreviation
	 *            String to set
	 */
	public void setAbbreviation(String varAbbreviation) {
		abbreviation = varAbbreviation;
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
