package mx.kenzie.nbt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface NBT {
    static NBT convert(Object value) {
        if (value == null) return NBTEnd.INSTANCE;
        if (value instanceof NBT nbt) return nbt;
        if (value instanceof Map<?, ?> map) return new NBTCompound(map);
        if (value instanceof List<?> list) return new NBTList(list);
        for (Tag tag : Tag.values()) for (Class<?> type : tag.types) if (type.isInstance(value)) return tag.make(value);
        return NBTEnd.INSTANCE;
    }

    static Object revert(NBT nbt) {
        return nbt.tag().revert(nbt);
    }
    
    <Type> Type value();
    
    default void write(OutputStream stream) throws IOException {
    }
    
    boolean softEquals(Object object);
    
    Tag tag();
    
    enum Tag implements Type {
        END,
        BYTE(NBTByte::new, (NBT::value), Byte.class),
        SHORT(NBTShort::new, (NBT::value), Short.class),
        INT(NBTInt::new, (NBT::value), Integer.class),
        LONG(NBTLong::new, (NBT::value), Long.class),
        FLOAT(NBTFloat::new, (NBT::value), Float.class),
        DOUBLE(NBTDouble::new, (NBT::value), Double.class),
        BYTE_ARRAY(NBTByteArray::new, (nbt -> ((byte[]) nbt.value()).clone()), byte[].class),
        STRING(NBTString::new, (NBT::value), String.class),
        LIST(NBTList::new, (nbt -> ((NBTList) nbt).revert()), List.class),
        COMPOUND(NBTCompound::new, (nbt -> ((NBTCompound) nbt).revert()), Map.class),
        INT_ARRAY(NBTIntArray::new, (nbt -> ((int[]) nbt.value()).clone()), int[].class),
        LONG_ARRAY(NBTLongArray::new, (nbt -> ((long[]) nbt.value()).clone()), long[].class);
        public final Class<?>[] types;
        public final Function<Object, NBT> make;
        public final Function<NBT, Object> revert;
        
        Tag(Function<Object, NBT> make, Function<NBT, Object> revert, Class<?>... types) {
            this.make = make;
            this.revert = revert;
            this.types = types;
        }
        
        Tag() {
            this.types = new Class[0];
            this.make = NBTEnd::getInstance;
            this.revert = (nbt -> null);
        }
        
        public NBT make(Object value) {
            return make.apply(value);
        }

        public Object revert(NBT nbt) {
            return revert.apply(nbt);
        }

    }
    
    interface Type {
        int ordinal();
        
        NBT make(Object value);
    }
}
