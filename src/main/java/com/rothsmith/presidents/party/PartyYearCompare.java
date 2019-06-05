/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents.party;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates if the party's end year (year party was dissolved) is great than or
 * equal to its founded year.
 *
 * @author drothauser
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { PartyYearCompare.Validator.class })
@Documented
public @interface PartyYearCompare {

	/**
	 * @return The field name in the bean of the founded year.
	 */
	String year1();

	/**
	 * @return The field name in the bean of the end year.
	 */
	String year2();

	/**
	 * @return the default error message.
	 */
	String message() default "{party.wrong.end.year}";

	/**
	 * @return validation classes
	 */
	Class<?>[] groups() default {};

	/**
	 * @return error metadata
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * Checks if the end year against the founded year per expected condition
	 * (e.g. greater than or equal).
	 * 
	 * @author drothauser
	 *
	 */
	class Validator implements ConstraintValidator<PartyYearCompare, Object> {

		/**
		 * SLF4J Logger for NumCompare.
		 */
		private static final Logger LOGGER =
		    LoggerFactory.getLogger(PartyYearCompare.class);

		/**
		 * Field name of the Founded Year.
		 */
		private String foundedYearField;

		/**
		 * Field name of the End Year.
		 */
		private String endYearField;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void initialize(final PartyYearCompare constraintAnnotation) {
			{
				foundedYearField = constraintAnnotation.year1();
				endYearField = constraintAnnotation.year2();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isValid(final Object object,
		    final ConstraintValidatorContext constraintValidatorContext) {

			boolean isValid = false;

			try {
				final Object foundedYearValue =
				    PropertyUtils.getProperty(object, foundedYearField);
				final Object endYearValue =
				    PropertyUtils.getProperty(object, endYearField);

				Integer foundedYear =
				    Integer.valueOf(Objects.toString(foundedYearValue,
				        Long.toString(Integer.MIN_VALUE)));

				Integer endYear = Integer.valueOf(Objects.toString(endYearValue,
				    Long.toString(Integer.MAX_VALUE)));

				// Some Jersey clients converts null to zero - be ready for
				// that:
				if (endYear.equals(0)) {
					endYear = Integer.MAX_VALUE;
				}

				isValid = foundedYear.compareTo(endYear) <= 0;

			} catch (IllegalAccessException | InvocationTargetException
			        | NoSuchMethodException e) {
				LOGGER.error("Could not obtain the value of the field(s): " + e,
				    e);
			}

			return isValid;
		}
	}

}
