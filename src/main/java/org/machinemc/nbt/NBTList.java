package org.machinemc.nbt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.nbt.exceptions.NBTException;
import org.machinemc.nbt.io.NBTOutputStream;
import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Consumer;

public class NBTList implements NBT<List<Object>>, Iterable<NBT<?>> {

    private final List<NBT<?>> list;
    private Tag type = Tag.END;

    private transient ListView listView;
    private transient NBTList unmodifiableView;

    public NBTList() {
        this(Tag.END);
    }

    public NBTList(Tag type) {
        this.list = new ArrayList<>();
        this.type = type;
    }

    public NBTList(Object... objects) {
        this(List.of(objects));
    }

    public NBTList(NBT<?>... elements) {
        this(List.of(elements));
    }

    public NBTList(Collection<?> collection) {
        this(collection.size());
        for (Object element : collection)
            addValue(element);
    }

    public NBTList(int initialCapacity) {
        this.list = new ArrayList<>(initialCapacity);
    }

    @Override
    public Tag tag() {
        return Tag.LIST;
    }

    @Override
    public List<Object> revert() {
        List<Object> list = new ArrayList<>(size());
        for (NBT<?> nbt : this)
            list.add(NBT.revert(nbt));
        return list;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTList clone() {
        return new NBTList(list);
    }

    @Override
    public void write(NBTOutputStream stream) throws IOException {
        stream.writeList(list);
    }

    public Tag getElementType() {
        return type;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object o) {
        return list.contains(NBT.convert(o));
    }

    @Override
    public @NotNull Iterator<NBT<?>> iterator() {
        return list.iterator();
    }

    public <T> T getValue(int index, T defaultValue) {
        T value = getValue(index);
        return value != null ? value : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(int index) {
        return (T) NBT.revert(get(index));
    }

    @SuppressWarnings("unchecked")
    public <T extends NBT<?>> T get(int index, T defaultValue) {
        T value = (T) get(index);
        return value != null ? value : defaultValue;
    }

    public NBT<?> get(int index) {
        return list.get(index);
    }

    public Object setValue(int index, Object element) {
        return NBT.revert(set(index, NBT.convert(element)));
    }

    public NBT<?> set(int index, NBT<?> element) {
        return list.set(index, check(element));
    }

    public void addValue(int index, Object element) {
        add(index, NBT.convert(element));
    }

    public void add(int index, NBT<?> element) {
        list.add(index, check(element));
    }

    public NBT<?> remove(int index) {
        return list.remove(index);
    }

    public int indexOf(Object o) {
        return list.indexOf(NBT.convert(o));
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(NBT.convert(o));
    }

    public boolean addValue(Object element) {
        return add(NBT.convert(element));
    }

    public boolean add(NBT<?> nbt) {
        return list.add(check(nbt));
    }

    public boolean remove(Object o) {
        return list.remove(NBT.convert(o));
    }

    public void clear() {
        list.clear();
    }

    public List<NBT<?>> listView() {
        if (listView == null) listView = new ListView();
        return listView;
    }

    public @UnmodifiableView NBTList unmodifiableView() {
        if (unmodifiableView == null) unmodifiableView = new UnmodifiableListView();
        return unmodifiableView;
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof NBTList other && Objects.equals(list, other.list);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    private NBT<?> check(NBT<?> nbt) {
        if (nbt == null)
            throw new NBTException("Lists may not contain null values");

        if (type == Tag.END) {
            type = nbt.tag();
            return nbt;
        }
        if (type != nbt.tag())
            throw new NBTException("Lists may contain one type of value. This is marked for '" + type.getTypeName() + "'");
        return nbt;
    }

    private class ListView implements List<NBT<?>> {

        @Override
        public int size() {
            return NBTList.this.size();
        }

        @Override
        public boolean isEmpty() {
            return NBTList.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return NBTList.this.contains(o);
        }

        @Override
        public @NotNull Iterator<NBT<?>> iterator() {
            return NBTList.this.iterator();
        }

        @Override
        public NBT<?> @NotNull [] toArray() {
            return list.toArray(new NBT[0]);
        }

        @Override
        public <T> T @NotNull [] toArray(T @NotNull [] a) {
            return list.toArray(a);
        }

        @Override
        public boolean add(NBT<?> nbt) {
            return NBTList.this.add(nbt);
        }

        @Override
        public boolean remove(Object o) {
            return NBTList.this.remove(o);
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            for (Object object : c) {
                if (!NBTList.this.contains(object))
                    return false;
            }
            return true;
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends NBT<?>> c) {
            boolean modified = false;
            for (NBT<?> nbt : c)
                modified |= NBTList.this.add(nbt);
            return modified;
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends NBT<?>> c) {
            boolean modified = false;
            for (NBT<?> nbt : c)
                NBTList.this.add(index++, nbt);
            return modified;
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            boolean modified = false;
            for (Object object : c)
                modified |= NBTList.this.remove(object);
            return modified;
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            boolean modified = false;
            Iterator<NBT<?>> iterator = iterator();
            while (iterator.hasNext()) {
                if (!c.contains(iterator.next())) {
                    iterator.remove();
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public void clear() {
            NBTList.this.clear();
        }

        @Override
        public NBT<?> get(int index) {
            return NBTList.this.get(index);
        }

        @Override
        public NBT<?> set(int index, NBT<?> element) {
            return NBTList.this.set(index, element);
        }

        @Override
        public void add(int index, NBT<?> element) {
            NBTList.this.add(index, element);
        }

        @Override
        public NBT<?> remove(int index) {
            return NBTList.this.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return NBTList.this.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return NBTList.this.lastIndexOf(o);
        }

        @Override
        public @NotNull ListIterator<NBT<?>> listIterator() {
            return list.listIterator();
        }

        @Override
        public @NotNull ListIterator<NBT<?>> listIterator(int index) {
            return list.listIterator(index);
        }

        @Override
        public @NotNull List<NBT<?>> subList(int fromIndex, int toIndex) {
            return list.subList(fromIndex, toIndex);
        }

    }

    private class UnmodifiableListView extends NBTList {

        @Override
        public Tag tag() {
            return NBTList.this.tag();
        }

        @Override
        public List<Object> revert() {
            return NBTList.this.revert();
        }

        @Override
        public void accept(NBTVisitor visitor) {
            NBTList.this.accept(visitor);
        }

        @Override
        public NBTList clone() {
            return NBTList.this.clone().unmodifiableView();
        }

        @Override
        public void write(NBTOutputStream stream) throws IOException {
            NBTList.this.write(stream);
        }

        @Override
        public Tag getElementType() {
            return NBTList.this.getElementType();
        }

        @Override
        public int size() {
            return NBTList.this.size();
        }

        @Override
        public boolean isEmpty() {
            return NBTList.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return NBTList.this.contains(o);
        }

        @Override
        public @NotNull Iterator<NBT<?>> iterator() {
            return new Iterator<>() {
                private final Iterator<NBT<?>> iterator = NBTList.this.iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public NBT<?> next() {
                    return iterator.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super NBT<?>> action) {
                    iterator.forEachRemaining(action);
                }
            };
        }

        @Override
        public <T> T getValue(int index, T defaultValue) {
            return NBTList.this.getValue(index, defaultValue);
        }

        @Override
        public <T> T getValue(int index) {
            return NBTList.this.getValue(index);
        }

        @Override
        public <T extends NBT<?>> T get(int index, T defaultValue) {
            return NBTList.this.get(index, defaultValue);
        }

        @Override
        public NBT<?> get(int index) {
            return NBTList.this.get(index);
        }

        @Override
        public Object setValue(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NBT<?> set(int index, NBT<?> element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addValue(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, NBT<?> element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NBT<?> remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            return NBTList.this.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return NBTList.this.lastIndexOf(o);
        }

        @Override
        public boolean addValue(Object element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(NBT<?> nbt) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<NBT<?>> listView() {
            return Collections.unmodifiableList(NBTList.this.listView());
        }

        @Override
        public @UnmodifiableView NBTList unmodifiableView() {
            return this;
        }

        @Override
        public String toString() {
            return NBTList.this.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return NBTList.this.equals(obj);
        }

        @Override
        public int hashCode() {
            return NBTList.this.hashCode();
        }

        @Override
        public void forEach(Consumer<? super NBT<?>> action) {
            NBTList.this.forEach(action);
        }

        @Override
        public Spliterator<NBT<?>> spliterator() {
            return NBTList.this.spliterator();
        }

        @Override
        public void write(OutputStream stream) throws IOException {
            NBTList.this.write(stream);
        }

        @Override
        public boolean softEquals(Object object) {
            return NBTList.this.softEquals(object);
        }

    }
    
}
