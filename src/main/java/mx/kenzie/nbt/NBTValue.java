package mx.kenzie.nbt;

import java.util.Objects;

public sealed interface NBTValue<Type>
    extends NBT
    permits NBTByte, NBTByteArray, NBTCompound, NBTDouble, NBTEnd, NBTFloat, NBTInt, NBTIntArray, NBTList, NBTLong, NBTLongArray, NBTShort, NBTString {
    default boolean softEquals(Object object) {
        if (this == object) return true;
        if (this.equals(object)) return true;
        return (Objects.equals(this.value(), object));
    }
    
    Type value();
}
