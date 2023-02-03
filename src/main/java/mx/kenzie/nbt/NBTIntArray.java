package mx.kenzie.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public record NBTIntArray(int[] value) implements NBTValue<int[]>, NBT, NBTArray<Integer> {
    public NBTIntArray(Object value) {
        this((int[]) value);
    }

    public NBTIntArray(InputStream stream) throws IOException {
        this(decodeInts(stream));
    }

    static int[] decodeInts(InputStream stream) throws IOException {
        final int[] ints = new int[NBTInt.decodeInt(stream)];
        for (int i = 0; i < ints.length; i++) ints[i] = NBTInt.decodeInt(stream);
        return ints;
    }

    @Override
    public String toString() {
        return Arrays.toString(value);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        NBTInt.encodeInt(stream, value.length);
        for (int i : value) NBTInt.encodeInt(stream, i);
    }

    @Override
    public Tag tag() {
        return Tag.INT_ARRAY;
    }

    @Override
    public Integer[] toArray() {
        final Integer[] array = new Integer[value.length];
        for (int i = 0; i < value.length; i++) array[i] = (Integer) value[i];
        return array;
    }

    @Override
    public int size() {
        return value.length;
    }

}
