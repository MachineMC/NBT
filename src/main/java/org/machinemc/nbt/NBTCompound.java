package org.machinemc.nbt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.nbt.exceptions.NBTException;
import org.machinemc.nbt.io.NBTInputStream;
import org.machinemc.nbt.io.NBTOutputStream;
import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NBTCompound implements NBT<Map<String, Object>>, Iterable<Map.Entry<String, NBT<?>>> {

    private final Map<String, NBT<?>> map;
    
    private transient Map<String, NBT<?>> mapView;
    private transient NBTCompound unmodifiableView;

    public NBTCompound() {
        this(new HashMap<>());
    }

    public NBTCompound(Map<?, ?> map) {
        this.map = new HashMap<>();
        map.forEach((key, value) -> set(key + "", value));
    }

    @Override
    public void write(NBTOutputStream stream) throws IOException {
        stream.writeCompound(map);
    }

    public void writeToFile(File file) throws IOException {
        writeToFile(file, false);
    }

    public void writeToFile(File file, boolean compress) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            writeRoot(new NBTOutputStream(stream, compress));
        }
    }

    public void writeRoot(OutputStream stream) throws IOException {
        writeRoot(stream instanceof NBTOutputStream ? (NBTOutputStream) stream : new NBTOutputStream(stream, false));
    }

    public void writeRoot(NBTOutputStream stream) throws IOException {
        stream.writeRootCompound(this);
    }

    public void writeRoot(OutputStream stream, @Nullable String rootName) throws IOException {
        writeRoot(stream instanceof NBTOutputStream ? (NBTOutputStream) stream : new NBTOutputStream(stream, false), rootName);
    }

    public void writeRoot(NBTOutputStream stream, @Nullable String rootName) throws IOException {
        stream.writeRootCompound(this, rootName);
    }

    @Override
    public Tag tag() {
        return Tag.COMPOUND;
    }

    @Override
    public Map<String, Object> revert() {
        Map<String, Object> map = new HashMap<>();
        forEach((key, nbt) -> map.put(key, NBT.revert(nbt)));
        return map;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTCompound clone() {
        return new NBTCompound(map);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(String key) {
        if (key == null) return false;
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        if (value == null) return false;
        return map.containsValue(NBT.convert(value));
    }

    public boolean containsTag(Tag tag) {
        if (tag == null || tag == Tag.END) return false;
        for (NBT<?> value : values()) {
            if (tag.equals(value.tag())) return true;
        }
        return false;
    }

    public <T> T getValue(String key, T defaultValue) {
        T value = getValue(key);
        return value != null ? value : defaultValue;
    }

    public <T> T getValue(String key) {
        return NBT.revert(getNBT(key));
    }

    public <T> List<T> getList(String key, Extractor<T> extractor) {
        if (!(getNBT(key) instanceof NBTList nbtList)) return null;
        List<T> list = new ArrayList<>(nbtList.size());
        for (NBT<?> nbt : nbtList) {
            if (!(nbt instanceof NBTCompound compound)) throw new NBTException("List contains a non-compound element");
            list.add(extractor.extract(compound));
        }
        return list;
    }

    public <T> T get(String key, Extractor<T> extractor, T defaultValue) {
        T value = get(key, extractor);
        return value != null ? value : defaultValue;
    }

    public <T> T get(String key, Extractor<T> extractor) {
        return getNBT(key) instanceof NBTCompound compound ? extractor.extract(compound) : null;
    }

    public <T extends NBT<?>> T getNBT(String key, T defaultValue) {
        T value = getNBT(key);
        return value != null ? value : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T extends NBT<?>> T getNBT(String key) {
        return (T) map.get(key);
    }

    @SafeVarargs
    public final <T> void setList(String key, Inserter<T> inserter, T @Nullable ... values) {
        setList(key, inserter, values != null ? Arrays.asList(values) : null);
    }

    public <T> void setList(String key, Inserter<T> inserter, @Nullable Collection<T> values) {
        check(key, values);
        if (values == null) {
            remove(key);
            return;
        }
        NBTList list = new NBTList();
        for (T value : values) {
            NBTCompound compound = new NBTCompound();
            inserter.insert(compound, value);
            list.add(compound);
        }
        set(key, list);
    }

    public <T> void set(String key, Inserter<T> inserter, @Nullable T value) {
        check(key, value);
        if (value == null) {
            remove(key);
            return;
        }
        NBTCompound compound = new NBTCompound();
        inserter.insert(compound, value);
        set(key, compound);
    }

    public void set(String key, @Nullable Object value) {
        check(key, value);
        if (value == null) {
            remove(key);
            return;
        }
        map.put(key, NBT.convert(value));
    }

    public NBT<?> remove(String key) {
        return map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public @NotNull Set<String> keySet() {
        return map.keySet();
    }

    public @NotNull Collection<NBT<?>> values() {
        return map.values();
    }

    public @NotNull Set<Map.Entry<String, NBT<?>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public @NotNull Iterator<Map.Entry<String, NBT<?>>> iterator() {
        return entrySet().iterator();
    }

    public void forEach(BiConsumer<? super String, ? super NBT<?>> action) {
        map.forEach(action);
    }
    
    public Map<String, NBT<?>> mapView() {
        if (mapView == null) mapView = new MapView();
        return mapView;
    }
    
    public @UnmodifiableView NBTCompound unmodifiableView() {
        if (unmodifiableView == null) unmodifiableView = new UnmodifiableCompoundView();
        return unmodifiableView;
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NBTCompound other && Objects.equals(map, other.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    private void check(Object key, Object value) {
        if (key == null)
            throw new NBTException("Compounds cannot have null keys");
        if (value instanceof NBT<?> nbt && nbt.tag() == Tag.END)
            throw new NBTException(Tag.END.getTypeName() + " cannot be used as a value");
    }

    @SafeVarargs
    public static NBTCompound ofEntries(Map.Entry<String, Object>... entries) {
        NBTCompound nbtCompound = new NBTCompound();
        for (Map.Entry<String, Object> entry : entries) {
            NBT<?> value = NBT.convert(entry.getValue());
            if (value == null)
                throw new IllegalArgumentException("Couldn't convert '" + entry.getValue() + "' to an NBT value");
            nbtCompound.set(entry.getKey(), value);
        }
        return nbtCompound;
    }

    public static NBTCompound readFromFile(File file) throws IOException {
        return readFromFile(file, false);
    }

    public static NBTCompound readFromFile(File file, boolean hasRootKey) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return readRootCompound(stream, hasRootKey);
        }
    }

    public static NBTCompound readRootCompound(InputStream stream) throws IOException {
        return new NBTInputStream(stream).readRootCompound();
    }

    public static NBTCompound readRootCompound(InputStream stream, boolean hasRootKey) throws IOException {
        return new NBTInputStream(stream).readRootCompound(hasRootKey);
    }

    public static NBTCompound readCompound(InputStream stream) throws IOException {
        return new NBTInputStream(stream).readCompound();
    }

    private class MapView implements Map<String, NBT<?>> {

        @Override
        public int size() {
            return NBTCompound.this.size();
        }

        @Override
        public boolean isEmpty() {
            return NBTCompound.this.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return NBTCompound.this.containsKey(String.valueOf(key));
        }

        @Override
        public boolean containsValue(Object value) {
            return NBTCompound.this.containsValue(value);
        }

        @Override
        public NBT<?> get(Object key) {
            return NBTCompound.this.getNBT(String.valueOf(key));
        }

        @Override
        public @Nullable NBT<?> put(String key, NBT<?> value) {
            NBT<?> previousValue = get(key);
            set(key, value);
            return previousValue;
        }

        @Override
        public NBT<?> remove(Object key) {
            return NBTCompound.this.remove(String.valueOf(key));
        }

        @Override
        public void putAll(@NotNull Map<? extends String, ? extends NBT<?>> m) {
            m.forEach(NBTCompound.this::set);
        }

        @Override
        public void clear() {
            NBTCompound.this.clear();
        }

        @Override
        public @NotNull Set<String> keySet() {
            return NBTCompound.this.keySet();
        }

        @Override
        public @NotNull Collection<NBT<?>> values() {
            return NBTCompound.this.values();
        }

        @Override
        public @NotNull Set<Entry<String, NBT<?>>> entrySet() {
            return NBTCompound.this.entrySet();
        }

    }

    private class UnmodifiableCompoundView extends NBTCompound {

        @Override
        public void write(NBTOutputStream stream) throws IOException {
            NBTCompound.this.write(stream);
        }

        @Override
        public void writeToFile(File file) throws IOException {
            NBTCompound.this.writeToFile(file);
        }

        @Override
        public void writeToFile(File file, boolean compress) throws IOException {
            NBTCompound.this.writeToFile(file, compress);
        }

        @Override
        public void writeRoot(OutputStream stream) throws IOException {
            NBTCompound.this.writeRoot(stream);
        }

        @Override
        public void writeRoot(NBTOutputStream stream) throws IOException {
            NBTCompound.this.writeRoot(stream);
        }

        @Override
        public void writeRoot(OutputStream stream, @Nullable String rootName) throws IOException {
            NBTCompound.this.writeRoot(stream, rootName);
        }

        @Override
        public void writeRoot(NBTOutputStream stream, @Nullable String rootName) throws IOException {
            NBTCompound.this.writeRoot(stream, rootName);
        }

        @Override
        public Tag tag() {
            return NBTCompound.this.tag();
        }

        @Override
        public Map<String, Object> revert() {
            return NBTCompound.this.revert();
        }

        @Override
        public void accept(NBTVisitor visitor) {
            NBTCompound.this.accept(visitor);
        }

        @Override
        public NBTCompound clone() {
            return NBTCompound.this.clone().unmodifiableView();
        }

        @Override
        public int size() {
            return NBTCompound.this.size();
        }

        @Override
        public boolean isEmpty() {
            return NBTCompound.this.isEmpty();
        }

        @Override
        public boolean containsKey(String key) {
            return NBTCompound.this.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return NBTCompound.this.containsValue(value);
        }

        @Override
        public boolean containsTag(Tag tag) {
            return NBTCompound.this.containsTag(tag);
        }

        @Override
        public <T> T getValue(String key, T defaultValue) {
            return NBTCompound.this.getValue(key, defaultValue);
        }

        @Override
        public <T> T getValue(String key) {
            return NBTCompound.this.getValue(key);
        }

        @Override
        public <T> List<T> getList(String key, Extractor<T> extractor) {
            return NBTCompound.this.getList(key, extractor);
        }

        @Override
        public <T> T get(String key, Extractor<T> extractor, T defaultValue) {
            return NBTCompound.this.get(key, extractor, defaultValue);
        }

        @Override
        public <T> T get(String key, Extractor<T> extractor) {
            return NBTCompound.this.get(key, extractor);
        }

        @Override
        public <T extends NBT<?>> T getNBT(String key, T defaultValue) {
            return NBTCompound.this.getNBT(key, defaultValue);
        }

        @Override
        public <T extends NBT<?>> T getNBT(String key) {
            return NBTCompound.this.getNBT(key);
        }

        @Override
        public <T> void setList(String key, Inserter<T> inserter, @Nullable Collection<T> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> void set(String key, Inserter<T> inserter, @Nullable T value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(String key, @Nullable Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NBT<?> remove(String key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull Set<String> keySet() {
            return Collections.unmodifiableSet(NBTCompound.this.keySet());
        }

        @Override
        public @NotNull Collection<NBT<?>> values() {
            return Collections.unmodifiableCollection(NBTCompound.this.values());
        }

        @Override
        public @NotNull Set<Map.Entry<String, NBT<?>>> entrySet() {
            return Collections.unmodifiableSet(NBTCompound.this.entrySet());
        }

        @Override
        public @NotNull Iterator<Map.Entry<String, NBT<?>>> iterator() {
            return new Iterator<>() {
                private final Iterator<Map.Entry<String, NBT<?>>> iterator = NBTCompound.this.iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Map.Entry<String, NBT<?>> next() {
                    return iterator.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super Map.Entry<String, NBT<?>>> action) {
                    iterator.forEachRemaining(action);
                }
            };
        }

        @Override
        public Map<String, NBT<?>> mapView() {
            return Collections.unmodifiableMap(NBTCompound.this.mapView());
        }

        @Override
        public @UnmodifiableView NBTCompound unmodifiableView() {
            return this;
        }

        @Override
        public String toString() {
            return NBTCompound.this.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return NBTCompound.this.equals(obj);
        }

        @Override
        public int hashCode() {
            return NBTCompound.this.hashCode();
        }

        @Override
        public void forEach(Consumer<? super Map.Entry<String, NBT<?>>> action) {
            NBTCompound.this.forEach(action);
        }

        @Override
        public Spliterator<Map.Entry<String, NBT<?>>> spliterator() {
            return NBTCompound.this.spliterator();
        }

        @Override
        public void write(OutputStream stream) throws IOException {
            NBTCompound.this.write(stream);
        }

        @Override
        public boolean softEquals(Object object) {
            return NBTCompound.this.softEquals(object);
        }

    }

}
