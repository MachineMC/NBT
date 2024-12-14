![banner](.github/assets/nbt_dark.png#gh-dark-mode-only)
![banner](.github/assets/nbt_light.png#gh-light-mode-only)

<p align="center">Java Implementation for Minecraft Java Edition NBT Format.</p>

<p align="center">
    <img src="https://img.shields.io/github/license/machinemc/nbt?style=for-the-badge&color=107185" alt="LICENSE">
    <img src="https://img.shields.io/github/v/release/machinemc/nbt?style=for-the-badge&color=edb228" alt="RELEASE">
</p>

---

This library is Java implementation of the NBT protocol for
Minecraft Java Edition. This library provides functionality
for handling Named Binary Tag (NBT) data, which is a data
interchange format used by Minecraft.

The NBT specification defines _13 different types_ of tags:

| Tag class                                                      | ID | Payload                                                                                                                                                                             |
|----------------------------------------------------------------|----|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [End](nbt-core/src/main/java/org/machinemc/nbt/NBTEnd.java)             | 0  | 0 bytes                                                                                                                                                                             |
| [Byte](nbt-core/src/main/java/org/machinemc/nbt/NBTByte.java)           | 1  | 1 byte / 8 bits, signed                                                                                                                                                             |
| [Short](nbt-core/src/main/java/org/machinemc/nbt/NBTShort.java)         | 2  | 2 bytes / 16 bits, signed, big endian                                                                                                                                               |
| [Int](nbt-core/src/main/java/org/machinemc/nbt/NBTInt.java)             | 3  | 4 bytes / 32 bits, signed, big endian                                                                                                                                               |
| [Long](nbt-core/src/main/java/org/machinemc/nbt/NBTLong.java)           | 4  | 8 bytes / 64 bits, signed, big endian                                                                                                                                               |
| [Float](nbt-core/src/main/java/org/machinemc/nbt/NBTFloat.java)         | 5  | 4 bytes / 32 bits, signed, big endian, IEEE 754-2008, binary32                                                                                                                      |
| [Double](nbt-core/src/main/java/org/machinemc/nbt/NBTDouble.java)       | 6  | 8 bytes / 64 bits, signed, big endian, IEEE 754-2008, binary64                                                                                                                      |
| [ByteArray](nbt-core/src/main/java/org/machinemc/nbt/NBTByteArray.java) | 7  | A signed integer (4 bytes) size, then the bytes comprising an array of length size.                                                                                                 |
| [String](nbt-core/src/main/java/org/machinemc/nbt/NBTString.java)       | 8  | An unsigned short (2 bytes) payload length, then a UTF-8 string resembled by length bytes.                                                                                          |
| [List](nbt-core/src/main/java/org/machinemc/nbt/NBTList.java)           | 9  | A byte denoting the tag ID of the list's contents, followed by the list's length as a signed integer (4 bytes), then length number of payloads that correspond to the given tag ID. |
| [Compound](nbt-core/src/main/java/org/machinemc/nbt/NBTCompound.java)   | 10 | Fully formed tags, followed by a TAG_End.                                                                                                                                           |
| [IntArray](nbt-core/src/main/java/org/machinemc/nbt/NBTIntArray.java)   | 11 | A signed integer size, then size number of TAG_Int's payloads.                                                                                                                      |
| [LongArray](nbt-core/src/main/java/org/machinemc/nbt/NBTLongArray.java) | 12 | A signed integer size, then size number of TAG_Long's payloads.                                                                                                                     |

* Every valid NBT structure starts with a compound tag, known as the root compound.
  All other elements within the NBT structure are nested within this root compound.

* The End tag serves to mark the end of either a compound or an empty list tag within the NBT structure.

### Features

- Comprehensive support for all NBT tags
- Lightweight and intuitive
- Supports new changes introduced in *1.20.2* version of Minecraft
- Supports *SNBT serialization and deserialization*, including boolean types
- Supports compression
- Easily expandable visitor API

#### SNBT Format

Library supports SNBT (Stringified NBT) format defined by Mojang,
utilized to represent NBT tags as human-readable strings. This
is widely used for NBT user input.

Example of SNBT format:

```text
{Fire:10,Leashed:1b,FallFlying:0b,Rotation:[60F,15F],Leash:{X:12,Y:15,Z:1}}
```

### Importing

#### Gradle

```kotlin
repositories {
    maven {
        name = "machinemcRepositoryReleases"
        url = uri("https://repo.machinemc.org/releases")
    }
}

dependencies {
    implementation("org.machinemc:nbt-core:VERSION")

    // for the SNBT parser
    implementation("org.machinemc:nbt-parser:VERSION")
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>machinemc-repository-releases</id>
        <name>MachineMC Repository</name>
        <url>https://repo.machinemc.org/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.machinemc</groupId>
        <artifactId>nbt-core</artifactId>
        <version>VERSION</version>
    </dependency>

    <!-- for the SNBT parser -->
    <dependency>
        <groupId>org.machinemc</groupId>
        <artifactId>nbt-parser</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

### Example usage

The following code snippet shows how to create NBTCompound:

```java
NBTCompound compound = new NBTCompound();
compound.set("hello", "there");
compound.set("test", 10);
compound.set("thing", -5.2);
compound.set("ints", new int[]{1, 2, 3});
```

More examples can be found in the [unit tests](nbt-core/src/test/java/org/machinemc/nbt).

### License
NBT is free software licensed under the [MIT license](LICENCE).
