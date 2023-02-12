package mx.kenzie.nbt;

import mx.kenzie.nbt.exceptions.NBTException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public final class NBTList implements NBTValue<List<NBT>>, NBT, List<NBT> {

    private final List<NBT> list;
    private Tag type = Tag.END;

    public NBTList(NBTList list) {
        this();
        this.list.addAll(list.list);
        this.type = list.type;
    }

    public NBTList() {
        this.list = new LinkedList<>();
    }

    public NBTList(Object... array) {
        this();
        for (final Object value : array) this.add(NBT.convert(value));
    }

    public NBTList(InputStream stream) throws IOException {
        this();
        final Tag[] tags = Tag.values();
        final Tag tag = tags[stream.read()];
        final int length = NBTInt.decodeInt(stream);
        if (tag == Tag.END || length < 1) return;
        switch (tag) {
            case BYTE -> {
                for (int i = 0; i < length; i++) this.add(new NBTByte(stream));
            }
            case SHORT -> {
                for (int i = 0; i < length; i++) this.add(new NBTShort(stream));
            }
            case INT -> {
                for (int i = 0; i < length; i++) this.add(new NBTInt(stream));
            }
            case LONG -> {
                for (int i = 0; i < length; i++) this.add(new NBTLong(stream));
            }
            case FLOAT -> {
                for (int i = 0; i < length; i++) this.add(new NBTFloat(stream));
            }
            case DOUBLE -> {
                for (int i = 0; i < length; i++) this.add(new NBTDouble(stream));
            }
            case BYTE_ARRAY -> {
                for (int i = 0; i < length; i++) this.add(new NBTByteArray(stream));
            }
            case STRING -> {
                for (int i = 0; i < length; i++) this.add(new NBTString(stream));
            }
            case LIST -> {
                for (int i = 0; i < length; i++) this.add(new NBTList(stream));
            }
            case COMPOUND -> {
                for (int i = 0; i < length; i++) this.add(new NBTCompound(stream));
            }
            case INT_ARRAY -> {
                for (int i = 0; i < length; i++) this.add(new NBTIntArray(stream));
            }
            case LONG_ARRAY -> {
                for (int i = 0; i < length; i++) this.add(new NBTLongArray(stream));
            }
            default -> throw new IOException("Unexpected value: " + tag);
        }
    }

    public NBTList(Object value) {
        this();
        if (value instanceof Collection<?> collection)
            for (final Object thing : collection) this.add(NBT.convert(thing));
        else if (value instanceof Object[] array)
            for (final Object thing : array) this.add(NBT.convert(thing));
        else this.add(NBT.convert(value));
    }

    public NBTList(NBT... items) {
        this();
        this.addAll(Arrays.asList(items));
    }

    public NBTList(Collection<?> collection) {
        this();
        for (final Object value : collection) this.add(NBT.convert(value));
    }

    private void tag(NBT nbt) {
        if (type == Tag.END) this.type = nbt.tag();
        else if (type != nbt.tag())
            throw new NBTException("Lists may contain one type of value. This is marked for '" + type.name() + "'");
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        if (type == Tag.END && list.size() > 0) this.tag(list.get(0));
        stream.write(type.ordinal());
        NBTInt.encodeInt(stream, list.size());
        for (final NBT nbt : list) nbt.write(stream);
    }

    @Override
    public Tag tag() {
        return Tag.LIST;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    public Tag getType() {
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
        for (final NBT nbt : list) if (nbt.softEquals(o)) return true;
        return false;
    }

    @Override
    public Iterator<NBT> iterator() {
        return list.iterator();
    }

    @Override
    public NBT[] toArray() {
        return list.toArray(new NBT[0]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(NBT nbt) {
        this.tag(nbt);
        return list.add(nbt);
    }

    @Override
    public boolean remove(Object o) {
        final boolean z = list.remove(o);
        if (list.isEmpty()) type = Tag.END;
        return z;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(list).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends NBT> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends NBT> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.remove(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        this.list.clear();
        this.type = Tag.END;
    }

    @Override
    public NBT get(int index) {
        return list.get(index);
    }

    @Override
    public NBT set(int index, NBT element) {
        this.tag(element);
        return list.set(index, element);
    }

    @Override
    public void add(int index, NBT element) {
        this.tag(element);
        this.list.add(index, element);
    }

    @Override
    public NBT remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<NBT> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<NBT> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<NBT> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public boolean addValue(Object object) {
        return this.add(NBT.convert(object));
    }

    public boolean contains(NBT nbt) {
        return list.contains(nbt);
    }

    @Override
    public List<NBT> value() {
        return list;
    }

    public List<?> revert() {
        return list.stream()
                .map(NBT::revert)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append('[');
        boolean first = true;
        for (final NBT nbt : list) {
            if (first) first = false;
            else builder.append(", ");
            builder.append(nbt.toString());
        }
        builder.append(']');
        return builder.toString();
    }

}
