package mx.kenzie.nbt;

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
        return value.toString() + "s";
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
}
