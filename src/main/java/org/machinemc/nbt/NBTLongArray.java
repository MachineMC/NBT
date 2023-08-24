package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public final class NBTLongArray implements NBTValue<long[]>, NBT, NBTArray<Long> {

    private final long[] value;

    public NBTLongArray(long[] value) {
        this.value = value;
    }

    public NBTLongArray(Long[] value) {
        this(unbox(value));
    }

    NBTLongArray(Object value) {
        this((long[]) value);
    }

    public NBTLongArray(InputStream stream) throws IOException {
        this(decodeLongs(stream));
    }

    static long[] decodeLongs(InputStream stream) throws IOException {
        final long[] longs = new long[NBTInt.decodeInt(stream)];
        for (int i = 0; i < longs.length; i++) longs[i] = NBTLong.decodeLong(stream);
        return longs;
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        NBTInt.encodeInt(stream, value.length);
        for (long j : value) NBTLong.encodeLong(stream, j);
    }

    @Override
    public Tag tag() {
        return Tag.LONG_ARRAY;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Long[] toArray() {
        final Long[] array = new Long[value.length];
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

        NBTLongArray nbtLongArray = (NBTLongArray) o;

        return Arrays.equals(value, nbtLongArray.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public NBTLongArray clone() {
        return new NBTLongArray(value.clone());
    }

    @Override
    public long[] value() {
        return value.clone();
    }

    private static long[] unbox(Long... longs) {
        final long[] array = new long[longs.length];
        for (int i = 0; i < longs.length; i++) array[i] = longs[i];
        return array;
    }

}