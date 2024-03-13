package org.machinemc.nbt.exceptions;

import org.machinemc.nbt.parser.StringReader;

public record MalformedNBTExceptionType(String message) {

    public MalformedNBTException create() {
        return new MalformedNBTException(message);
    }

    public MalformedNBTException createWithContext(StringReader reader) {
        return new MalformedNBTException(message, reader);
    }

    public MalformedNBTException createWithContext(String input, int position) {
        return new MalformedNBTException(message, input, position);
    }

    @Override
    public String toString() {
        return message;
    }

}
