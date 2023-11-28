package org.machinemc.nbt.parser;

import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTByte;
import org.machinemc.nbt.exceptions.MalformedNBTException;

class NBTValueParser implements NBTElementParser<NBT<?>> {

    @Override
    public NBT<?> parse(StringReader reader) throws MalformedNBTException {
        reader.skipWhitespace();
        char current = reader.peek();
        NBT<?> nbt = null;
        switch (current) {
            case '{' -> {
                int start = reader.getCursor(), end = findClosingBrace(start, reader.getInput()) + 1;
                NBTParser parser = new NBTParser(reader.substring(start, end));
                nbt = parser.parse();
                reader.setCursor(parser.getReader().getCursor());
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
            switch ((String) nbt.revert()) {
                case "true":
                    return new NBTByte(true);
                case "false":
                    return new NBTByte(false);
            }
        }

        return nbt;
    }

    private static int findClosingBrace(int start, String string) {
        if (string.charAt(start) != '{')
            return -1;

        char[] chars = string.toCharArray();
        int level = 0;
        for (int i = start; i < chars.length; i++) {
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
