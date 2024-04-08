package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;
import org.jetbrains.annotations.NotNull;
import org.machinemc.nbt.io.NBTOutputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class NBTIntArray implements NBTArray<int[], Integer> {

    private final int[] ints;

    public NBTIntArray(Integer[] ints) {
        this(unbox(ints));
    }

    public NBTIntArray(int size) {
        this(new int[size]);
    }

    public NBTIntArray(int... ints) {
        this.ints = ints;
    }

    @Override
    public Tag tag() {
        return Tag.INT_ARRAY;
    }

    @Override
    public int[] revert() {
        return ints.clone();
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTIntArray clone() {
        return new NBTIntArray(ints.clone());
    }

    @Override
    public void write(NBTOutputStream stream) throws IOException {
        stream.writeIntArray(ints);
    }

    @Override
    public int size() {
        return ints.length;
    }

    @Override
    public Integer get(int index) {
        return ints[index];
    }

    @Override
    public void set(int index, @NotNull Integer element) {
        ints[index] = Objects.requireNonNull(element, "element");
    }

    @Override
    public Tag getElementType() {
        return Tag.INT;
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof NBTIntArray other && Arrays.equals(ints, other.ints);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(ints);
    }

    private static int[] unbox(Integer[] value) {
        int[] primitive = new int[value.length];
        for (int i = 0; i < value.length; i++) primitive[i] = value[i];
        return primitive;
    }

}
