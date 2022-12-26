package mx.kenzie.wellspring.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTDouble(Double value) implements NBTValue<Double>, NBT {
    public NBTDouble(Object value) {
        this((Double) value);
    }
    
    public NBTDouble(InputStream stream) throws IOException {
        this(Double.longBitsToDouble(NBTLong.decodeLong(stream)));
    }
    
    @Override
    public String toString() {
        return value.toString() + "d";
    }
    
    @Override
    public void write(OutputStream stream) throws IOException {
        final long value = Double.doubleToLongBits(this.value);
        NBTLong.encodeLong(stream, value);
    }
    
    @Override
    public Tag tag() {
        return Tag.DOUBLE;
    }
}
