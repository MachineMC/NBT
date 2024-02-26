package org.machinemc.nbt.io;

import org.jetbrains.annotations.NotNull;
import org.machinemc.nbt.*;
import org.machinemc.nbt.exceptions.NBTException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

public class NBTInputStream extends InputStream {

    // https://en.wikipedia.org/wiki/Gzip#File_format
    private static final short GZIP_HEADER = 0x1F8B;
    private static final byte[] GZIP_HEADER_BYTES = {(byte) (GZIP_HEADER >>> Byte.SIZE), (byte) (GZIP_HEADER & 0xFF)};

    private InputStream in;

    public NBTInputStream(InputStream in) {
        this.in = in;
    }

    public NBTCompound readRootCompound() throws IOException {
        return readRootCompound(true);
    }

    public NBTCompound readRootCompound(boolean hasRootName) throws IOException {
        unzipIfNecessary();
        NBT.Tag tag = readTag();
        if (tag != NBT.Tag.COMPOUND)
            throw new NBTException("Expected " + NBT.Tag.COMPOUND.getTypeName() + ", but got " + tag.getTypeName());
        if (hasRootName) readString().revert();
        return readCompound();
    }

    public NBT<?> readNBT(NBT.Tag tag) throws IOException {
        return tag.read(this);
    }

    public NBTByte readByte() throws IOException {
        return new NBTByte(read());
    }

    public NBTShort readShort() throws IOException {
        return new NBTShort(read() << Byte.SIZE | read());
    }

    public NBTInt readInt() throws IOException {
        int value = 0;
        for (int i = Integer.BYTES - 1; i >= 0; i--)
            value |= read() << Byte.SIZE * i;
        return new NBTInt(value);
    }

    public NBTLong readLong() throws IOException {
        long value = 0;
        for (int i = Long.BYTES - 1; i >= 0; i--)
            value |= (long) read() << Byte.SIZE * i;
        return new NBTLong(value);
    }

    public NBTFloat readFloat() throws IOException {
        return new NBTFloat(Float.intBitsToFloat(readInt().revert()));
    }

    public NBTDouble readDouble() throws IOException {
        return new NBTDouble(Double.longBitsToDouble(readLong().revert()));
    }

    public NBTString readString() throws IOException {
        short length = (short) (read() << Byte.SIZE | read());
        return new NBTString(new String(readNBytes(length), StandardCharsets.UTF_8));
    }

    public NBTByteArray readByteArray() throws IOException {
        return new NBTByteArray(readNBytes(readInt().revert()));
    }

    public NBTIntArray readIntArray() throws IOException {
        int length = readInt().revert();
        int[] array = new int[length];
        for (int i = 0; i < length; i++)
            array[i] = readInt().revert();
        return new NBTIntArray(array);
    }

    public NBTLongArray readLongArray() throws IOException {
        int length = readInt().revert();
        long[] array = new long[length];
        for (int i = 0; i < length; i++)
            array[i] = readLong().revert();
        return new NBTLongArray(array);
    }

    public NBTList readList() throws IOException {
        NBT.Tag elementType = readTag();
        int length = readInt().revert();
        NBTList list = new NBTList(elementType);
        for (int i = 0; i < length; i++)
            list.add(readNBT(elementType));
        return list;
    }

    public NBTCompound readCompound() throws IOException {
        NBTCompound compound = new NBTCompound();
        NBT<?> value;
        while (true) {
            NBT.Tag tag = readTag();
            if (tag == NBT.Tag.END) break;
            String key = readString().revert();
            value = readNBT(tag);
            compound.set(key, value);
        }
        return compound;
    }

    public NBT.Tag readTag() throws IOException {
        return NBT.Tag.values()[read()];
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int read(byte @NotNull [] b) throws IOException {
        return in.read(b);
    }

    @Override
    public int read(byte @NotNull [] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return in.readAllBytes();
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        return in.readNBytes(len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return in.readNBytes(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public void skipNBytes(long n) throws IOException {
        in.skipNBytes(n);
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return in.transferTo(out);
    }

    private void unzipIfNecessary() throws IOException {
        in = new PushbackInputStream(in, 2);
        byte[] header = in.readNBytes(2);
        ((PushbackInputStream) in).unread(header);
        if (Arrays.equals(GZIP_HEADER_BYTES, header))
            in = new GZIPInputStream(in);
    }

}
