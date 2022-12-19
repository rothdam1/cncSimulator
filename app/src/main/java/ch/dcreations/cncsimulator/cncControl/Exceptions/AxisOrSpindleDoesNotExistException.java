package ch.dcreations.cncsimulator.cncControl.Exceptions;


/**
 * <p>
 * <p>
 * Exception if the NC Code haves a wrong Command
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
public class AxisOrSpindleDoesNotExistException extends Exception{

    public AxisOrSpindleDoesNotExistException(String message) {
        super(message);
    }
}
