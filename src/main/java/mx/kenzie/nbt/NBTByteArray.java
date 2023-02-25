package mx.kenzie.nbt;

import mx.kenzie.nbt.visitor.NBTStringVisitor;
import mx.kenzie.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public record NBTByteArray(byte[] value) implements NBTValue<byte[]>, NBT, NBTArray<Byte> {

    public NBTByteArray(Object value) {
        this((byte[]) value);
    }

    public NBTByteArray(InputStream stream) throws IOException {
        this(decodeBytes(stream));
    }

    static byte[] decodeBytes(InputStream stream) throws IOException {
        final byte[] bytes = new byte[NBTInt.decodeInt(stream)];
        final int read = stream.read(bytes);
        assert read == bytes.length;
        return bytes;
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        NBTInt.encodeInt(stream, value.length);
        stream.write(value);
    }

    @Override
    public Tag tag() {
        return Tag.BYTE_ARRAY;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Byte[] toArray() {
        final Byte[] bytes = new Byte[value.length];
        for (int i = 0; i < value.length; i++) bytes[i] = value[i];
        return bytes;
    }

    @Override
    public int size() {
        return value.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NBTByteArray bytes = (NBTByteArray) o;

        return Arrays.equals(value, bytes.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public NBTByteArray clone() {
        try {
            return (NBTByteArray) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
