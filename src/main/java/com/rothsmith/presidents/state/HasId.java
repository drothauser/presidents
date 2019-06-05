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
 * Checks whether a given id exists in the STATE table.
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
	String message() default "{state.does.not.exist}";

	/**
	 * @return validation classes
	 */
	Class<?>[] groups() default {};

	/**
	 * @return error metadata
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * Validates the state's id.
	 * 
	 * @author drothauser
	 *
	 */
	class Validator implements ConstraintValidator<HasId, Integer> {

		/**
		 * DAO to maintain the STATE table. See {@link DbUtilsJdbcDao}.
		 */
		private DbUtilsJdbcDao<StateDto, StateDto> stateDao;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void initialize(final HasId hasId) {
			this.stateDao =
			    new DbUtilsJdbcDao<StateDto, StateDto>("/statedao.properties");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isValid(final Integer id,
		    final ConstraintValidatorContext constraintValidatorContext) {

			StateDto params = new StateDto();
			params.setId(id);
			List<StateDto> stateDtos = stateDao.select(params);
			return !CollectionUtils.isEmpty(stateDtos);
		}
	}

}
