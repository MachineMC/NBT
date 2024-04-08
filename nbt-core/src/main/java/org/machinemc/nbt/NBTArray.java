package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.nbt.io.NBTOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface NBTArray<T, E> extends NBT<T>, Iterable<E> {

    int size();

    E get(int index);

    void set(int index, @NotNull E element);

    Tag getElementType();

    @Override
    NBTArray<T, E> clone();

    default @UnmodifiableView NBTArray<T, E> unmodifiableView() {
        return new NBTArray<>() {

            @Override
            public int size() {
                return NBTArray.this.size();
            }

            @Override
            public E get(int index) {
                return NBTArray.this.get(index);
            }

            @Override
            public void set(int index, @NotNull E element) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Tag getElementType() {
                return NBTArray.this.tag();
            }

            @Override
            public NBTArray<T, E> clone() {
                return NBTArray.this.unmodifiableView().clone().unmodifiableView();
            }

            @Override
            public Tag tag() {
                return NBTArray.this.tag();
            }

            @Override
            public T revert() {
                return NBTArray.this.revert();
            }

            @Override
            public void accept(NBTVisitor visitor) {
                NBTArray.this.accept(visitor);
            }

            @Override
            public void write(NBTOutputStream stream) throws IOException {
                NBTArray.this.write(stream);
            }

            @Override
            public @UnmodifiableView NBTArray<T, E> unmodifiableView() {
                return this;
            }

            @Override
            public @NotNull Iterator<E> iterator() {
                return NBTArray.this.iterator();
            }

            @Override
            public void forEach(Consumer<? super E> action) {
                NBTArray.this.forEach(action);
            }

            @Override
            public Spliterator<E> spliterator() {
                return NBTArray.this.spliterator();
            }

            @Override
            public void write(OutputStream stream) throws IOException {
                NBTArray.this.write(stream);
            }

            @Override
            public boolean softEquals(Object object) {
                return NBTArray.this.softEquals(object);
            }

            @Override
            public int hashCode() {
                return NBTArray.this.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return NBTArray.this.equals(obj);
            }

            @Override
            public String toString() {
                return NBTArray.this.toString();
            }

        };
    }

    @Override
    default @NotNull Iterator<E> iterator() {
        return new Iterator<>() {

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
