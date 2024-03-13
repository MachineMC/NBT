package org.machinemc.nbt.parser;

import org.machinemc.nbt.exceptions.MalformedNBTException;

import java.util.function.Predicate;

public class StringReader implements Cloneable {

    private final String input;
    private final int start, end;
    private int cursor = 0;

    public StringReader(String input) {
        this(input, 0, input.length());
    }

    private StringReader(String input, int start, int end) {
        this.input = input;
        this.start = start;
        this.end = end;
    }

    public String readUntil(char terminator) {
        return readUntil(c -> c == terminator);
    }

    public String readUntil(Predicate<Character> predicate) {
        StringBuilder result = new StringBuilder();
        while (canRead()) {
            if (predicate.test(peek()))
                return result.toString();
            result.append(read());
        }
        return "";
    }

    public String finish() {
        String string = input.substring(cursor, end);
        cursor = end;
        return string;
    }

    public boolean canRead() {
        return canRead(1);
    }

    public boolean canRead(int length) {
        return cursor + length <= end;
    }

    public char peek() {
        return peek(0);
    }

    public char peek(int offset) {
        int cursor = this.cursor + offset;
        return input.charAt(cursor);
    }

    public char read() {
        return input.charAt(cursor++);
    }

    public void skip() {
        cursor++;
    }

    public void skipWhitespace() {
        readUntil(c -> !Character.isWhitespace(c));
    }

    public void eat(char expected) {
        if (!eatSafely(expected))
            throw MalformedNBTException.EXPECTED_SYMBOL.createWithContext(this, expected);
    }

    public boolean eatSafely(char expected) {
        skipWhitespace();
        if (!canRead())
            return false;
        char found = peek();
        if (found == expected) {
            read();
            return true;
        }
        return false;
    }

    public void reset() {
        cursor = start;
    }

    public int getRemaining() {
        return end - cursor;
    }

    public String getInput() {
        return input;
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public StringReader substring(int start, int end) {
        return new StringReader(input, start, end);
    }

    @Override
    public StringReader clone() {
        try {
            return (StringReader) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
