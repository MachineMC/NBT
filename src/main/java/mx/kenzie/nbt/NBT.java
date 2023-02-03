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

    <Type> Type value();

    default void write(OutputStream stream) throws IOException {
    }

    boolean softEquals(Object object);

    Tag tag();

    enum Tag implements Type {
        END,
        BYTE(NBTByte::new, Byte.class),
        SHORT(NBTShort::new, Short.class),
        INT(NBTInt::new, Integer.class),
        LONG(NBTLong::new, Long.class),
        FLOAT(NBTFloat::new, Float.class),
        DOUBLE(NBTDouble::new, Double.class),
        BYTE_ARRAY(NBTByteArray::new, byte[].class),
        STRING(NBTString::new, String.class),
        LIST(NBTList::new, List.class),
        COMPOUND(NBTCompound::new, Map.class),
        INT_ARRAY(NBTIntArray::new, int[].class),
        LONG_ARRAY(NBTLongArray::new, long[].class);
        public final Class<?>[] types;
        public final Function<Object, NBT> function;

        Tag(Function<Object, NBT> function, Class<?>... types) {
            this.function = function;
            this.types = types;
        }

        Tag() {
            this.types = new Class[0];
            this.function = NBTEnd::getInstance;
        }

        public NBT make(Object value) {
            return function.apply(value);
        }
    }

    interface Type {
        int ordinal();

        NBT make(Object value);
    }
}
