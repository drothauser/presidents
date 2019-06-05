/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.party;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.apache.commons.collections.CollectionUtils;

import com.rothsmith.dao.dbutils.DbUtilsJdbcDao;

/**
 * Checks whether the data in the given {@link PartyDto} object already exists
 * in the PARTY table.
 *
 * @author drothauser
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { DupCheck.Validator.class })
@Documented
public @interface DupCheck {

	/**
	 * @return the default error message.
	 */
	String message() default "{party.exists}";

	/**
	 * @return validation classes
	 */
	Class<?>[] groups() default {};

	/**
	 * @return error metadata
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * Checks if a party already exists.
	 * 
	 * @author drothauser
	 *
	 */
	class Validator implements ConstraintValidator<DupCheck, PartyDto> {

		/**
		 * DAO to maintain the PARTY table. See {@link DbUtilsJdbcDao}.
		 */
		private DbUtilsJdbcDao<PartyDto, PartyDto> partyDao;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void initialize(final DupCheck dupCheck) {
			this.partyDao =
			    new DbUtilsJdbcDao<PartyDto, PartyDto>("/partydao.properties");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isValid(final PartyDto partyDto,
		    final ConstraintValidatorContext constraintValidatorContext) {

			List<PartyDto> partyDtos = partyDao
			    .selectByStatement("query.nameConstraintCheck", partyDto);
			return CollectionUtils.isEmpty(partyDtos);
		}
	}

}
