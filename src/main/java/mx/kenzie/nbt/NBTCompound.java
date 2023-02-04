package mx.kenzie.nbt;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public final class NBTCompound implements NBTValue<Map<String, NBT>>, Iterable<String>, Map<String, NBT>, NBT {
    private final Map<String, NBT> map = new HashMap<>();

    public NBTCompound(NBTCompound compound) {
        this();
        this.map.putAll(compound.map);
    }

    public NBTCompound() {
    }

    public NBTCompound(Object value) {
        this((Map<?, ?>) value);
    }

    public NBTCompound(Map<?, ?> map) {
        this();
        if (map instanceof NBTCompound compound) this.map.putAll(compound.map);
        else for (final Entry<?, ?> entry : map.entrySet()) {
            this.set(entry.getKey().toString(), entry.getValue());
        }
    }

    public NBTCompound(InputStream stream) throws IOException {
        this();
        this.read(stream);
    }

    private static UUID fromInts(int[] array) {
        if (array.length < 4) return null;
        final long big = 0xFFFFFFFFL;
        return new UUID((long) array[0] << 32 | (long) array[1] & big, (long) array[2] << 32 | (long) array[3] & big);
    }

    private static int[] toInts(UUID uuid) {
        final long most = uuid.getMostSignificantBits();
        final long least = uuid.getLeastSignificantBits();
        return new int[]{(int) (most >> 32), (int) most, (int) (least >> 32), (int) least};
    }

    public <Type> void set(String key, Type value) {
        if (value == null) this.remove(key);
        else if (value instanceof NBT nbt) this.map.put(key, nbt);
        else if (value instanceof UUID uuid) this.setUUID(key, uuid);
        else this.map.put(key, NBT.convert(value));
    }

    public <Type> void set(String key, Inserter<Type> inserter, Type value) {
        if (value == null) {
            this.remove(key);
            return;
        }
        final NBTCompound compound = new NBTCompound();
        inserter.accept(compound, value);
        this.map.put(key, compound);
    }

    public <Type> void setList(String key, Inserter<Type> inserter, Collection<Type> value) {
        if (value == null) {
            this.remove(key);
            return;
        }
        final NBTList list = new NBTList();
        for (Type type : value) {
            final NBTCompound compound = new NBTCompound();
            inserter.accept(compound, type);
            list.add(compound);
        }
        this.map.put(key, list);
    }

    @SafeVarargs
    public final <Type> void setList(String key, Inserter<Type> inserter, Type... value) {
        if (value == null) {
            this.remove(key);
            return;
        }
        this.setList(key, inserter, List.of(value));
    }

    public void read(InputStream stream) throws IOException {
        final Tag[] tags = Tag.values();
        for (int i = stream.read(); i != -1; i = stream.read()) {
            final Tag tag = tags[i];
            if (tag == Tag.END) return;
            final String key = NBTString.decodeString(stream);
            final NBT nbt = switch (tag) {
                case BYTE -> new NBTByte(stream);
                case SHORT -> new NBTShort(stream);
                case INT -> new NBTInt(stream);
                case LONG -> new NBTLong(stream);
                case FLOAT -> new NBTFloat(stream);
                case DOUBLE -> new NBTDouble(stream);
                case BYTE_ARRAY -> new NBTByteArray(stream);
                case STRING -> new NBTString(stream);
                case LIST -> new NBTList(stream);
                case COMPOUND -> new NBTCompound(stream);
                case INT_ARRAY -> new NBTIntArray(stream);
                case LONG_ARRAY -> new NBTLongArray(stream);
                default -> throw new IOException("Unexpected value: " + tag);
            };
            this.put(key, nbt);
        }
    }

    public void set(String key, byte... value) {
        this.map.put(key, new NBTByteArray(value));
    }

    public void set(String key, int... value) {
        this.map.put(key, new NBTIntArray(value));
    }

    public void set(String key, long... value) {
        this.map.put(key, new NBTLongArray(value));
    }

    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public NBT get(Object key) {
        return map.get(key);
    }

    @Override
    public NBT put(String key, NBT value) {
        return map.put(key, value);
    }

    @Override
    public NBT remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends NBT> map) {
        this.map.putAll(map);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<NBT> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, NBT>> entrySet() {
        return map.entrySet();
    }

    public <Type> Type get(String key) {
        return map.get(key).value();
    }

    @SuppressWarnings("unchecked")
    public <Type> Type get(String key, Type alternative) {
        if (alternative instanceof NBT) return (Type) map.getOrDefault(key, NBT.convert(alternative));
        return map.getOrDefault(key, NBT.convert(alternative)).value();
    }

    public <Type> Type get(String key, Extractor<Type> extractor) {
        if (map.get(key) instanceof NBTCompound compound) return extractor.apply(compound);
        return null;
    }

    public <Type> Type get(String key, Extractor<Type> extractor, Type alternative) {
        if (map.get(key) instanceof NBTCompound compound) return extractor.apply(compound, alternative);
        return alternative;
    }

    @SuppressWarnings("unchecked")
    public <Type extends NBT> Type get(String key, Tag tag) {
        final NBT nbt = map.get(key);
        if (nbt == null) return null;
        if (nbt.tag() == tag) return (Type) nbt;
        throw new NBTException("Requested tag is a '" + nbt.tag() + "' not a '" + tag + "'.");
    }

    public NBTList getList(String key) {
        final NBT nbt = map.get(key);
        if (nbt instanceof NBTList list) return list;
        return new NBTList(nbt);
    }

    public <Type> List<Type> getList(String key, Extractor<Type> extractor) {
        if (!(map.get(key) instanceof NBTList list)) return null;
        final List<Type> converted = new ArrayList<>(list.size());
        for (NBT nbt : list) {
            if (!(nbt instanceof NBTCompound compound)) throw new NBTException("List contains a non-compound element.");
            converted.add(extractor.apply(compound));
        }
        return converted;
    }

    public <Type> List<Type> getList(String key, Extractor<Type> extractor, List<Type> alternative) {
        try {
            final List<Type> list = this.getList(key, extractor);
            if (list != null) return list;
            return alternative;
        } catch (NBTException ex) {
            return alternative;
        }
    }

    @Override
    public Iterator<String> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public void forEach(Consumer<? super String> action) {
        this.map.keySet().forEach(action);
    }

    @Override
    public Spliterator<String> spliterator() {
        return map.keySet().spliterator();
    }

    @Override
    public Map<String, NBT> value() {
        return map;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append('{');
        boolean first = true;
        for (final Entry<String, NBT> entry : entrySet()) {
            if (first) first = false;
            else builder.append(", ");
            builder.append('"').append(entry.getKey()).append('"').append(": ");
            builder.append(entry.getValue().toString());
        }
        builder.append('}');
        return builder.toString();
    }

    public void read(File file) throws IOException {
        try (final InputStream stream = new FileInputStream(file)) {
            this.readAll(stream);
        }
    }

    public void readAll(InputStream stream) {
        try {
            final int tag = stream.read();
            assert tag == this.tag().ordinal(); // we are discarding this anyway
            NBTString.decodeString(stream); // ignore the empty base tag
            this.read(stream);
        } catch (IOException ex) {
            throw new NBTException(ex);
        }
    }

    public void write(File file) throws IOException {
        try (final OutputStream stream = new FileOutputStream(file)) {
            this.writeAll(stream);
        }
    }

    public void writeAll(OutputStream stream) {
        try {
            stream.write(this.tag().ordinal());
            NBTString.encodeString(stream, "");
            this.write(stream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        for (final Entry<String, NBT> entry : map.entrySet()) {
            stream.write(entry.getValue().tag().ordinal());
            NBTString.encodeString(stream, entry.getKey());
            entry.getValue().write(stream);
        }
        stream.write(Tag.END.ordinal());
    }

    @Override
    public Tag tag() {
        return Tag.COMPOUND;
    }

    public boolean contains(String key, NBT.Tag tag) {
        if (!this.containsKey(key)) return false;
        return map.get(key).tag() == tag;
    }

    public void setUUID(String key, UUID value) {
        if (this.contains(key + "Most", Tag.LONG) && this.contains(key + "Least", Tag.LONG)) {
            this.map.remove(key + "Most");
            this.map.remove(key + "Least");
        }
        this.map.put(key, new NBTIntArray(toInts(value)));
    }

    public UUID getUUID(String key, UUID alternative) {
        final UUID found = this.getUUID(key);
        if (found == null) return alternative;
        return found;
    }

    public UUID getUUID(String key) {
        if (this.contains(key, Tag.INT_ARRAY)) { // I would love to know why we use four ints rather than two longs
            final int[] value = this.get(key);
            return fromInts(value);
        } else if (this.contains(key + "Most", Tag.LONG) && this.contains(key + "Least", Tag.LONG)) {
            return new UUID(this.get(key + "Most"), this.get(key + "Least"));
        } else return null;
    }

    public boolean hasUUID(String key) {
        if (this.contains(key + "Most", Tag.LONG) && this.contains(key + "Least", Tag.LONG)) return true;
        if (!this.containsKey(key)) return false;
        final NBT nbt = map.get(key);
        return map.get(key).tag() == Tag.INT_ARRAY && nbt.value() instanceof int[] ints && ints.length == 4;
    }

}
