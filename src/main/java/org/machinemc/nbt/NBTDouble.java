package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTDouble(Double value) implements NBTValue<Double>, NBT {

    public NBTDouble(Object value) {
        this(((Number) value).doubleValue());
    }

    public NBTDouble(InputStream stream) throws IOException {
        this(Double.longBitsToDouble(NBTLong.decodeLong(stream)));
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        final long value = Double.doubleToLongBits(this.value);
        NBTLong.encodeLong(stream, value);
    }

    @Override
    public Tag tag() {
        return Tag.DOUBLE;
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

        NBTDouble nbtDouble = (NBTDouble) o;

        return value.equals(nbtDouble.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public NBTDouble clone() {
        try {
            return (NBTDouble) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
