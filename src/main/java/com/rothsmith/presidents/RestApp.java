/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents;

import javax.ws.rs.ApplicationPath;

import org.eclipse.persistence.jaxb.BeanValidationMode;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

/**
 * REST application to negate the need for web.xml servlet configuration.
 * 
 * @author drothauser
 *
 */
@ApplicationPath("/rest/*")
public class RestApp
        extends ResourceConfig {

	/**
	 * Default constructor that registers the packages that contain REST
	 * resources.
	 */
	public RestApp() {
		// packages("com.rothsmith.presidents");
		packages(RestApp.class.getPackage().getName());
		register(org.glassfish.jersey.logging.LoggingFeature.class);
		register(CORSResponseFilter.class);
		// register(ValidationConfigurationContextResolver.class);
		// register(ValidationExceptionMapper.class);
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		register(new MoxyJsonConfig().setFormattedOutput(true)
		    // Turn off BV otherwise the entities on server would be validated
		    // by MOXy as well.
		    .property(MarshallerProperties.BEAN_VALIDATION_MODE,
		        BeanValidationMode.NONE)
		    .resolver());
	}

}
