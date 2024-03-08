package org.machinemc.nbt;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class NBTReaderTest {

    @Test
    public void bigtest() throws IOException {
        URL resource = getResource("bigtest.nbt");
        try (InputStream stream = resource.openStream()) {
            NBTCompound compound = NBTCompound.readRootCompound(stream);
            assert Objects.equals(compound.getValue("shortTest"), (short) 32767);
            assert Objects.equals(compound.getValue("longTest"), 9223372036854775807L);
            assert Objects.equals(compound.getValue("byteTest"), (byte) 127);
            byte[] byteArray = new byte[1000];
            for (int n = 0; n < byteArray.length; n++)
                byteArray[n] = (byte) ((n * n * 255 + n * 7) % 100);
            assert Arrays.equals(compound.getValue("byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8, ...))"), byteArray);
            assert Objects.equals(compound.getValue("listTest (long)"), List.of(11L, 12L, 13L, 14L, 15L));
            assert Objects.equals(compound.getValue("floatTest"), 0.49823147f);
            assert Objects.equals(compound.getValue("doubleTest"), 0.4931287132182315);
            assert Objects.equals(compound.getValue("intTest"), 2147483647);
            assert Objects.equals(compound.getValue("listTest (compound)"), List.of(
                    Map.of(
                            "created-on", 1264099775885L,
                            "name", "Compound tag #0"
                    ),
                    Map.of(
                            "created-on", 1264099775885L,
                            "name", "Compound tag #1"
                    )
            ));
            assert Objects.equals(compound.getValue("nested compound test"), Map.of(
                    "egg", Map.of(
                            "name", "Eggbert",
                            "value", 0.5f
                    ),
                    "ham", Map.of(
                            "name", "Hampus",
                            "value", 0.75f
                    )
            ));
            assert Objects.equals(compound.getValue("stringTest"), "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!");
        }
    }

    @Test
    public void helloWorld() throws IOException {
        URL resource = getResource("hello_world.nbt");
        try (InputStream stream = resource.openStream()) {
            NBTCompound compound = NBTCompound.readRootCompound(stream);
            assert compound.getValue("name", "foo").equals("Bananrama");
        }
    }

    @Test
    public void playerNanValue() throws IOException {
        URL resource = getResource("Player-nan-value.dat");
        try (InputStream stream = resource.openStream()) {
            NBTCompound compound = NBTCompound.readRootCompound(stream);
            assert Objects.equals(compound.getValue("Motion"), List.of(0d, 0d, 0d));
            assert Objects.equals(compound.getValue("FallDistance"), 0f);
            assert Objects.equals(compound.getValue("Pos"), List.of(0d, Double.NaN, 0d));
            assert Objects.equals(compound.getValue("Health"), (short) 20);
            assert Objects.equals(compound.getValue("DeathTime"), (short) 0);
            assert Objects.equals(compound.getValue("Fire"), (short) -20);
            assert Objects.equals(compound.getValue("Air"), (short) 300);
            assert Objects.equals(compound.getValue("OnGround"), (byte) 1);
            assert Objects.equals(compound.getValue("HurtTime"), (short) 0);
            assert Objects.equals(compound.getValue("Rotation"), List.of(164f, -63f));
            assert Objects.equals(compound.getValue("AttackTime"), (short) 0);
            assert Objects.equals(compound.getValue("Inventory"), Collections.emptyList());
        }
    }

    private static URL getResource(String name) {
        URL resource = NBTReaderTest.class.getProtectionDomain().getClassLoader().getResource(name);
        assert resource != null : '\'' + name + "' not found";
        return resource;
    }

}
