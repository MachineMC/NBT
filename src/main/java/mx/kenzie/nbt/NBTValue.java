package mx.kenzie.nbt;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public sealed interface NBTValue<T>
    extends NBT
    permits NBTByte, NBTByteArray, NBTCompound, NBTDouble, NBTEnd, NBTFloat, NBTInt, NBTIntArray, NBTList, NBTLong, NBTLongArray, NBTShort, NBTString {

    default boolean softEquals(Object object) {
        if (this == object) return true;
        if (this.equals(object)) return true;
        return (Objects.equals(this.value(), object));
    }

    T value();

    @FunctionalInterface
    interface Extractor<T> extends Function<NBTCompound, T> {

        @Override
        T apply(NBTCompound compound);

        default T apply(NBTCompound compound, T alternative) {
            final T found = this.apply(compound);
            if (found != null) return found;
            return alternative;
        }

    }

    @FunctionalInterface
    interface Inserter<T> extends BiConsumer<NBTCompound, T> {

        @Override
        void accept(NBTCompound compound, T type);

    }

}
