package mx.kenzie.nbt;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTInt(Integer value) implements NBTValue<Integer>, NBT {
    public NBTInt(Object value) {
        this(((Number) value).intValue());
    }
    
    public NBTInt(InputStream stream) throws IOException {
        this(decodeInt(stream));
    }
    
    static int decodeInt(InputStream stream) throws IOException {
        final int a = stream.read(), b = stream.read(), c = stream.read(), d = stream.read();
        if (d < 0) throw new EOFException();
        return ((a << 24) + (b << 16) + (c << 8) + d);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    @Override
    public void write(OutputStream stream) throws IOException {
        NBTInt.encodeInt(stream, value);
    }
    
    static void encodeInt(OutputStream stream, int value) throws IOException {
        final byte[] buffer = new byte[4];
        buffer[0] = (byte) (value >>> 24);
        buffer[1] = (byte) (value >>> 16);
        buffer[2] = (byte) (value >>> 8);
        buffer[3] = (byte) (value);
        stream.write(buffer);
    }
    
    @Override
    public Tag tag() {
        return Tag.INT;
    }
}
