package org.machinemc.nbt.parser;

import org.machinemc.nbt.NBTString;
import org.machinemc.nbt.exceptions.MalformedNBTException;

class StringParser implements NBTElementParser<NBTString> {

    private static final StringParser QUOTED_STRING_PARSER = new StringParser(true);
    private static final StringParser UNQUOTED_STRING_PARSER = new StringParser(false);

    private final boolean quoted;

    private StringParser(boolean quoted) {
        this.quoted = quoted;
    }

    @Override
    public NBTString parse(StringReader reader) throws MalformedNBTException {
        if (quoted)
            return parseQuotedString(reader);
        return parseUnquotedString(reader);
    }

    private NBTString parseQuotedString(StringReader reader) {
        char quote = reader.next();
        StringBuilder stringBuilder = new StringBuilder();
        boolean escaped = false;
        while (reader.canRead()) {
            char current = reader.read();
            if (current == '\\' && !escaped) {
                escaped = true;
                continue;
            }

            if (current == quote && !escaped)
                return new NBTString(stringBuilder.toString());

            escaped = false;
            stringBuilder.append(current);
        }
        throw MalformedNBTException.unterminatedQuote(reader.getCursor());
    }

    private NBTString parseUnquotedString(StringReader reader) {
        String value = reader.readUntil(c -> !(Character.isDigit(c) || Character.isAlphabetic(c) || c == '_' || c == '-' || c == '.' || c == '+'));
        if (value.isEmpty())
            throw MalformedNBTException.emptyUnquotedString(reader.getCursor());
        return new NBTString(value);
    }

    public static StringParser quoted() {
        return QUOTED_STRING_PARSER;
    }

    public static StringParser unquoted() {
        return UNQUOTED_STRING_PARSER;
    }

}
