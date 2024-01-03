package org.machinemc.nbt.io;

import org.machinemc.nbt.NBTCompound;

import java.io.*;

public final class NBTReader {

    private NBTReader() {
        throw new UnsupportedOperationException();
    }

    public static NBTCompound readFile(File file) throws IOException {
        return readFile(file, false);
    }

    public static NBTCompound readFile(File file, boolean hasRootKey) throws IOException {
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

}
