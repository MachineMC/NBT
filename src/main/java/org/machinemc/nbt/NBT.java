package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents NBT element.
 */
public interface NBT extends Cloneable {

    /**
     * Converts object to its NBT counterpart.
     * @param value value to convert
     * @return NBT element
     */
    static NBT convert(Object value) {
        if (value == null) return NBTEnd.INSTANCE;
        if (value instanceof NBT nbt) return nbt;
        if (value instanceof Map<?, ?> map) return new NBTCompound(map);
        if (value instanceof List<?> list) return new NBTList(list);
        if (value instanceof Boolean boo) return new NBTByte(boo);
        for (Tag tag : Tag.values()) for (Class<?> type : tag.types) if (type.isInstance(value)) return tag.make(value);
        return NBTEnd.INSTANCE;
    }

    /**
     * Reverts NBT element to its original value.
     * @param nbt NBT element
     * @return object
     */
    static Object revert(NBT nbt) {
        return nbt.tag().revert(nbt);
    }

    /**
     * Returns original object backing the NBT element.
     * @return value
     * @param <T> NBT value
     */
    <T> T value();

    /**
     * Writes the NBT element to a stream.
     * @param stream output stream
     */
    default void write(OutputStream stream) throws IOException {
    }

    /**
     * Checks whether provided object equals this element or its value.
     * @param object object
     * @return whether the object is equal to this element or value
     */
    boolean softEquals(Object object);

    /**
     * @return type of the element
     */
    Tag tag();

    /**
     * Visits the element using NBT visitor
     * @param visitor visitor
     */
    void accept(NBTVisitor visitor);

    /**
     * @return deep clone of this NBT
     */
    NBT clone();

    /**
     * Represents type of NBT element
     */
    enum Tag {

        END,
        BYTE(NBTByte::new, NBT::value, Byte.class),
        SHORT(NBTShort::new, NBT::value, Short.class),
        INT(NBTInt::new, NBT::value, Integer.class),
        LONG(NBTLong::new, NBT::value, Long.class),
        FLOAT(NBTFloat::new, NBT::value, Float.class),
        DOUBLE(NBTDouble::new, NBT::value, Double.class),
        BYTE_ARRAY(NBTByteArray::new, (nbt -> ((byte[]) nbt.value()).clone()), byte[].class),
        STRING(NBTString::new, NBT::value, String.class),
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
            this.revert = nbt -> null;
        }

        /**
         * Creates NBT element of given type using given value.
         * @param value value
         * @return NBT element
         */
        public NBT make(Object value) {
            return make.apply(value);
        }

        /**
         * Returns backing value of NBT element.
         * @param nbt NBT element
         * @return value
         */
        public Object revert(NBT nbt) {
            return revert.apply(nbt);
        }

    }

}
