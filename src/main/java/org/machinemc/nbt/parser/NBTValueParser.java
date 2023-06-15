package org.machinemc.nbt.parser;

import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTByte;
import org.machinemc.nbt.exceptions.MalformedNBTException;

class NBTValueParser implements NBTElementParser<NBT> {

    @Override
    public NBT parse(StringReader reader) throws MalformedNBTException {
        reader.skipWhitespace();
        char current = reader.peek();
        NBT nbt = null;
        switch (current) {
            case '{' -> {
                String compound = reader.getInput().substring(reader.getCursor());
                compound = compound.substring(0, findClosingBrace(compound) + 1);
                NBTParser parser = new NBTParser(compound);
                nbt = parser.parse();
                reader.setCursor(reader.getCursor() + parser.getReader().getCursor());
                return nbt;
            }
            case '\'', '"' -> {
                return StringParser.quoted().parse(reader);
            }
            case '[' -> {
                if (reader.peek(2) == ';')
                    return new ArrayParser().parse(reader);
                return new ListParser().parse(reader);
            }
        }

        int start = reader.getCursor();
        if (Character.isDigit(current) || current == '.' || current == '-') {
            try {
                nbt = new NumberParser().parse(reader);
            } catch (Exception e) {
                reader.setCursor(start);
            }
        }

        if (nbt == null) {
            nbt = StringParser.unquoted().parse(reader);
            switch ((String) nbt.value()) {
                case "true":
                    return new NBTByte(1);
                case "false":
                    return new NBTByte(0);
            }
        }

        return nbt;
    }

    private static int findClosingBrace(String string) {
        if (string.charAt(0) != '{')
            return -1;

        char[] chars = string.toCharArray();
        int level = 0;
        for (int i = 0; i < chars.length; i++) {
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

}
