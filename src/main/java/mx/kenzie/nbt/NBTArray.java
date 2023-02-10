package mx.kenzie.nbt;

import java.util.Iterator;

public interface NBTArray<Value> extends Iterable<Value> {

    @Override
    default Iterator<Value> iterator() {
        return new ArrayIterator<>(this.toArray());
    }

    Value[] toArray();

    default int size() {
        return this.toArray().length;
    }

}

class ArrayIterator<Value> implements Iterator<Value> {
    private final Value[] array;
    private int index = 0;

    @SafeVarargs
    public ArrayIterator(Value... array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return index < array.length;
    }

    @Override
    public Value next() {
        return array[index++];
    }

    @Override
    @Deprecated
    public void remove() {
        this.array[index] = null;
    }

}
