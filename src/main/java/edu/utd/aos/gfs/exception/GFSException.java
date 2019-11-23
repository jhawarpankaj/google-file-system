package edu.utd.aos.gfs.exception;

/**
 * Custom Exception thrown by the framework.
 * 
 * @author pankaj
 */
public class GFSException extends Exception{

	/**
	 * Default ID used in serialization.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Custom Exception thrown by the framework.
	 */
	public GFSException() {
		super();
	}
	
    /**
     * Custom exception thrown by the framework.
     *
     * @param message
     *            Description of the exception.
     */
	public GFSException(final String message) {
		super(message);
	}
	
	/**
     * Custom exception thrown by the framework.
     *
     * @param throwable
     *            Root cause of the exception.
     */
    public GFSException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Custom exception thrown by the framework.
     *
     * @param message
     *            Description of the exception.
     * @param throwable
     *            Root cause of the exception.
     */
    public GFSException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
