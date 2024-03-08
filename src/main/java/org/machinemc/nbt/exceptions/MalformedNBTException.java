package org.machinemc.nbt.exceptions;

import org.machinemc.nbt.parser.StringReader;

public class MalformedNBTException extends NBTException {

    public static final DynamicMalformedNBTExceptionType ARRAY_INVALID = new DynamicMalformedNBTExceptionType(1, args ->
            "Invalid array type '" + args[0] + "'");
    public static final DynamicMalformedNBTExceptionType ARRAY_MIXED = new DynamicMalformedNBTExceptionType(2, args ->
            "Can't insert " + args[0] + " into " + args[1]);
    public static final MalformedNBTExceptionType EXPECTED_KEY = new MalformedNBTExceptionType("Expected key");
    public static final MalformedNBTExceptionType EXPECTED_VALUE = new MalformedNBTExceptionType("Expected value");
    public static final DynamicMalformedNBTExceptionType LIST_MIXED = new DynamicMalformedNBTExceptionType(2, args ->
            "Can't insert " + args[0] + " into list of " + args[1]);
    public static final MalformedNBTExceptionType EXPECTED_END_OF_QUOTE = new MalformedNBTExceptionType("Unclosed quoted string");
    public static final DynamicMalformedNBTExceptionType INVALID_ESCAPE = new DynamicMalformedNBTExceptionType(1, args ->
            "Invalid escape sequence '" + args[0] + "' in quoted string");
    public static final DynamicMalformedNBTExceptionType EXPECTED_SYMBOL = new DynamicMalformedNBTExceptionType(1, args ->
            "Expected '" + args[0] + "'");

    public static boolean ENABLE_STACK_TRACES = true;
    public static int SUBPART_SHOWN = 10;

    private final String message;
    private final String input;
    private final int cursor;

    public MalformedNBTException(String message) {
        this(message, null, -1);
    }

    public MalformedNBTException(String message, StringReader reader) {
        this(message, reader.getInput(), reader.getCursor());
    }

    public MalformedNBTException(String message, String input, int cursor) {
        super(message, null, ENABLE_STACK_TRACES, ENABLE_STACK_TRACES);
        this.message = message;
        this.input = input;
        this.cursor = cursor;
    }

    @Override
    public String getMessage() {
        String message = getRawMessage();
        String context = getContext();
        if (context != null)
            message += " at position " + cursor + ": " + context;
        return message;
    }

    public String getContext() {
        if (input == null || cursor < 0) return null;
        StringBuilder builder = new StringBuilder();
        int cursor = Math.min(input.length(), this.cursor);
        if (cursor > SUBPART_SHOWN) builder.append("...");

        builder.append(input, Math.max(0, cursor - SUBPART_SHOWN), cursor);
        builder.append("<--[HERE]");
        return builder.toString();
    }

    public String getRawMessage() {
        return message;
    }

    public String getInput() {
        return input;
    }

    public int getCursor() {
        return cursor;
    }

}
