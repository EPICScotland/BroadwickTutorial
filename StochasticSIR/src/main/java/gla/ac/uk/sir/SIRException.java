package gla.ac.uk.sir;

/**
 * A RuntimeException for the SIR stochastic project.
 */
@SuppressWarnings("serial")
public class SIRException extends RuntimeException {

    /**
     * Constructs a new SIR exception with null as its detail message.
     */
    public SIRException() {
        super();
    }

    /**
     * Constructs a new SIR exception with the specified detail message.
     * @param msg the detail message. The detail message is saved for later
     * retrieval by the Throwable.getMessage() method.
     */
    public SIRException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new SIR exception with the specified throwable object.
     * @param cause the Throwable object.
     */
    public SIRException(final Throwable cause) {
        super(cause);
    }
}
