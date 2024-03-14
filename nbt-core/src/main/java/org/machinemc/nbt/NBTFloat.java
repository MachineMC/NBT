package org.machinemc.nbt;

import org.machinemc.nbt.io.NBTOutputStream;
import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;

public class NBTFloat implements NBT<Float> {

    private final float value;

    public NBTFloat(Number number) {
        this(number.floatValue());
    }

    public NBTFloat(float value) {
        this.value = value;
    }

    @Override
    public Tag tag() {
        return Tag.FLOAT;
    }

    @Override
    public Float revert() {
        return value;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTFloat clone() {
        return new NBTFloat(value);
    }

    @Override
    public void write(NBTOutputStream stream) throws IOException {
        stream.writeFloat(value);
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NBTFloat other && value == other.value;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }

}
