package org.machinemc.nbt.parser;

import org.machinemc.nbt.*;
import org.machinemc.nbt.NBT.Tag;
import org.machinemc.nbt.exceptions.MalformedNBTException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.*;

public class NBTParser {

    private static final Predicate<Character> numberPredicate = c -> '0' <= c && c <= '9' || c == '.' || c == '-';

    private final StringReader reader;
    private NBTCompound nbtCompound;

    public NBTParser(String input) {
        this(new StringReader(input.trim()));
    }

    public NBTParser(StringReader reader) {
        this.reader = reader;
    }

    public NBTCompound parse() throws MalformedNBTException {
        return nbtCompound = parseCompound();
    }

    private String parseKey() {
        reader.skipWhitespace();
        if (!reader.canRead())
            throw MalformedNBTException.EXPECTED_KEY.createWithContext(reader);
        return parseString().revert();
    }

    private NBT<?> parseValue() {
        reader.skipWhitespace();
        if (!reader.canRead())
            throw MalformedNBTException.EXPECTED_VALUE.createWithContext(reader);

        char c = reader.peek();
        return switch (c) {
            case '[' -> reader.canRead(3) && reader.peek(2) == ';' ? parseArray() : parseList();
            case '{' -> parseCompound();
            default -> parseSimpleValue();
        };
    }

    private NBT<?> parseSimpleValue() {
        char c = reader.peek();
        if (c == '\'' || c == '"') return parseQuotedString();

        int start = reader.getCursor();
        try {
            if (isNumberAllowed(c)) return parseNumber();
        } catch (NumberFormatException ignored) {
            reader.setCursor(start);
        }

        NBTString string = parseUnquotedString();
        if (string.revert().isEmpty())
            throw MalformedNBTException.EXPECTED_VALUE.createWithContext(reader);
        return switch (string.revert()) {
            case "true" -> new NBTByte(true);
            case "false" -> new NBTByte(false);
            default -> string;
        };
    }

    private NBTString parseString() {
        if (!reader.canRead())
            return new NBTString("");
        return switch (reader.peek()) {
            case '"', '\'' -> parseQuotedString();
            default -> parseUnquotedString();
        };
    }

    private NBTString parseQuotedString() {
        char quote = reader.read();
        StringBuilder stringBuilder = new StringBuilder();
        boolean escaped = false;
        while (reader.canRead()) {
            char current = reader.read();

            if (escaped && (current != '\\' || current != quote)) {
                reader.setCursor(reader.getCursor() - 1);
                throw MalformedNBTException.INVALID_ESCAPE.createWithContext(reader, String.valueOf(current));
            }

            if (current == '\\' && !escaped) {
                escaped = true;
                continue;
            }

            if (current == quote && !escaped)
                return new NBTString(stringBuilder.toString());

            escaped = false;
            stringBuilder.append(current);
        }
        throw MalformedNBTException.EXPECTED_END_OF_QUOTE.createWithContext(reader);
    }

    private NBTString parseUnquotedString() {
        String value = reader.readUntil(c -> !(Character.isDigit(c) || Character.isAlphabetic(c) || c == '_' || c == '-' || c == '.' || c == '+'));
        return new NBTString(value);
    }

    private NBT<?> parseNumber() {
        String numberString = reader.readUntil(numberPredicate.negate());

        boolean hasSuffix = true;
        Function<String, Number> parser = switch (reader.canRead() ? Character.toLowerCase(reader.peek()) : 0) {
            case 'b' -> Byte::parseByte;
            case 's' -> Short::parseShort;
            case 'l' -> Long::parseLong;
            case 'f' -> Float::parseFloat;
            case 'd' -> Double::parseDouble;
            default -> {
                hasSuffix = false;
                yield numberString.indexOf('.') == -1 ? Integer::parseInt : Double::parseDouble;
            }
        };

        if (reader.canRead() && hasSuffix)
            reader.skip();

        return NBT.convert(parser.apply(numberString));
    }

    private NBT<?> parseArray() {
        eat('[');
        char type = reader.peek();
        Tag arrayTag = switch (type) {
            case 'B' -> Tag.BYTE;
            case 'I' -> Tag.INT;
            case 'L' -> Tag.LONG;
            default -> throw MalformedNBTException.ARRAY_INVALID.createWithContext(reader, type);
        };
        reader.skip(); // Skip array type character
        eat(';');

        reader.skipWhitespace();
        List<Number> elements = new ArrayList<>();
        while (reader.canRead() && reader.peek() != ']') {
            int start = reader.getCursor();
            NBT<?> value = parseValue();
            if (!value.tag().equals(arrayTag)) {
                reader.setCursor(start);
                throw MalformedNBTException.ARRAY_MIXED.createWithContext(reader, value.tag(), arrayTag);
            }
            elements.add((Number) value.revert());

            if (!hasElementSeparator())
                break;
        }
        eat(']');

        return switch (arrayTag) {
            case BYTE -> createArray(elements, Number::byteValue, byte[]::new, NBTByteArray::new);
            case INT -> createArray(elements, Number::intValue, int[]::new, NBTIntArray::new);
            default -> createArray(elements, Number::longValue, long[]::new, NBTLongArray::new);
        };
    }

    private <T extends NBTArray<P, N>, N, P> T createArray(
            List<Number> elements,
            Function<Number, N> numberFunction,
            IntFunction<P> primitiveArrayCreator,
            Function<P, T> nbtArrayCreator
    ) {
        int length = elements.size();
        Iterator<Number> iterator = elements.iterator();
        P array = primitiveArrayCreator.apply(length);
        for (int i = 0; i < length; i++)
            Array.set(array, i, numberFunction.apply(iterator.next()));
        return nbtArrayCreator.apply(array);
    }

    private NBTList parseList() {
        return parseCollection('[', ']', NBTList::new, list -> {
            int start = reader.getCursor();
            NBT<?> value = parseValue();
            if (list.getElementType() != Tag.END && !value.tag().equals(list.getElementType())) {
                reader.setCursor(start);
                throw MalformedNBTException.LIST_MIXED.createWithContext(reader, value.tag(), list.getElementType());
            }
            list.add(value);
        });
    }

    private NBTCompound parseCompound() {
        return parseCollection('{', '}', NBTCompound::new, nbtCompound -> {
            String key = parseKey();
            eat(':');
            reader.skipWhitespace();
            NBT<?> value = parseValue();
            nbtCompound.set(key, value);
        });
    }

    private <T> T parseCollection(char opening, char closing, Supplier<T> collectionFactory, Consumer<T> elementReader) {
        eat(opening);
        reader.skipWhitespace();
        T collection = collectionFactory.get();
        while (reader.canRead() && reader.peek() != closing) {
            elementReader.accept(collection);
            if (!hasElementSeparator())
                break;
        }
        eat(closing);
        return collection;
    }

    private boolean hasElementSeparator() {
        reader.skipWhitespace();
        if (reader.eatSafely(',')) {
            reader.skipWhitespace();
            return true;
        }
        return false;
    }

    private void eat(char expected) {
        reader.skipWhitespace();
        reader.eat(expected);
    }

    public StringReader getReader() {
        return reader;
    }

    public NBTCompound getParsedCompound() {
        return nbtCompound;
    }

    private static boolean isNumberAllowed(char c) {
        return '0' <= c && c <= '9' || c == '.' || c == '-';
    }

    public static NBTCompound parse(String input) {
        return new NBTParser(input).parse();
    }

}
