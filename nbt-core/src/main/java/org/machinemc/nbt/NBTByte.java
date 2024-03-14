package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;
import org.machinemc.nbt.io.NBTOutputStream;

import java.io.IOException;

public class NBTByte implements NBT<Byte> {

    private final byte value;

    public NBTByte(Number number) {
        this(number.byteValue());
    }

    public NBTByte(boolean bool) {
        this((byte) (bool ? 1 : 0));
    }

    public NBTByte(byte value) {
        this.value = value;
    }

    @Override
    public Tag tag() {
        return Tag.BYTE;
    }

    @Override
    public Byte revert() {
        return value;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTByte clone() {
        return new NBTByte(value);
    }

    @Override
    public void write(NBTOutputStream stream) throws IOException {
        stream.writeByte(value);
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NBTByte other && value == other.value;
    }

    @Override
    public int hashCode() {
        return Byte.hashCode(value);
    }

}
