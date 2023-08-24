package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public final class NBTByteArray implements NBTValue<byte[]>, NBT, NBTArray<Byte> {

    private final byte[] value;

    public NBTByteArray(byte[] value) {
        this.value = value;
    }

    public NBTByteArray(Byte[] value) {
        this(unbox(value));
    }

    NBTByteArray(Object value) {
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
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public NBTByteArray clone() {
        return new NBTByteArray(value.clone());
    }

    @Override
    public byte[] value() {
        return value.clone();
    }

    private static byte[] unbox(Byte... value) {
        final byte[] array = new byte[value.length];
        for (int i = 0; i < value.length; i++) array[i] = value[i];
        return array;
    }

}
