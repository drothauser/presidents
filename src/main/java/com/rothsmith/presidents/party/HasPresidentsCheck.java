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
import com.rothsmith.presidents.president.PresidentDto;

/**
 * Checks whether the party being validated is associated with one or more
 * presidents.
 *
 * @author drothauser
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { HasPresidentsCheck.Validator.class })
@Documented
public @interface HasPresidentsCheck {

	/**
	 * @return the default error message.
	 */
	String message() default "{party.has.presidents}";

	/**
	 * @return validation classes
	 */
	Class<?>[] groups() default {};

	/**
	 * @return error metadata
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * Validates whether a party id is associated with one or more presidents.
	 * 
	 * @author drothauser
	 *
	 */
	class Validator
	        implements ConstraintValidator<HasPresidentsCheck, Integer> {

		/**
		 * DAO for the PRESIDENT table. See {@link DbUtilsJdbcDao}.
		 */
		private DbUtilsJdbcDao<PresidentDto, PresidentDto> presidentDao;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void initialize(final HasPresidentsCheck hasPresidentsCheck) {
			presidentDao = new DbUtilsJdbcDao<PresidentDto, PresidentDto>(
			    "/presidentdao.properties");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isValid(final Integer id,
		    final ConstraintValidatorContext constraintValidatorContext) {

			PresidentDto params = new PresidentDto();
			params.setPartyId(id);

			List<PresidentDto> presidentDtos =
			    presidentDao.selectByStatement("query.selectByParty", params);

			return CollectionUtils.isEmpty(presidentDtos);

		}
	}

}
