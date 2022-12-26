package mx.kenzie.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public record NBTLongArray(long[] value) implements NBTValue<long[]>, NBT {
    public NBTLongArray(Object value) {
        this((long[]) value);
    }
    
    public NBTLongArray(InputStream stream) throws IOException {
        this(decodeLongs(stream));
    }
    
    static long[] decodeLongs(InputStream stream) throws IOException {
        final long[] longs = new long[NBTInt.decodeInt(stream)];
        for (int i = 0; i < longs.length; i++) longs[i] = NBTLong.decodeLong(stream);
        return longs;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(value);
    }
    
    @Override
    public void write(OutputStream stream) throws IOException {
        NBTInt.encodeInt(stream, value.length);
        for (long j : value) NBTLong.encodeLong(stream, j);
    }
    
    @Override
    public Tag tag() {
        return Tag.LONG_ARRAY;
    }
}
