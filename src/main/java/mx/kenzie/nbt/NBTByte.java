package mx.kenzie.nbt;

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
        return value.toString() + "b";
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

}
