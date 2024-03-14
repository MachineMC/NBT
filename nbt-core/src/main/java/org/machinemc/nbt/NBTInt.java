package org.machinemc.nbt;

import org.machinemc.nbt.io.NBTOutputStream;
import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;

public class NBTInt implements NBT<Integer> {

    private final int value;

    public NBTInt(Number number) {
        this(number.intValue());
    }

    public NBTInt(int value) {
        this.value = value;
    }

    @Override
    public Tag tag() {
        return Tag.INT;
    }

    @Override
    public Integer revert() {
        return value;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTInt clone() {
        return new NBTInt(value);
    }

    @Override
    public void write(NBTOutputStream stream) throws IOException {
        stream.writeInt(value);
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NBTInt other && value == other.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}
