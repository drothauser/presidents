/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.president;

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
 * Checks whether a given PRESIDENT table ID exists.
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
	String message() default "{president.does.not.exist}";

	/**
	 * @return validation classes
	 */
	Class<?>[] groups() default {};

	/**
	 * @return error metadata
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * Validates the president's id.
	 * 
	 * @author drothauser
	 *
	 */
	class Validator implements ConstraintValidator<HasId, Integer> {

		/**
		 * DAO to maintain PRESIDENT table. See {@link DbUtilsJdbcDao}.
		 */
		private DbUtilsJdbcDao<PresidentDto, PresidentDto> presidentDao;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void initialize(final HasId hasId) {
			this.presidentDao = new DbUtilsJdbcDao<PresidentDto, PresidentDto>(
			    "/presidentdao.properties");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isValid(final Integer id,
		    final ConstraintValidatorContext constraintValidatorContext) {

			PresidentDto params = new PresidentDto();
			params.setId(id);
			List<PresidentDto> presidentDtos = presidentDao.select(params);
			return !CollectionUtils.isEmpty(presidentDtos);
		}
	}

}
