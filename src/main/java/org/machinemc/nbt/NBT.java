package org.machinemc.nbt;

import org.machinemc.nbt.io.NBTOutputStream;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface NBT<T> {

    Tag tag();

    T revert();

    void accept(NBTVisitor visitor);

    NBT<T> clone();

    default void write(OutputStream stream) throws IOException {
        write(stream, false);
    }

    default void write(OutputStream stream, boolean compress) throws IOException {
        new NBTOutputStream(stream, compress).writeNBT(this);
    }

    default boolean softEquals(Object object) {
        if (this == object) return true;
        if (equals(object)) return true;
        return !(object instanceof NBT<?>) && Objects.equals(revert(), object);
    }

    static NBT<?> convert(Object object) {
        if (object instanceof NBT<?> nbt) return nbt;
        if (object == null) return NBTEnd.INSTANCE;
        if (object instanceof Boolean bool) return new NBTByte(bool);
        for (Tag tag : Tag.values()) {
            NBT<?> nbt = tag.make(object);
            if (nbt != null) return nbt;
        }
        return null;
    }

    static <T> T revert(NBT<T> nbt) {
        return nbt != null ? nbt.revert() : null;
    }

    enum Tag {

        END(Void.class, object -> NBTEnd.INSTANCE),
        BYTE(Byte.class, NBTByte::new),
        SHORT(Short.class, NBTShort::new),
        INT(Integer.class, NBTInt::new),
        LONG(Long.class, NBTLong::new),
        FLOAT(Float.class, NBTFloat::new),
        DOUBLE(Double.class, NBTDouble::new),
        BYTE_ARRAY(byte[].class, NBTByteArray::new),
        STRING(String.class, NBTString::new),
        LIST(List.class, NBTList::new),
        COMPOUND(Map.class, NBTCompound::new),
        INT_ARRAY(int[].class, NBTIntArray::new),
        LONG_ARRAY(long[].class, NBTLongArray::new);

        private final Class<?> type;
        private final Function<Object, NBT<?>> make;

        @SuppressWarnings({"unchecked", "rawtypes"})
        <T> Tag(Class<T> type, Function<T, NBT<? extends T>> make) {
            this.type = type;
            this.make = (Function) make;
        }

        public int getID() {
            return ordinal();
        }

        public String getTypeName() {
            return "Tag_" + name();
        }

        public NBT<?> make(Object object) {
            if (!type.isInstance(object)) return null;
            return make.apply(object);
        }

    }

    @FunctionalInterface
    interface Extractor<T> extends Function<NBTCompound, T> {

        T extract(NBTCompound compound);

        @Override
        default T apply(NBTCompound compound) {
            return extract(compound);
        }

        default T extract(NBTCompound compound, T alternative) {
            T value = extract(compound);
            return value != null ? value : alternative;
        }

    }

    @FunctionalInterface
    interface Inserter<T> extends BiConsumer<NBTCompound, T> {

        void insert(NBTCompound compound, T t);

        @Override
        default void accept(NBTCompound compound, T t) {
            insert(compound, t);
        }

    }

}
