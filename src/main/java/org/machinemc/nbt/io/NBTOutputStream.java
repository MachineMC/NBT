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

    public void writeRootCompound(NBTCompound compound) throws IOException {
        writeRootCompound(compound, "");
    }

    public void writeRootCompound(NBTCompound compound, @Nullable String rootName) throws IOException {
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

    public void writeNBT(NBT<?> nbt) throws IOException {
        if (nbt instanceof NBTCompound || nbt instanceof NBTList) {
            writeObject(nbt);
            return;
        }
        writeObject(nbt.revert());
    }

    public void writeObject(Object object) throws IOException {
        if (object == null) writeTag(NBT.Tag.END);
        else if (object instanceof Byte value) writeByte(value);
        else if (object instanceof Short value) writeShort(value);
        else if (object instanceof Integer value) writeInt(value);
        else if (object instanceof Long value) writeLong(value);
        else if (object instanceof Float value) writeFloat(value);
        else if (object instanceof Double value) writeDouble(value);
        else if (object instanceof String value) writeString(value);
        else if (object instanceof byte[] value) writeByteArray(value);
        else if (object instanceof int[] value) writeIntArray(value);
        else if (object instanceof long[] value) writeLongArray(value);
        else if (object instanceof Collection<?> value) writeList(value);
        else if (object instanceof Map<?, ?> value) writeCompound(value);
        else throw new IllegalArgumentException("Cannot write object '" + object + "' as NBT");
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
            write(0);
            return;
        }
        write(value.length);
        write(value);
    }

    public void writeIntArray(int[] value) throws IOException {
        if (value == null) {
            write(0);
            return;
        }
        write(value.length);
        for (int i : value)
            writeInt(i);
    }

    public void writeLongArray(long[] value) throws IOException {
        if (value == null) {
            write(0);
            return;
        }
        write(value.length);
        for (long i : value)
            writeLong(i);
    }

    public void writeList(Collection<?> value) throws IOException {
        if (value == null) {
            writeEnd();
            write(0);
            return;
        }
        NBTList list = (NBTList) NBT.convert(new ArrayList<>(value));
        assert list != null;
        writeTag(list.getElementType());
        write(list.size());
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
        write(NBT.Tag.END.getID());
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
