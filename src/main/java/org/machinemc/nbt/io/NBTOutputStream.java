package org.machinemc.nbt.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTList;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class NBTOutputStream extends OutputStream {

    private final OutputStream out;

    public NBTOutputStream(OutputStream out, boolean compress) throws IOException {
        this.out = compress ? new GZIPOutputStream(out) : out;
    }

    public void writeRootCompound(@Nullable NBTCompound compound) throws IOException {
        writeRootCompound(compound, "");
    }

    public void writeRootCompound(@Nullable NBTCompound compound, @Nullable String rootName) throws IOException {
        try {
            if (compound == null) {
                writeEnd();
                return;
            }
            writeTag(compound.tag());
            if (rootName != null) writeObject(rootName);
            writeNBT(compound);
        } finally {
            if (out instanceof GZIPOutputStream gzip) gzip.finish();
        }
    }

    public void writeObject(Object object) throws IOException {
        NBT nbt = NBT.convert(object);
        if (nbt == null) throw new IllegalArgumentException("Cannot write object '" + object + "' as NBT");
        writeNBT(nbt);
    }

    public void writeNBT(NBT<?> nbt) throws IOException {
        nbt.write(this);
    }

    public void writeTag(NBT.Tag tag) throws IOException {
        write(tag.getID());
    }

    public void writeByte(byte value) throws IOException {
        write(value);
    }

    public void writeShort(short value) throws IOException {
        write(value >>> Byte.SIZE);
        write(value & 0xFF);
    }

    public void writeInt(int value) throws IOException {
        for (int i = Integer.BYTES - 1; i >= 0; i--)
            write((byte) (value >>> i * Byte.SIZE & 0xFF));
    }

    public void writeLong(long value) throws IOException {
        for (int i = Long.BYTES - 1; i >= 0; i--)
            write((byte) (value >>> i * Byte.SIZE & 0xFF));
    }

    public void writeFloat(float value) throws IOException {
        writeInt(Float.floatToIntBits(value));
    }

    public void writeDouble(double value) throws IOException {
        writeLong(Double.doubleToLongBits(value));
    }

    public void writeString(String value) throws IOException {
        int length = value == null ? 0 : value.length();
        writeShort((short) length);
        if (length < 1) return;
        write(value.getBytes(StandardCharsets.UTF_8));
    }

    public void writeByteArray(byte[] value) throws IOException {
        if (value == null) {
            writeInt(0);
            return;
        }
        writeInt(value.length);
        write(value);
    }

    public void writeIntArray(int[] value) throws IOException {
        if (value == null) {
            writeInt(0);
            return;
        }
        writeInt(value.length);
        for (int i : value)
            writeInt(i);
    }

    public void writeLongArray(long[] value) throws IOException {
        if (value == null) {
            writeInt(0);
            return;
        }
        writeInt(value.length);
        for (long i : value)
            writeLong(i);
    }

    public void writeList(Collection<?> value) throws IOException {
        if (value == null) {
            writeEnd();
            writeInt(0);
            return;
        }
        NBTList list = (NBTList) NBT.convert(new ArrayList<>(value));
        assert list != null;
        writeTag(list.getElementType());
        writeInt(list.size());
        for (NBT<?> nbt : list)
            writeNBT(nbt);
    }

    public void writeCompound(Map<?, ?> value) throws IOException {
        NBTCompound compound = (NBTCompound) NBT.convert(value);
        assert compound != null;
        for (Map.Entry<String, NBT<?>> entry : compound) {
            writeTag(entry.getValue().tag());
            writeString(entry.getKey());
            writeNBT(entry.getValue());
        }
        writeEnd();
    }

    public void writeEnd() throws IOException {
        writeTag(NBT.Tag.END);
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte @NotNull [] b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte @NotNull [] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

}
