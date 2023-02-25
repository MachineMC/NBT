package mx.kenzie.nbt;

import mx.kenzie.nbt.visitor.NBTStringVisitor;
import mx.kenzie.nbt.visitor.NBTVisitor;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTLong(Long value) implements NBTValue<Long>, NBT {

    public NBTLong(Object value) {
        this(((Number) value).longValue());
    }

    public NBTLong(InputStream stream) throws IOException {
        this(decodeLong(stream));
    }

    static long decodeLong(InputStream stream) throws IOException {
        final long a = stream.read(), b = stream.read(), c = stream.read(), d = stream.read();
        final long f = stream.read(), g = stream.read(), h = stream.read(), i = stream.read();
        if (i < 0) throw new EOFException();
        return (a << 56) + ((b & 255) << 48) + ((c & 255) << 40) + ((d & 255) << 32) +
            ((f & 255) << 24) + ((g & 255) << 16) + ((h & 255) << 8) + (i & 255);
    }

    static void encodeLong(OutputStream stream, long value) throws IOException {
        final byte[] buffer = new byte[8];
        buffer[0] = (byte) (value >>> 56);
        buffer[1] = (byte) (value >>> 48);
        buffer[2] = (byte) (value >>> 40);
        buffer[3] = (byte) (value >>> 32);
        buffer[4] = (byte) (value >>> 24);
        buffer[5] = (byte) (value >>> 16);
        buffer[6] = (byte) (value >>> 8);
        buffer[7] = (byte) (value);
        stream.write(buffer);
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        NBTLong.encodeLong(stream, value);
    }

    @Override
    public Tag tag() {
        return Tag.LONG;
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

        NBTLong nbtLong = (NBTLong) o;

        return value.equals(nbtLong.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public NBTLong clone() {
        try {
            return (NBTLong) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
