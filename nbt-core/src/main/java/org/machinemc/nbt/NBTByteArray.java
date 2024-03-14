package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;
import org.jetbrains.annotations.NotNull;
import org.machinemc.nbt.io.NBTOutputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class NBTByteArray implements NBTArray<byte[], Byte> {

    private final byte[] bytes;

    public NBTByteArray(Byte[] bytes) {
        this(unbox(bytes));
    }

    public NBTByteArray(int... ints) {
        this(ints.length);
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) ints[i];
    }

    public NBTByteArray(int size) {
       this(new byte[size]);
    }

    public NBTByteArray(byte... bytes) {
        this.bytes = bytes;
    }

    @Override
    public Tag tag() {
        return Tag.BYTE_ARRAY;
    }

    @Override
    public byte[] revert() {
        return bytes.clone();
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTByteArray clone() {
        return new NBTByteArray(bytes.clone());
    }

    @Override
    public void write(NBTOutputStream stream) throws IOException {
        stream.writeByteArray(bytes);
    }

    @Override
    public int size() {
        return bytes.length;
    }

    @Override
    public Byte get(int index) {
        return bytes[index];
    }

    @Override
    public void set(int index, @NotNull Byte element) {
        bytes[index] = Objects.requireNonNull(element, "element");
    }

    @Override
    public Tag getElementType() {
        return Tag.BYTE;
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof NBTByteArray other && Arrays.equals(bytes, other.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    private static byte[] unbox(Byte[] value) {
        byte[] primitive = new byte[value.length];
        for (int i = 0; i < value.length; i++) primitive[i] = value[i];
        return primitive;
    }

}
