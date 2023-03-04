package org.machinemc.nbt;

import java.util.Iterator;

public interface NBTArray<T> extends Iterable<T> {

    @Override
    default Iterator<T> iterator() {
        return new ArrayIterator<>(this.toArray());
    }

    T[] toArray();

    default int size() {
        return this.toArray().length;
    }

}

class ArrayIterator<T> implements Iterator<T> {

    private final T[] array;
    private int index = 0;

    @SafeVarargs
    public ArrayIterator(T... array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return index < array.length;
    }

    @Override
    public T next() {
        return array[index++];
    }

    @Override
    @Deprecated
    public void remove() {
        this.array[index] = null;
    }

}
