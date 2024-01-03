package org.machinemc.nbt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.machinemc.nbt.exceptions.NBTException;
import org.machinemc.nbt.io.NBTOutputStream;
import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.*;
import java.util.*;

public class NBTCompound implements NBT<Map<String, Object>>, Map<String, NBT<?>>, Iterable<Map.Entry<String, NBT<?>>> {

    private final Map<String, NBT<?>> map;

    public NBTCompound() {
        this(new HashMap<>());
    }

    public NBTCompound(Map<?, ?> map) {
        this.map = new HashMap<>();
        if (map instanceof NBTCompound compound) putAll(compound);
        else map.forEach((key, value) -> set(key + "", value));
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
        writeRoot(stream, rootName, false);
    }

    public void writeRoot(OutputStream stream, @Nullable String rootName, boolean compress) throws IOException {
        new NBTOutputStream(stream, compress).writeRootCompound(this, rootName);
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

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) return false;
        return map.containsKey(key);
    }

    @Override
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
        return NBT.revert(get(key));
    }

    public <T> List<T> getList(String key, Extractor<T> extractor) {
        if (!(get(key) instanceof NBTList nbtList)) return null;
        List<T> list = new ArrayList<>(nbtList.size());
        for (NBT<?> nbt : nbtList) {
            if (!(nbt instanceof NBTCompound compound)) throw new NBTException("List contains a non-compound element");
            list.add(extractor.extract(compound));
        }
        return list;
    }

    public <T> T get(String key, Extractor<T> extractor) {
        return get(key) instanceof NBTCompound compound ? extractor.extract(compound) : null;
    }

    public <T> T get(String key, Extractor<T> extractor, T defaultValue) {
        return get(key) instanceof NBTCompound compound ? extractor.extract(compound, defaultValue) : defaultValue;
    }

    public <T extends NBT<?>> T get(String key, T defaultValue) {
        T value = get(key);
        return value != null ? value : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T extends NBT<?>> T get(String key) {
        return (T) map.get(key);
    }

    @Override
    @Deprecated
    public NBT<?> get(Object key) {
        return map.get(key);
    }

    public void set(String key, Object value) {
        if (value == null) {
            remove(key);
            return;
        }
        put(key, NBT.convert(value));
    }

    public <T> void set(String key, Inserter<T> inserter, T value) {
        if (value == null) {
            remove(key);
            return;
        }
        NBTCompound compound = new NBTCompound();
        inserter.insert(compound, value);
        set(key, compound);
    }

    @SafeVarargs
    public final <T> void setList(String key, Inserter<T> inserter, T... values) {
        setList(key, inserter, values != null ? List.of(values) : null);
    }

    public <T> void setList(String key, Inserter<T> inserter, Collection<T> values) {
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

    @Override
    public @Nullable NBT<?> put(String key, NBT<?> value) {
        check(key, value);
        return map.put(key, value);
    }

    @Override
    public NBT<?> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends NBT<?>> m) {
        map.forEach(this::put);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public @NotNull Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public @NotNull Collection<NBT<?>> values() {
        return map.values();
    }

    @Override
    public @NotNull Set<Entry<String, NBT<?>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public @NotNull Iterator<Entry<String, NBT<?>>> iterator() {
        return map.entrySet().iterator();
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
        if (key == null) {
            throw new NBTException("Compounds cannot have null keys");
        } else if (value == null) {
            throw new NBTException("Compounds cannot have null values");
        } else if (value instanceof NBT<?> nbt && nbt.tag() == Tag.END) {
            throw new NBTException(Tag.END.getTypeName() + " cannot be used as a value");
        }
    }

    @SafeVarargs
    public static NBTCompound ofEntries(Entry<String, NBT<?>>... entries) {
        NBTCompound nbtCompound = new NBTCompound();
        for (Entry<String, NBT<?>> entry : entries) nbtCompound.put(entry.getKey(), entry.getValue());
        return nbtCompound;
    }

}
