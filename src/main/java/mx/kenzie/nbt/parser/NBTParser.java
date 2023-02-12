package mx.kenzie.nbt.parser;

import mx.kenzie.nbt.*;
import mx.kenzie.nbt.exceptions.MalformedNBTException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class NBTParser {

    private final String input;
    private final NBTCompound nbtCompound;
    private int cursor;
    private int mark = 0;

    public NBTParser(String input) {
        this.input = input;
        this.nbtCompound = new NBTCompound();
    }

    public NBTCompound parse() {
        return handleCompound();
    }

    private NBTCompound handleCompound() {
        eat('{');
        do {
            if (!hasNext())
                throw new MalformedNBTException();

            char current = step();
            if (current == '}')
                return nbtCompound;

            String key = key();
            eat(':');
            nbtCompound.set(key, value());
            skipWhitespace();
        } while (current() != '}' && next() == ',');
        if (hasNext())
            throw new MalformedNBTException();
        return nbtCompound;
    }

    private String key() {
        if (current() == '"' || current() == '\'')
            return handleQuotedString().value();
        return handleUnquotedString().value();
    }

    private NBT value() {
        char current = step();

        NBT nbt = null;
        switch (current) {
            case '{' -> {
                String compound = input.substring(cursor - 1);
                compound = compound.substring(0, findClosingBrace(compound, 0) + 1);
                NBTParser parser = new NBTParser(compound);
                nbt = parser.handleCompound();
                cursor += parser.cursor;
            }
            case '\'', '"' -> {
                nbt =  handleQuotedString();
            }
            case '[' -> {
                if (peek(1) == ';') {
                    Class<?> arrayClass = switch (peek()) {
                        case 'B' -> Byte.class;
                        case 'I' -> Integer.class;
                        case 'L' -> Long.class;
                        default -> null;
                    };
                    if (arrayClass != null) {
                        nbt =  handleArray(arrayClass);
                        break;
                    }
                }
                nbt =  handleList();
            }
        }

        if (nbt != null)
            return nbt;

        mark();
        if (Character.isDigit(current) || current == '.' || current == '-') {
            try {
                nbt = handleNumber();
            } catch (Exception e) {
                reset();
            }
        }

        if (nbt == null)
            nbt = handleUnquotedString();

        if (nbt != null)
            return nbt;

        throw new MalformedNBTException(String.format("Unexpected character '%s'", current()));
    }

    private int findClosingBrace(String string, int index) {
        if (string.charAt(index) != '{')
            return -1;

        char[] chars = string.toCharArray();
        int level = 0;
        for (int i = index; i < chars.length; i++) {
            if (chars[i] == '{') {
                level++;
            } else if (chars[i] == '}') {
                level--;
                if (level == 0)
                    return i;
            }
        }

        return -1;
    }

    private NBTString handleQuotedString() {
        char quote = current();
        StringBuilder stringBuilder = new StringBuilder();
        boolean escaped = false;
        while (hasNext()) {
            char current = next();
            if (current == '\\' && !escaped) {
                escaped = true;
                continue;
            }

            if (current == quote && !escaped)
                return new NBTString(stringBuilder.toString());

            escaped = false;
            stringBuilder.append(current);
        }
        throw new MalformedNBTException("Unterminated quoted literal");
    }

    private NBTString handleUnquotedString() {
        return new NBTString(readUntil(c -> !(Character.isDigit(c) || Character.isAlphabetic(c) || c == '_' || c == '-' || c == '.' || c == '+')));
    }

    private NBT handleArray(Class<?> arrayClass) {
        List<Object> elements = new ArrayList<>();
        next(); // skip type
        eat(';');
        boolean loop;
        do {
            Object value = value().value();

            if (!arrayClass.isInstance(value))
                throw new MalformedNBTException("'" + value + "' is not of type '" + arrayClass.getSimpleName() + '\'');

            elements.add(value);
            loop = false;
            skipWhitespace();
            if (peek() == ',') {
                loop = true;
                step();
            }
        } while (loop);
        skipWhitespace();
        eat(']');

        Object[] array = elements.toArray(new Object[0]);
        if (arrayClass == Byte.class) {
            byte[] bytes = new byte[array.length];
            for (int i = 0; i < array.length; i++)
                bytes[i] = (byte) array[i];

            return new NBTByteArray(bytes);
        } else if (arrayClass == Integer.class) {
            int[] ints = new int[array.length];
            for (int i = 0; i < array.length; i++)
                ints[i] = (int) array[i];

            return new NBTIntArray(ints);
        } else if (arrayClass == Long.class) {
            long[] longs = new long[array.length];
            for (int i = 0; i < array.length; i++)
                longs[i] = (long) array[i];

            return new NBTLongArray(longs);
        }
        throw new MalformedNBTException("Unexpected array type '" + arrayClass + '\'');
    }

    private NBTList handleList() {
        NBTList list = new NBTList();
        boolean loop;
        do {
            list.add(value());
            loop = false;
            skipWhitespace();
            if (peek() == ',') {
                loop = true;
                step();
            }
        } while (loop);
        skipWhitespace();
        eat(']');
        return list;
    }

    private NBT handleNumber() {
        char c = Character.toLowerCase(current());
        StringBuilder stringBuilder = new StringBuilder();
        Class<? extends Number> numberClass = Integer.class;
        boolean usedDecimal = false;
        boolean negate = c == '-';
        if (negate) {
            stringBuilder.append('-');
            c = Character.toLowerCase(next());
        }

        loop: while (true) {
            stringBuilder.append(c);

            switch (c) {
                case '.':
                    if (usedDecimal) {
                        throw new MalformedNBTException();
                    } else {
                        usedDecimal = true;
                    }
                    numberClass = Double.class;
                    break;
                case 'b':
                case 's':
                case 'l':
                case 'f':
                case 'd':
                    numberClass = c == 'b' ? Byte.class : c == 's' ? Short.class : c == 'l' ? Long.class : c == 'f' ? Float.class : Double.class;
                    break loop;
                default:
                    if (Character.digit(c, 10) < 0) {
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        stepBack();
                        break loop;
                    }
                    break;
            }

            if (!hasNext())
                break;

            c = Character.toLowerCase(step());
        }

        String number = stringBuilder.toString();

        if (numberClass != Integer.class && numberClass != Double.class)
            number = number.substring(0, number.length() - 1);

        if (numberClass == Byte.class) {
            return new NBTByte(Byte.parseByte(number));
        } else if (numberClass == Short.class) {
            return new NBTShort(Short.parseShort(number));
        } else if (numberClass == Integer.class) {
            return new NBTInt(Integer.parseInt(number));
        } else if (numberClass == Long.class) {
            return new NBTLong(Long.parseLong(number));
        } else if (numberClass == Float.class) {
            return new NBTFloat(Float.parseFloat(number));
        } else {
            return new NBTDouble(Double.parseDouble(number));
        }
    }

    private String readUntil(Predicate<Character> predicate) {
        char current = current();
        StringBuilder builder = new StringBuilder();
        while (hasNext()) {
            builder.append(current);
            if (!hasNext() || predicate.test(current = peek()))
                break;
            next();
        }
        return builder.toString();
    }

    private char step() {
        skipWhitespace();
        return next();
    }

    private void skipWhitespace() {
        if (!hasNext())
            return;
        readUntil(character -> character != ' ');
    }

    private void stepBack() {
        cursor--;
    }

    private char next() {
        return input.charAt(cursor++);
    }

    private boolean hasNext() {
        return cursor < input.length();
    }

    private char peek() {
        return peek(0);
    }

    private char peek(int offset) {
        return input.charAt(cursor + offset);
    }

    private char current() {
        return input.charAt(cursor - 1);
    }

    private void eat(char c) {
        if (next() != c)
            throw new MalformedNBTException(String.format("Unexpected character '%s'", current()));
    }

    private void mark() {
        mark = cursor;
    }

    private void reset() {
        cursor = mark;
    }

}
