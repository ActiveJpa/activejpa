/**
 * 
 */
package org.activejpa.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class OpenSessionInViewFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(OpenSessionInViewFilter.class);
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		JPAContext context = JPA.instance.getDefaultConfig().getContext();		
		context.getEntityManager();
		logger.debug("Initialized context and transaction");
		
		try {
			logger.debug("Executing ServletRequest");
			chain.doFilter(request, response);
			logger.debug("Done with execution of ServletRequest");
			
		} finally {
			logger.debug("Closing context and transaction");
			if (context.isTxnOpen()) {
				context.closeTxn(true);
			}
			context.close();
			logger.debug("Closed context and transaction");
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

}
