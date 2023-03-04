package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTFloat(Float value) implements NBTValue<Float>, NBT {

    public NBTFloat(Object value) {
        this(((Number) value).floatValue());
    }

    public NBTFloat(InputStream stream) throws IOException {
        this(Float.intBitsToFloat(NBTInt.decodeInt(stream)));
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        final int value = Float.floatToIntBits(this.value);
        NBTInt.encodeInt(stream, value);
    }

    @Override
    public Tag tag() {
        return Tag.FLOAT;
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

        NBTFloat nbtFloat = (NBTFloat) o;

        return value.equals(nbtFloat.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public NBTFloat clone() {
        try {
            return (NBTFloat) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
