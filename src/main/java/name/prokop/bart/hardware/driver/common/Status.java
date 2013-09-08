/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.common;

/**
 * Handy class for returning status of certain operation
 * @author bart
 */
public class Status {

    private final boolean successful;
    private final String description;
    private final Throwable throwable;
    private static final Status OK = new Status(true);

    public static Status ok() {
        return OK;
    }
    
    public static Status fail(String description) {
        return new Status(false, description);
    }

    public Status(boolean successful) {
        this.successful = successful;
        this.description = "";
        this.throwable = null;
    }

    public Status(boolean successful, String description) {
        this.successful = successful;
        this.description = description;
        this.throwable = null;
    }

    public Status(Throwable throwable) {
        this.successful = false;
        this.description = throwable.getMessage();
        this.throwable = throwable;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getDescription() {
        return description;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return successful ? "Success" : ("Failture " + description);
    }
}
