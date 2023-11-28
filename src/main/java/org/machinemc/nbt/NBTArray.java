package org.machinemc.nbt;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface NBTArray<T, E> extends NBT<T>, Iterable<E> {

    int size();

    E get(int index);

    void set(int index, @NotNull E element);

    Tag getElementType();

    @Override
    NBTArray<T, E> clone();

    @Override
    default @NotNull Iterator<E> iterator() {
        return new Iterator<E>() {

            private final int size = size();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public E next() {
                return get(index++);
            }

        };
    }

}
