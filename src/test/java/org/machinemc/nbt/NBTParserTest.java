package org.machinemc.nbt;

import org.junit.Test;
import org.machinemc.nbt.parser.NBTParser;

import java.util.*;

public class NBTParserTest {

    @Test
    public void basic() {
        final NBTCompound compound = new NBTCompound();
        compound.set("Foo", new NBTByte(10));
        compound.set("Bar", new NBTString("Boo"));
        final NBTCompound parsed = new NBTParser(compound.toString()).parse();

        assert compound.getNBT("Foo").equals(parsed.getNBT("Foo"));
        assert compound.getNBT("Bar").equals(parsed.getNBT("Bar"));
    }

    @Test
    public void list() {
        final NBTCompound compound = new NBTCompound();
        final NBTList list = new NBTList();
        compound.set("list", list);
        list.addValue(10);
        list.addValue(3);
        list.addValue(5);
        final NBTCompound parsed = new NBTParser(compound.toString()).parse();
        assert parsed.getNBT("list") != null;
        List<Object> parsedList = parsed.getValue("list");
        assert parsedList.get(0).equals(10);
        assert parsedList.get(1).equals(3);
        assert parsedList.get(2).equals(5);
    }

    @Test
    public void longCompound() {
        final NBTCompound compound = new NBTCompound();
        Random random = new Random();
        for (int i = 0; i < 200; i++)
            compound.set(String.valueOf(random.nextInt()), new NBTString(UUID.randomUUID()));
        final NBTCompound parsed = new NBTParser(compound.toString()).parse();

        assert compound.size() == parsed.size();

        for (Map.Entry<String, NBT<?>> entry : compound) {
            assert parsed.getNBT(entry.getKey()) != null;
            assert NBT.revert(entry.getValue()).equals(parsed.getValue(entry.getKey()));
        }
    }

    @Test
    public void arrayTypes() {
        NBTLongArray longArray = new NBTLongArray(1, 2, 3, 4, 5);
        NBTByteArray byteArray = new NBTByteArray(2, 3, 4, 5, 6);
        NBTIntArray intArray = new NBTIntArray(3, 4, 5, 6, 7);

        final NBTCompound compound = new NBTCompound(Map.of(
                "long", longArray,
                "byte", byteArray,
                "int", intArray
        ));

        final NBTCompound parsed = new NBTParser(compound.toString()).parse();

        long[] parsedLong = parsed.getValue("long");
        byte[] parsedByte = parsed.getValue("byte");
        int[] parsedInt = parsed.getValue("int");

        assert parsedLong.length == longArray.revert().length;
        for (int i = 0; i < parsedLong.length; i++)
            assert parsedLong[i] == longArray.revert()[i];

        assert parsedByte.length == byteArray.revert().length;
        for (int i = 0; i < parsedLong.length; i++)
            assert parsedByte[i] == byteArray.revert()[i];

        assert parsedInt.length == intArray.revert().length;
        for (int i = 0; i < parsedLong.length; i++)
            assert parsedInt[i] == intArray.revert()[i];

    }

}
