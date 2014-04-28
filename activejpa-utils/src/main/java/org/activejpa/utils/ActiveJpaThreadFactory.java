/**
 * 
 */
package org.activejpa.utils;

import java.util.concurrent.ThreadFactory;

import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;

/**
 * Thread Factory implementation that ensures the jpa context is closed after the thread completes its run. 
 * This will ensure that jpa sessions are not leaked when the application is running in thread-context mode. 
 * 
 * @author ganeshs
 * 
 */
public class ActiveJpaThreadFactory implements ThreadFactory {

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
					e.printStackTrace();
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
