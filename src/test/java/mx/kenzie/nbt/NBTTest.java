package mx.kenzie.wellspring.nbt;

import mx.kenzie.wellspring.BasicTest;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NBTTest extends BasicTest {
    
    @Test
    public void basic() {
        final NBTCompound compound = new NBTCompound();
        compound.set("hello", "there");
        compound.set("test", 10);
        compound.set("thing", -5.2);
        compound.set("ints", 1, 2, 3);
        assert compound.size() == 4;
        assert compound.get("hello").equals("there");
        assert compound.get("test").equals(10);
        assert compound.get("thing").equals(-5.2);
        assert Arrays.equals(compound.get("ints"), new int[] {1, 2, 3});
    }
    
    @Test
    public void list() throws IOException {
        final NBTCompound compound = new NBTCompound(), second = new NBTCompound();
        final NBTList list = new NBTList();
        compound.set("list", list);
        list.addValue(10);
        list.addValue(new NBTInt(3));
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        compound.write(stream);
        final byte[] bytes = stream.toByteArray();
        second.read(new ByteArrayInputStream(bytes));
        assert second.size() == 1;
        assert second.<List<NBT>>get("list").size() == 2;
        assert second.<List<NBT>>get("list").get(0).value().equals(10);
        assert second.<List<NBT>>get("list").get(1).value().equals(3);
    }
    
    @Test
    public void stream() throws IOException {
        final NBTCompound first = new NBTCompound(), second = new NBTCompound();
        first.set("hello", "there");
        first.set("test", 10);
        first.set("thing", -5.2);
        first.set("ints", 1, 2, 3);
        assert first.size() == 4;
        assert second.size() == 0;
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            first.write(stream);
            final byte[] bytes = stream.toByteArray();
            second.read(new ByteArrayInputStream(bytes));
        }
        assert second.size() == 4;
        assert second.get("hello").equals("there");
        assert second.get("test").equals(10);
        assert second.get("thing").equals(-5.2);
        assert Arrays.equals(second.get("ints"), new int[] {1, 2, 3});
        second.clear();
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            first.writeAll(stream);
            final byte[] bytes = stream.toByteArray();
            second.readAll(new ByteArrayInputStream(bytes));
        }
        assert second.size() == 4;
        assert second.get("hello").equals("there");
        assert second.get("test").equals(10);
        assert second.get("thing").equals(-5.2);
        assert Arrays.equals(second.get("ints"), new int[] {1, 2, 3});
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
    
    
}
