package mx.kenzie.nbt;

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
    
    @Override
    public String toString() {
        return value.toString() + "l";
    }
    
    @Override
    public void write(OutputStream stream) throws IOException {
        NBTLong.encodeLong(stream, value);
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
    public Tag tag() {
        return Tag.LONG;
    }
}
