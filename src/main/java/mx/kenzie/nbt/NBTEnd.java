package mx.kenzie.nbt;

import mx.kenzie.nbt.visitor.NBTStringVisitor;
import mx.kenzie.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.OutputStream;

public record NBTEnd(Void value) implements NBTValue<Void>, NBT {

    public static final NBTEnd INSTANCE = new NBTEnd(null);

    public static NBTEnd getInstance(Object object) {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write(Tag.END.ordinal());
    }

    @Override
    public Tag tag() {
        return Tag.END;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NBTEnd;
    }

    @Override
    public NBTEnd clone() {
        try {
            return (NBTEnd) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
