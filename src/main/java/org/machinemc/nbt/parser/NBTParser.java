package org.machinemc.nbt.parser;

import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.exceptions.MalformedNBTException;

public class NBTParser {

    private final StringReader reader;
    private NBTCompound nbtCompound = new NBTCompound();

    public NBTParser(String input) {
        this(new StringReader(input));
    }

    NBTParser(StringReader reader) {
        this.reader = reader;
    }

    public NBTCompound parse() throws MalformedNBTException {
        reader.reset();
        reader.eat('{');
        nbtCompound = reader.peek() == '}' ? new NBTCompound() : parseKeyValue(new NBTCompound());
        reader.eat('}');
        if (reader.canRead())
            throw MalformedNBTException.unexpectedTrailingCharacter(reader.getCursor() + 1);
        return nbtCompound;
    }

    private NBTCompound parseKeyValue(NBTCompound nbtCompound) throws MalformedNBTException {
        String key = key();
        reader.skipWhitespace();
        reader.eat(':');
        NBT value = new NBTValueParser().parse(reader);
        nbtCompound.set(key, value);
        reader.skipWhitespace();
        if (reader.canRead() && reader.peek() == ',') {
            reader.read();
            parseKeyValue(nbtCompound);
        }
        return nbtCompound;
    }

    private String key() {
        reader.skipWhitespace();
        if (reader.peek() == '"' || reader.peek() == '\'')
            return StringParser.quoted().parse(reader).revert();
        return StringParser.unquoted().parse(reader).revert();
    }

    public StringReader getReader() {
        return reader;
    }

    public NBTCompound getParsedCompound() {
        return nbtCompound;
    }

    public static NBTCompound parse(String input) {
        return new NBTParser(input).parse();
    }

}
