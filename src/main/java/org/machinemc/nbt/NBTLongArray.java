package org.machinemc.nbt;

import org.jetbrains.annotations.NotNull;
import org.machinemc.nbt.io.NBTOutputStream;
import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class NBTLongArray implements NBTArray<long[], Long> {

    private final long[] longs;

    public NBTLongArray(Long[] longs) {
        this(unbox(longs));
    }

    public NBTLongArray(int size) {
        this(new Long[size]);
    }

    public NBTLongArray(long... longs) {
        this.longs = longs;
    }

    @Override
    public Tag tag() {
        return Tag.LONG_ARRAY;
    }

    @Override
    public long[] revert() {
        return longs.clone();
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTLongArray clone() {
        return new NBTLongArray(longs.clone());
    }

    @Override
    public void write(NBTOutputStream stream) throws IOException {
        stream.writeLongArray(longs);
    }

    @Override
    public int size() {
        return longs.length;
    }

    @Override
    public Long get(int index) {
        return longs[index];
    }

    @Override
    public void set(int index, @NotNull Long element) {
        longs[index] = Objects.requireNonNull(element, "element");
    }

    @Override
    public Tag getElementType() {
        return Tag.LONG;
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof NBTLongArray other && Arrays.equals(longs, other.longs);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(longs);
    }

    private static long[] unbox(Long[] value) {
        long[] primitive = new long[value.length];
        for (int i = 0; i < value.length; i++) primitive[i] = value[i];
        return primitive;
    }

}
