/**
 * 
 */
package org.activejpa.utils;

import java.util.concurrent.ThreadFactory;

import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread Factory implementation that ensures the jpa context is closed after the thread completes its run. 
 * This will ensure that jpa sessions are not leaked when the application is running in thread-context mode. 
 * 
 * @author ganeshs
 * 
 */
public class ActiveJpaThreadFactory implements ThreadFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(ActiveJpaThreadFactory.class); 

	@Override
	public Thread newThread(Runnable runnable) {
		return new Thread(runnable) {
			@Override
			public void run() {
				JPAContext context = JPA.instance.getDefaultConfig().getContext();
				context.getEntityManager();
				try {
					super.run();
				} catch(Exception e) {
					logger.error("Failed while running the thread", e);
				} finally {
					if (context.isTxnOpen()) {
						context.closeTxn(true);
					}
					context.close();
				}
			}
		};
	}

}
