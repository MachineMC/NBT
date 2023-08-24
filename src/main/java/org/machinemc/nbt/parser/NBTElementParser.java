package org.machinemc.nbt.parser;

import org.machinemc.nbt.NBT;
import org.machinemc.nbt.exceptions.MalformedNBTException;

interface NBTElementParser<T extends NBT> {

    T parse(StringReader reader) throws MalformedNBTException;

}
