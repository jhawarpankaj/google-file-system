package edu.utd.aos.mutex.exception;

/**
 * Custom Exception thrown by the framework.
 * 
 * @author pankaj
 */
public class MutexException extends Exception{

	/**
	 * Default ID used in serialization.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Custom Exception thrown by the framework.
	 */
	public MutexException() {
		super();
	}
	
    /**
     * Custom exception thrown by the framework.
     *
     * @param message
     *            Description of the exception.
     */
	public MutexException(final String message) {
		super(message);
	}
	
	/**
     * Custom exception thrown by the framework.
     *
     * @param throwable
     *            Root cause of the exception.
     */
    public MutexException(final Throwable throwable) {
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
    public MutexException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
