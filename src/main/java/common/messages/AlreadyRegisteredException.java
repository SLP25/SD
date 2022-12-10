package common.messages;

/**
 * An exception indicating the given Message subclass has already
 * registered with its superclass or the given id has already been takem.
 *
 * Thrown when attempting to register the same subclass twice, or when registering
 * two different classes with the same id
 *
 * @see Message for more details on how class registration works
 */
public class AlreadyRegisteredException extends RuntimeException{
    public AlreadyRegisteredException(String message) {
        super(message);
    }
}
