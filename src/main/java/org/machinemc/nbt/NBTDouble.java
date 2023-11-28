package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

public class NBTDouble implements NBT<Double> {

    private final double value;

    public NBTDouble(Number number) {
        this(number.doubleValue());
    }

    public NBTDouble(double value) {
        this.value = value;
    }

    @Override
    public Tag tag() {
        return Tag.DOUBLE;
    }

    @Override
    public Double revert() {
        return value;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTDouble clone() {
        return new NBTDouble(value);
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NBTDouble other && value == other.value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

}
