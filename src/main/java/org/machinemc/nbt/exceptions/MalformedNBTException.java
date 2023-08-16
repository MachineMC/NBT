package org.machinemc.nbt.exceptions;

public class MalformedNBTException extends NBTException {

    public MalformedNBTException(String message, int pos) {
        super(String.format("Malformed NBT data: %s at pos %d", message, pos));
    }

    public static MalformedNBTException unexpectedArrayType(char dataClass, int pos) {
        return new MalformedNBTException("Unexpected array type '" + dataClass + '\'', pos);
    }

    public static MalformedNBTException valueDoesNotMatchArrayType(Class<?> arrayType, int pos) {
        return new MalformedNBTException("Unable to parse array. Value is not of expected type " + arrayType.getName() + ".", pos);
    }

    public static MalformedNBTException unexpectedTrailingCharacter(int pos) {
        return new MalformedNBTException("An unexpected character was found after the end of the NBT data.", pos);
    }

    public static MalformedNBTException multipleDecimaledNumber(String number, int pos) {
        return new MalformedNBTException("The number contains multiple decimal points: " + number, pos);
    }

    public static MalformedNBTException unterminatedQuote(int pos) {
        return new MalformedNBTException("Unterminated quoted literal", pos);
    }

    public static MalformedNBTException emptyUnquotedString(int pos) {
        return new MalformedNBTException("Unable to parse unquoted string. Value is empty.", pos);
    }

    public static MalformedNBTException expected(char expected, char actual, int pos) {
        return new MalformedNBTException(String.format("Expected character '%s' but found '%s'", expected, actual), pos);
    }

    public static MalformedNBTException endedUnexpectedly(int pos) {
        return new MalformedNBTException("The NBT data ended unexpectedly. A compound must be closed by a '}' character.", pos);
    }

}
