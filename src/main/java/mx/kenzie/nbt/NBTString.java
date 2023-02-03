package mx.kenzie.nbt;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public record NBTString(String value) implements NBTValue<String>, NBT {
    public NBTString(Object value) {
        this(value instanceof String string ? string : value.toString());
    }

    public NBTString(InputStream stream) throws IOException {
        this(decodeString(stream));
    }

    static String decodeString(InputStream stream) throws IOException {
        int a = stream.read(), b = stream.read();
        if (b < 0) throw new EOFException();
        final int length = ((a << 8) + b);
        final byte[] bytes = new byte[length];
        final int read = stream.read(bytes, 0, length);
        assert read == length;
        return new String(bytes, StandardCharsets.UTF_8);
    }

    static void encodeString(OutputStream stream, String value) throws IOException {
        final int length = value == null ? 0 : value.length();
        stream.write((byte) (length >>> 8));
        stream.write((byte) length);
        if (length < 1) return;
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        stream.write(bytes);
    }

    @Override
    public String toString() {
        return '"' + value + '"';
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        NBTString.encodeString(stream, value);
    }

    @Override
    public Tag tag() {
        return Tag.STRING;
    }

    public int length() {
        return value == null ? 0 : value.length();
    }


}
