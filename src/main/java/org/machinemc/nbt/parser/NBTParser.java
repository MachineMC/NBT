package org.machinemc.nbt.parser;

import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.exceptions.MalformedNBTException;

public class NBTParser {

    private final StringReader reader;
    private NBTCompound nbtCompound = new NBTCompound();

    public NBTParser(String input) {
        this.reader = new StringReader(input);
    }

    public NBTCompound parse() throws MalformedNBTException {
        reader.reset();
        MalformedNBTException exception = null;
        try {
            reader.eat('{');
            if (reader.peek() != '}')
                nbtCompound = parseCompound(new NBTCompound());
            if (!reader.canRead()) {
                exception = new MalformedNBTException("The NBT data ended unexpectedly. A compound must be closed by a '}' character.", reader.getCursor());
            } else if (reader.canRead(2)) {
                exception = new MalformedNBTException("An unexpected character was found after the end of the NBT data.", reader.getCursor());
            }
            reader.eat('}');
        } catch (StringIndexOutOfBoundsException e) {
            exception = new MalformedNBTException("The NBT data ended unexpectedly. A compound must be closed by a '}' character.", reader.getCursor());
        } catch (MalformedNBTException e) {
            exception = e;
        }

        if (exception != null)
            throw new MalformedNBTException("The NBT data provided is invalid and cannot be parsed.", exception, reader.getCursor());
        return nbtCompound;
    }

    private NBTCompound parseCompound(NBTCompound nbtCompound) throws MalformedNBTException {
        String key = key();
        reader.skipWhitespace();
        reader.eat(':');
        NBT value = new NBTValueParser().parse(reader);
        nbtCompound.set(key, value);
        reader.skipWhitespace();
        if (reader.canRead() && reader.peek() == ',') {
            reader.read();
            parseCompound(nbtCompound);
        }
        return nbtCompound;
    }

    private String key() {
        reader.skipWhitespace();
        if (reader.peek() == '"' || reader.peek() == '\'')
            return StringParser.quoted().parse(reader).value();
        return StringParser.unquoted().parse(reader).value();
    }

    public StringReader getReader() {
        return reader;
    }

    public NBTCompound getParsedCompound() {
        return nbtCompound;
    }

}
