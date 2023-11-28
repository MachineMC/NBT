package org.machinemc.nbt;

import org.machinemc.nbt.visitor.NBTStringVisitor;
import org.machinemc.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class NBTString implements NBT<String> {

    private final String string;

    public NBTString(Object object) {
        this(object instanceof String str ? str : String.valueOf(object));
    }

    public NBTString(String string) {
        this.string = string != null ? string : String.valueOf((Object) null);
    }

    @Override
    public Tag tag() {
        return Tag.STRING;
    }

    @Override
    public String revert() {
        return string;
    }

    @Override
    public void accept(NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NBTString clone() {
        return new NBTString(string);
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NBTString other && Objects.equals(string, other.string);
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    public static String quoteAndEscape(String string) {
        char quote = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (quote == c) builder.append('\\');
            else if (quote == 0 && c == '"') quote = '\'';
            else if (quote == 0 && c == '\'') quote = '"';
            builder.append(c);
        }
        if (quote == 0) quote = '"';
        return quote + builder.toString() + quote;
    }

}
