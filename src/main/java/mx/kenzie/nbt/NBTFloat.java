package mx.kenzie.wellspring.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTFloat(Float value) implements NBTValue<Float>, NBT {
    public NBTFloat(Object value) {
        this((Float) value);
    }
    
    public NBTFloat(InputStream stream) throws IOException {
        this(Float.intBitsToFloat(NBTInt.decodeInt(stream)));
    }
    
    @Override
    public String toString() {
        return value.toString() + "f";
    }
    
    @Override
    public void write(OutputStream stream) throws IOException {
        final int value = Float.floatToIntBits(this.value);
        NBTInt.encodeInt(stream, value);
    }
    
    @Override
    public Tag tag() {
        return Tag.FLOAT;
    }
}
