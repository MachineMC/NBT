package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;
import org.machinemc.nbt.io.NBTOutputStream;

import java.io.IOException;

public class NBTEnd implements NBT<Void> {

    public static final NBTEnd INSTANCE = new NBTEnd();

    private NBTEnd() {}

    @Override
    public Tag tag() {
        return Tag.END;
    }

    @Override
    public Void revert() {
        return null;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTEnd clone() {
        return INSTANCE;
    }

    @Override
    public void write(NBTOutputStream stream) throws IOException {
        stream.writeEnd();
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

}
