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
    
    public <Type> void set(String key, Type value) {
        if (value == null) this.remove(key);
        else if (value instanceof NBT nbt) this.map.put(key, nbt);
        else this.map.put(key, NBT.convert(value));
    }
    
    public NBTCompound(InputStream stream) throws IOException {
        this();
        this.read(stream);
    }
    
    public void read(InputStream stream) throws IOException {
        final Tag[] tags = Tag.values();
        for (int i = stream.read(); i != -1; i = stream.read()) {
            final Tag tag = tags[i];
            if (tag == Tag.END) return;
            final String key = NBTString.decodeString(stream);
            final NBT nbt = switch (tag) {
                case BYTE -> new NBTByte((byte) stream.read());
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
    
}
