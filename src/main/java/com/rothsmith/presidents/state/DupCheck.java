/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.state;

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
@Constraint(validatedBy = { DupCheck.Validator.class })
@Documented
public @interface DupCheck {

	/**
	 * @return the default error message.
	 */
	String message() default "{state.exists}";

	/**
	 * @return validation classes
	 */
	Class<?>[] groups() default {};

	/**
	 * @return error metadata
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * Checks if a state already exists.
	 * 
	 * @author drothauser
	 *
	 */
	class Validator implements ConstraintValidator<DupCheck, StateDto> {

		/**
		 * DAO to maintain the PARTY table. See {@link DbUtilsJdbcDao}.
		 */
		private DbUtilsJdbcDao<StateDto, StateDto> stateDao;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void initialize(final DupCheck dupCheck) {
			this.stateDao =
			    new DbUtilsJdbcDao<StateDto, StateDto>("/statedao.properties");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isValid(final StateDto stateDto,
		    final ConstraintValidatorContext constraintValidatorContext) {

			List<StateDto> stateDtos =
			    stateDao.selectByStatement("query.constraintCheck", stateDto);
			return CollectionUtils.isEmpty(stateDtos);
		}
	}

}
