package org.machinemc.nbt;

import org.jetbrains.annotations.NotNull;
import org.machinemc.nbt.exceptions.NBTException;
import org.machinemc.nbt.io.NBTOutputStream;
import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.util.*;

public class NBTList implements NBT<List<Object>>, List<NBT<?>> {

    private final List<NBT<?>> list;
    private Tag type = Tag.END;

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

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(NBT.convert(o));
    }

    @Override
    public @NotNull Iterator<NBT<?>> iterator() {
        return list.iterator();
    }

    @Override
    public NBT<?> @NotNull [] toArray() {
        return list.toArray(new NBT[0]);
    }

    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        return list.toArray(a);
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

    @Override
    public NBT<?> get(int index) {
        return list.get(index);
    }

    public Object setValue(int index, Object element) {
        return NBT.revert(set(index, NBT.convert(element)));
    }

    @Override
    public NBT<?> set(int index, NBT<?> element) {
        return list.set(index, check(element));
    }

    public void addValue(int index, Object element) {
        add(index, NBT.convert(element));
    }

    @Override
    public void add(int index, NBT<?> element) {
        list.add(index, check(element));
    }

    @Override
    public NBT<?> remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(NBT.convert(o));
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(NBT.convert(o));
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

    public boolean addValue(Object element) {
        return add(NBT.convert(element));
    }

    @Override
    public boolean add(NBT<?> nbt) {
        return list.add(check(nbt));
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(NBT.convert(o));
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        Collection<? extends NBT<?>> other = new NBTList(c).list;
        return new HashSet<>(list).containsAll(other);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends NBT<?>> c) {
        int length = c.size();
        if (length == 0) return false;
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends NBT<?>> c) {
        int length = c.size();
        if (length == 0) return false;
        for (NBT<?> nbt : c) add(index++, nbt);
        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean modified = false;
        for (Object element : c)
            modified |= remove(element);
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
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

}
