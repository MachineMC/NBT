package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;
import org.machinemc.nbt.io.NBTOutputStream;

import java.io.IOException;

public class NBTShort implements NBT<Short> {

    private final short value;

    public NBTShort(Number number) {
        this(number.shortValue());
    }

    public NBTShort(short value) {
        this.value = value;
    }

    @Override
    public Tag tag() {
        return Tag.SHORT;
    }

    @Override
    public Short revert() {
        return value;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTShort clone() {
        return new NBTShort(value);
    }

    @Override
    public void write(NBTOutputStream stream) throws IOException {
        stream.writeShort(value);
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NBTShort other && value == other.value;
    }

    @Override
    public int hashCode() {
        return Short.hashCode(value);
    }

}
