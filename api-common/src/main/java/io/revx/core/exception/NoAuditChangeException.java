package io.revx.core.exception;

/**
 * This Exception is thrown if nothing has changed in entity 
 * or the audit service is not tracking the change currently
 * @author tchauhan
 *
 */
public class NoAuditChangeException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoAuditChangeException() {
        super();
    }

    public NoAuditChangeException(String msg) {
        super(msg);
    }

}
