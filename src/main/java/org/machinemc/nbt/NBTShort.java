package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTShort(Short value) implements NBTValue<Short>, NBT {

    public NBTShort(Object value) {
        this(((Number) value).shortValue());
    }

    public NBTShort(InputStream stream) throws IOException {
        this(decodeShort(stream));
    }

    static short decodeShort(InputStream stream) throws IOException {
        final int a = stream.read(), b = stream.read();
        if (b < 0) throw new EOFException();
        return (short) ((a << 8) + b);
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write((value >>> 8));
        stream.write(value);
    }

    @Override
    public Tag tag() {
        return Tag.SHORT;
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

        NBTShort nbtShort = (NBTShort) o;

        return value.equals(nbtShort.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public NBTShort clone() {
        try {
            return (NBTShort) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
