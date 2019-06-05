/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.presidents;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * 
 * This CORSResponseFilter always adds:
 * <ul>
 * <li>Access-Control-Allow-Origin header to the response(line 18). The “*”
 * means the request can come from any domain – this is the way to set this
 * header if you want to make your REST API public where everyone can access
 * it.</li>
 * <li>Access-Control-Allow-Methods header to the response (line 20), which
 * indicates that GET, POST, DELETE, PUT methods are allowed when accessing the
 * resource.</li>
 * <li>Access-Control-Allow-Headers header to the response (line 21), which
 * indicates that the X-Requested-With, Content-Type, X-Codingpedia headers can
 * be used when making the actual request.</li>
 * </ul>
 * 
 * @author drothauser
 *
 */
public class CORSResponseFilter implements ContainerResponseFilter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filter(ContainerRequestContext requestContext,
	    ContainerResponseContext responseContext) throws IOException {

		MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		headers.add("Access-Control-Allow-Headers",
		    "X-Requested-With, Content-Type, X-Codingpedia");
		responseContext.getHeaders().add("Access-Control-Allow-Methods",
		    "GET, POST, PUT, DELETE, OPTIONS, HEAD");

	}

}
