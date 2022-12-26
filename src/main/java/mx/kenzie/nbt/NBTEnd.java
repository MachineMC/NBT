package mx.kenzie.nbt;

import java.io.IOException;
import java.io.OutputStream;

public record NBTEnd(Void value) implements NBTValue<Void>, NBT {
    public static final NBTEnd INSTANCE = new NBTEnd(null);
    
    public static NBTEnd getInstance(Object object) {
        return INSTANCE;
    }
    
    @Override
    public String toString() {
        return "null";
    }
    
    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write(Tag.END.ordinal());
    }
    
    @Override
    public Tag tag() {
        return Tag.END;
    }
}
