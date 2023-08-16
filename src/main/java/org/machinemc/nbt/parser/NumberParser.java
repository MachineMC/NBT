package org.machinemc.nbt.parser;

import org.machinemc.nbt.*;
import org.machinemc.nbt.exceptions.MalformedNBTException;

class NumberParser implements NBTElementParser<NBT> {

    @Override
    public NBT parse(StringReader reader) throws MalformedNBTException {
        char c;
        StringBuilder result = new StringBuilder();
        int start = reader.getCursor(), decimals = 0;

        while (isNumberAllowed(c = reader.peek())) {
            if (c == '.')
                decimals++;
            result.append(reader.read());
        }
        String numberString = result.toString();
        if (decimals > 1)
            throw MalformedNBTException.multipleDecimaledNumber(numberString, start);

        NBT nbt = switch (Character.toLowerCase(reader.peek())) {
            case 'b' -> new NBTByte(Byte.parseByte(numberString));
            case 's' -> new NBTShort(Short.parseShort(numberString));
            case 'l' -> new NBTLong(Long.parseLong(numberString));
            case 'f' -> new NBTFloat(Float.parseFloat(numberString));
            case 'd' -> new NBTDouble(Double.parseDouble(numberString));
            default -> null;
        };

        if (nbt == null)
            return decimals == 1 ? new NBTDouble(Double.parseDouble(numberString)) : new NBTInt(Integer.parseInt(numberString));

        reader.read();
        return nbt;
    }

    private static boolean isNumberAllowed(char c) {
        return Character.isDigit(c) || c == '.' || c == '-';
    }

}
