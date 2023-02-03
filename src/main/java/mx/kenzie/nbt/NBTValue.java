package mx.kenzie.nbt;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public sealed interface NBTValue<Type>
    extends NBT
    permits NBTByte, NBTByteArray, NBTCompound, NBTDouble, NBTEnd, NBTFloat, NBTInt, NBTIntArray, NBTList, NBTLong, NBTLongArray, NBTShort, NBTString {
    default boolean softEquals(Object object) {
        if (this == object) return true;
        if (this.equals(object)) return true;
        return (Objects.equals(this.value(), object));
    }

    Type value();

    @FunctionalInterface
    interface Extractor<Type> extends Function<NBTCompound, Type> {

        @Override
        Type apply(NBTCompound compound);

        default Type apply(NBTCompound compound, Type alternative) {
            final Type found = this.apply(compound);
            if (found != null) return found;
            return alternative;
        }

    }

    @FunctionalInterface
    interface Inserter<Type> extends BiConsumer<NBTCompound, Type> {

        @Override
        void accept(NBTCompound compound, Type type);

    }

}
