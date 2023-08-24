package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public final class NBTIntArray implements NBTValue<int[]>, NBT, NBTArray<Integer> {

    private final int[] value;

    public NBTIntArray(int[] value) {
        this.value = value;
    }

    public NBTIntArray(Integer[] value) {
        this(unbox(value));
    }

    NBTIntArray(Object value) {
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
        return new NBTStringVisitor().visitNBT(this);
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
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Integer[] toArray() {
        final Integer[] array = new Integer[value.length];
        for (int i = 0; i < value.length; i++) array[i] = value[i];
        return array;
    }

    @Override
    public int size() {
        return value.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NBTIntArray nbtIntArray = (NBTIntArray) o;

        return Arrays.equals(value, nbtIntArray.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public NBTIntArray clone() {
        return new NBTIntArray(value.clone());
    }

    @Override
    public int[] value() {
        return value.clone();
    }

    private static int[] unbox(Integer... value) {
        final int[] array = new int[value.length];
        for (int i = 0; i < value.length; i++) array[i] = value[i];
        return array;
    }

}
