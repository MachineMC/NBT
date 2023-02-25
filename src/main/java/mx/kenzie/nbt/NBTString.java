package mx.kenzie.nbt;

import mx.kenzie.nbt.visitor.NBTStringVisitor;
import mx.kenzie.nbt.visitor.NBTVisitor;

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
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        NBTString.encodeString(stream, value);
    }

    @Override
    public Tag tag() {
        return Tag.STRING;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    public int length() {
        return value == null ? 0 : value.length();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NBTString nbtString = (NBTString) o;

        return value.equals(nbtString.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public NBTString clone() {
        try {
            return (NBTString) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String quoteAndEscape(String var0) {
        StringBuilder builder = new StringBuilder();
        char quote = 0;

        for(int var3 = 0; var3 < var0.length(); ++var3) {
            char current = var0.charAt(var3);

            if (current == '\\') {
                builder.append('\\');
            } else if (current == '"' || current == '\'') {
                if (quote == 0)
                    quote = current == '"' ? '\'' : '"';

                if (quote == current)
                    builder.append('\\');
            }

            builder.append(current);
        }

        if (quote == 0)
            quote = '\"';

        builder.insert(0, quote);
        builder.append(quote);
        return builder.toString();
    }

}
