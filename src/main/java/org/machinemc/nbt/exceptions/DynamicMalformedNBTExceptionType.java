package org.machinemc.nbt.exceptions;

import org.machinemc.nbt.parser.StringReader;

import java.util.function.Function;

public class DynamicMalformedNBTExceptionType {

    private final int args;
    private final Function<Object[], String> message;

    public DynamicMalformedNBTExceptionType(int args, Function<Object[], String> message) {
        this.args = args;
        this.message = message;
    }

    public MalformedNBTException create(Object... args) {
        checkArgLength(args);
        return new MalformedNBTException(message.apply(args));
    }

    public MalformedNBTException createWithContext(StringReader reader, Object... args) {
        checkArgLength(args);
        return new MalformedNBTException(message.apply(args), reader);
    }

    private void checkArgLength(Object... args) {
        if (args.length != this.args)
            throw new IllegalArgumentException("Expected " + this.args + " arguments, got " + args.length);
    }

}
