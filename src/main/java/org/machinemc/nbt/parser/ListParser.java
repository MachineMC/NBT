package org.machinemc.nbt.parser;

import org.machinemc.nbt.NBTList;
import org.machinemc.nbt.exceptions.MalformedNBTException;

class ListParser implements NBTElementParser<NBTList> {

    @Override
    public NBTList parse(StringReader reader) throws MalformedNBTException {
        reader.eat('[');
        NBTList list = new NBTList();
        boolean loop;
        do {
            reader.skipWhitespace();
            if (reader.peek() == ']')
                break;
            list.add(new NBTValueParser().parse(reader));
            loop = false;
            reader.skipWhitespace();
            if (reader.peek() == ',') {
                loop = true;
                reader.next();
            }
        } while (loop);
        reader.skipWhitespace();
        reader.eat(']');
        return list;
    }

}
