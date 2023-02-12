package mx.kenzie.nbt.exceptions;

public class MalformedNBTException extends NBTException {

    public MalformedNBTException(String message, int pos) {
        super(String.format("Malformed NBT data: %s @ %d", message, pos));
    }

    public MalformedNBTException(String message, Throwable cause, int pos) {
        super(String.format("Malformed NBT data: %s @ %d", message, pos), cause);
    }

}
