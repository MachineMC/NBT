package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTByte(Byte value) implements NBTValue<Byte>, NBT {

    public NBTByte(Object value) {
        this(((Number) value).byteValue());
    }

    public NBTByte(Boolean value) {
        this(value ? 1 : 0);
    }

    public NBTByte(InputStream stream) throws IOException {
        this((byte) stream.read());
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write(value);
    }

    @Override
    public Tag tag() {
        return Tag.BYTE;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NBTByte nbtByte = (NBTByte) o;

        return value.equals(nbtByte.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public NBTByte clone() {
        try {
            return (NBTByte) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
