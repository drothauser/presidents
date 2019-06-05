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
 * Checks whether a given id exists in the PARTY table.
 *
 * @author drothauser
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { HasId.Validator.class })
@Documented
public @interface HasId {

	/**
	 * @return the default error message.
	 */
	String message() default "{party.does.not.exist}";

	/**
	 * @return validation classes
	 */
	Class<?>[] groups() default {};

	/**
	 * @return error metadata
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * Validates the party's id.
	 * 
	 * @author drothauser
	 *
	 */
	class Validator implements ConstraintValidator<HasId, Integer> {

		/**
		 * DAO to maintain the PARTY table. See {@link DbUtilsJdbcDao}.
		 */
		private DbUtilsJdbcDao<PartyDto, PartyDto> partyDao;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void initialize(final HasId hasId) {
			this.partyDao =
			    new DbUtilsJdbcDao<PartyDto, PartyDto>("/partydao.properties");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isValid(final Integer id,
		    final ConstraintValidatorContext constraintValidatorContext) {

			PartyDto params = new PartyDto();
			params.setId(id);
			List<PartyDto> partyDtos = partyDao.select(params);
			return !CollectionUtils.isEmpty(partyDtos);
		}
	}

}
