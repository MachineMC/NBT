package org.machinemc.nbt.parser;

import org.machinemc.nbt.*;
import org.machinemc.nbt.exceptions.MalformedNBTException;

class NumberParser implements NBTElementParser<NBT> {

    @Override
    public NBT parse(StringReader reader) throws MalformedNBTException {
        char c;
        StringBuilder result = new StringBuilder();
        boolean hasDecimal = false;

        while (isNumberAllowed(c = reader.peek())) {
            if (c == '.') {
                if (hasDecimal)
                    throw new MalformedNBTException("The number contains multiple decimal points: " + result, reader.getCursor());
                hasDecimal = true;
            }
            result.append(reader.read());
        }
        String numberString = result.toString();
        NBT nbt;

        nbt = switch (Character.toLowerCase(reader.peek())) {
            case 'b' -> new NBTByte(Byte.parseByte(numberString));
            case 's' -> new NBTShort(Short.parseShort(numberString));
            case 'l' -> new NBTLong(Long.parseLong(numberString));
            case 'f' -> new NBTFloat(Float.parseFloat(numberString));
            case 'd' -> new NBTDouble(Double.parseDouble(numberString));
            default -> null;
        };

        if (nbt == null)
            return hasDecimal ? new NBTDouble(Double.parseDouble(numberString))
                    : new NBTInt(Integer.parseInt(numberString));

        reader.read();
        return nbt;
    }

    private static boolean isNumberAllowed(char c) {
        return Character.isDigit(c) || c == '.' || c == '-';
    }

}
