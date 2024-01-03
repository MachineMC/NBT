package org.machinemc.nbt;

import org.machinemc.nbt.io.NBTInputStream;
import org.machinemc.nbt.io.NBTOutputStream;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
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
        write(stream instanceof NBTOutputStream ? (NBTOutputStream) stream : new NBTOutputStream(stream, false));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default void write(NBTOutputStream stream) throws IOException {
        ((Tag.Writer) tag().writer).write(stream, this.revert());
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

        END(Void.class, object -> NBTEnd.INSTANCE, (stream, unused) -> stream.writeEnd(), stream -> {
            throw new UnsupportedOperationException("Cannot read Tag_END NBT");
        }),
        BYTE(Byte.class, NBTByte::new, NBTOutputStream::writeByte, NBTInputStream::readByte),
        SHORT(Short.class, NBTShort::new, NBTOutputStream::writeShort, NBTInputStream::readShort),
        INT(Integer.class, NBTInt::new, NBTOutputStream::writeInt, NBTInputStream::readInt),
        LONG(Long.class, NBTLong::new, NBTOutputStream::writeLong, NBTInputStream::readLong),
        FLOAT(Float.class, NBTFloat::new, NBTOutputStream::writeFloat, NBTInputStream::readFloat),
        DOUBLE(Double.class, NBTDouble::new, NBTOutputStream::writeDouble, NBTInputStream::readDouble),
        BYTE_ARRAY(byte[].class, NBTByteArray::new, NBTOutputStream::writeByteArray, NBTInputStream::readByteArray),
        STRING(String.class, NBTString::new, NBTOutputStream::writeString, NBTInputStream::readString),
        LIST(List.class, NBTList::new, NBTOutputStream::writeList, NBTInputStream::readList),
        COMPOUND(Map.class, NBTCompound::new, NBTOutputStream::writeCompound, NBTInputStream::readCompound),
        INT_ARRAY(int[].class, NBTIntArray::new, NBTOutputStream::writeIntArray, NBTInputStream::readIntArray),
        LONG_ARRAY(long[].class, NBTLongArray::new, NBTOutputStream::writeLongArray, NBTInputStream::readLongArray);

        private final Class<?> type;
        private final Function<Object, NBT<?>> make;
        private final Writer<?> writer;
        private final Reader reader;

        @SuppressWarnings({"unchecked", "rawtypes"})
        <T> Tag(Class<T> type, Function<T, NBT<? extends T>> make, Writer<T> writer, Reader reader) {
            this.type = type;
            this.make = (Function) make;
            this.writer = writer;
            this.reader = reader;
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

        public NBT<?> read(InputStream stream) throws IOException {
            NBTInputStream nbtStream = stream instanceof NBTInputStream ? (NBTInputStream) stream : new NBTInputStream(stream);
            return reader.read(nbtStream);
        }

        @FunctionalInterface
        private interface Writer<T> {
            void write(NBTOutputStream stream, T t) throws IOException;
        }

        @FunctionalInterface
        private interface Reader {
            NBT<?> read(NBTInputStream stream) throws IOException;
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
