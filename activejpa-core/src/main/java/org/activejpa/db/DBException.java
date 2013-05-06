/**
 * 
 */
package org.activejpa.db;

/**
 * @author ganeshs
 *
 */
public class DBException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DBException() {
	}

	/**
	 * @param message
	 */
	public DBException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DBException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

}
