package org.machinemc.nbt;

import org.junit.Test;
import org.machinemc.nbt.io.NBTReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class NBTTest {

    @Test
    public void iteration() {
        final NBTIntArray array = new NBTIntArray(10, 14, 2);
        assert array.size() == 3;
        final int[] integers = array.revert();
        assert integers.length == 3;
        for (Integer integer : array) assert integer > 1 && integer < 15;
        int count = 0;
        for (Integer integer : array) count += integer;
        assert count == 26;
    }

    @Test
    public void bytes() throws IOException {
        final NBTCompound compound = new NBTCompound();
        compound.set("first", (byte) 10);
        compound.set("test", (byte) -10);
        assert compound.getValue("first") instanceof Byte;
        assert compound.getValue("test") instanceof Byte;
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        compound.write(stream);
        final byte[] bytes = stream.toByteArray();
        final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        final NBTCompound read = NBTReader.readCompound(input);
        assert read.containsKey("first");
        assert read.containsKey("test");
        assert read.getValue("first") instanceof Byte;
        assert read.getValue("test") instanceof Byte;
        assert read.getValue("first").equals((byte) 10) : read.getValue("first");
        assert read.getValue("test").equals((byte) -10) : read.getValue("test");
    }

    @Test
    public void basic() {
        final NBTCompound compound = new NBTCompound();
        compound.set("hello", "there");
        compound.set("test", 10);
        compound.set("thing", -5.2);
        compound.set("ints", new int[]{1, 2, 3});
        assert compound.size() == 4;
        assert compound.getValue("hello").equals("there");
        assert compound.getValue("test").equals(10);
        assert compound.getValue("thing").equals(-5.2);
        assert Arrays.equals(compound.getValue("ints"), new int[]{1, 2, 3});
    }

    @Test
    public void list() throws IOException {
        final NBTCompound compound = new NBTCompound();
        final NBTList list = new NBTList();
        compound.set("list", list);
        list.addValue(10);
        list.addValue(new NBTInt(3));
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        compound.write(stream);
        final byte[] bytes = stream.toByteArray();
        NBTCompound second = NBTReader.readCompound(new ByteArrayInputStream(bytes));
        assert second.size() == 1;
        assert second.<NBTList>get("list").size() == 2;
        assert second.<NBTList>get("list").get(0).revert().equals(10);
        assert second.<NBTList>get("list").get(1).revert().equals(3);
    }

    @Test
    public void stream() throws IOException {
        final NBTCompound first = new NBTCompound();
        NBTCompound second = new NBTCompound();
        first.set("hello", "there");
        first.set("test", 10);
        first.set("thing", -5.2);
        first.set("ints", new int[]{1, 2, 3});
        assert first.size() == 4;
        assert second.isEmpty();
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            first.write(stream);
            final byte[] bytes = stream.toByteArray();
            second = NBTReader.readCompound(new ByteArrayInputStream(bytes));
        }
        assert second.size() == 4;
        assert second.getValue("hello").equals("there");
        assert second.getValue("test").equals(10);
        assert second.getValue("thing").equals(-5.2);
        assert Arrays.equals(second.getValue("ints"), new int[]{1, 2, 3});
        second.clear();
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            first.writeRoot(stream);
            final byte[] bytes = stream.toByteArray();
            second = NBTReader.readRootCompound(new ByteArrayInputStream(bytes));
        }
        assert second.size() == 4;
        assert second.getValue("hello").equals("there");
        assert second.getValue("test").equals(10);
        assert second.getValue("thing").equals(-5.2);
        assert Arrays.equals(second.getValue("ints"), new int[]{1, 2, 3});
    }

    @Test
    public void binary() throws IOException {
        final NBTCompound compound = new NBTCompound();
        compound.set("test", 10);
        assert compound.size() == 1;
        final byte[] number = this.bytes(compound);
        assert number[0] == NBT.Tag.INT.ordinal();
        assert number[number.length - 2] == 10;
        assert number[number.length - 1] == NBT.Tag.END.ordinal();
        compound.set("test", (byte) 10);
        assert compound.size() == 1;
        final byte[] bytes = this.bytes(compound);
        assert bytes[0] == NBT.Tag.BYTE.ordinal();
        assert bytes[bytes.length - 2] == 10;
        assert bytes[bytes.length - 1] == NBT.Tag.END.ordinal();
    }

    private byte[] bytes(NBTCompound compound) throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        compound.write(stream);
        return stream.toByteArray();
    }

    @Test
    public void customTypes() {
        final NBTCompound compound = new NBTCompound();
        compound.set("hello", "there");
        assert compound.containsKey("hello");
        assert compound.getValue("hello").equals("there");
        compound.set("hello", this::insert, "there");
        assert compound.containsKey("hello");
        assert !compound.getValue("hello").equals("there");
        assert compound.getValue("hello") instanceof Map<?, ?>;
        assert compound.get("hello", this::extract, "beans").equals("there");
        assert compound.get("beans", this::extract, "beans").equals("beans");
        compound.remove("hello");
        assert compound.get("hello", this::extract, "beans").equals("beans");
        assert compound.get("hello", (NBT.Extractor<String>) this::extract) == null;
    }

    @Test
    public void customLists() {
        final NBTCompound compound = new NBTCompound();
        compound.set("hello", "there");
        assert compound.containsKey("hello");
        compound.setList("hello", this::insert, (String[]) null);
        assert !compound.containsKey("hello");
        compound.setList("hello", this::insert, List.of("there", "general", "kenobi"));
        assert compound.containsKey("hello");
        assert compound.<NBTList>get("hello").size() == 3;
        compound.setList("hello", this::insert, "there", "general", "kenobi");
        assert compound.containsKey("hello");
        assert compound.<NBTList>get("hello").size() == 3;
        final List<String> list = compound.getList("hello", this::extract);
        assert list != null;
        assert list.size() == 3;
        assert list.equals(List.of("there", "general", "kenobi"));
    }

    @Test
    public void stringTest() {
        final NBTCompound first = new NBTCompound();
        first.set("hello", "there");
        first.set("test", 10);
        first.set("thing", -5.2);
        first.set("ints", new int[]{1, 2, 3});

        final NBTCompound second = new NBTCompound();
        second.set("thing", -5.2);
        second.set("ints", new int[]{1, 2, 3});
        second.set("hello", "there");
        second.set("test", 10);

        assert first.toString().equals(second.toString());
    }

    @Test
    public void equalsTest() {
        final NBTCompound first = new NBTCompound();
        first.set("hello", "there");
        first.set("test", 10);
        first.set("thing", -5.2);
        first.set("ints", new int[]{1, 2, 3});

        final NBTCompound second = new NBTCompound();
        second.set("thing", -5.2);
        second.set("ints", new int[]{1, 2, 3});
        second.set("hello", "there");
        second.set("test", 10);

        assert first.equals(second);
    }

    private void insert(NBTCompound compound, String string) {
        compound.set("value", string);
    }

    private String extract(NBTCompound compound) {
        return compound.getValue("value");
    }

}
