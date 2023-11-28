package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.OutputStream;

public class NBTLong implements NBT<Long> {

    private final long value;

    public NBTLong(Number number) {
        this(number.longValue());
    }

    public NBTLong(long value) {
        this.value = value;
    }

    @Override
    public Tag tag() {
        return Tag.LONG;
    }

    @Override
    public Long revert() {
        return value;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTLong clone() {
        return new NBTLong(value);
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NBTLong other && value == other.value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

}
