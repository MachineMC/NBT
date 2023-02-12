package mx.kenzie.nbt.exceptions;

public class MalformedNBTException extends NBTException {

    public MalformedNBTException() {
    }

    public MalformedNBTException(String message) {
        super(message);
    }

    public MalformedNBTException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedNBTException(Throwable cause) {
        super(cause);
    }

}
