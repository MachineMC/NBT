package org.machinemc.nbt.parser;

import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTByteArray;
import org.machinemc.nbt.NBTIntArray;
import org.machinemc.nbt.NBTLongArray;
import org.machinemc.nbt.exceptions.MalformedNBTException;

import java.util.ArrayList;
import java.util.List;

class ArrayParser implements NBTElementParser<NBT> {

    @Override
    public NBT parse(StringReader reader) throws MalformedNBTException {
        reader.eat('[');
        List<Number> elements = new ArrayList<>();
        char dataClass = reader.read();
        Class<? extends Number> arrayClass = switch (dataClass) {
            case 'B' -> Byte.class;
            case 'I' -> Integer.class;
            case 'L' -> Long.class;
            default -> throw new MalformedNBTException("Unexpected array type '" + dataClass + '\'', reader.getCursor());
        };
        reader.eat(';');
        boolean loop;
        do {
            reader.skipWhitespace();
            if (reader.peek() == ']')
                break;
            Number value = new NumberParser().parse(reader).value();

            if (!arrayClass.isInstance(value))
                throw new MalformedNBTException("Unable to parse array. Value is not of expected type " + arrayClass.getName() + ".", reader.getCursor());

            elements.add(value);
            loop = false;
            reader.skipWhitespace();
            if (reader.peek() == ',') {
                loop = true;
                reader.next();
            }
        } while (loop);
        reader.skipWhitespace();
        reader.eat(']');

        Number[] array = elements.toArray(new Number[0]);
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
        }

        long[] longs = new long[array.length];
        for (int i = 0; i < array.length; i++)
            longs[i] = (long) array[i];

        return new NBTLongArray(longs);
    }

}
