package org.machinemc.nbt.visitor;

import org.machinemc.nbt.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class NBTStringVisitor implements NBTVisitor {

    private static final Pattern SIMPLE_VALUE = Pattern.compile("[\\dA-Za-z_\\-.+]+");

    private final StringBuilder builder = new StringBuilder();

    public String visitNBT(NBT nbt) {
        nbt.accept(this);
        return toString();
    }

    @Override
    public void visit(NBTString nbtString) {
        builder.append(NBTString.quoteAndEscape(nbtString.value()));
    }

    @Override
    public void visit(NBTByte nbtByte) {
        builder.append(nbtByte.value()).append('b');
    }

    @Override
    public void visit(NBTShort nbtShort) {
        builder.append(nbtShort.value()).append('s');
    }

    @Override
    public void visit(NBTInt nbtInt) {
        builder.append(nbtInt.value());
    }

    @Override
    public void visit(NBTLong nbtLong) {
        builder.append(nbtLong.value()).append('L');
    }

    @Override
    public void visit(NBTFloat nbtFloat) {
        builder.append(nbtFloat.value()).append('f');
    }

    @Override
    public void visit(NBTDouble nbtDouble) {
        builder.append(nbtDouble.value()).append('d');
    }

    @Override
    public void visit(NBTByteArray nbtByteArray) {
        builder.append("[B;");
        byte[] bytes = nbtByteArray.value();

        for (int i = 0; i < bytes.length; i++) {
            if (i != 0)
                builder.append(',');

            builder.append(bytes[i]).append('B');
        }

        builder.append(']');
    }

    @Override
    public void visit(NBTIntArray nbtIntArray) {
        builder.append("[I;");
        int[] ints = nbtIntArray.value();

        for (int i = 0; i < ints.length; i++) {
            if (i != 0)
                builder.append(',');

            builder.append(ints[i]);
        }

        builder.append(']');
    }

    @Override
    public void visit(NBTLongArray nbtLongArray) {
        builder.append("[L;");
        long[] longs = nbtLongArray.value();

        for (int i = 0; i < longs.length; i++) {
            if (i != 0)
                builder.append(',');

            builder.append(longs[i]).append('L');
        }

        builder.append(']');
    }

    @Override
    public void visit(NBTList nbtList) {
        builder.append('[');

        for (int i = 0; i < nbtList.size(); i++) {
            if (i != 0)
                builder.append(',');

            builder.append(new NBTStringVisitor().visitNBT(nbtList.get(i)));
        }

        builder.append(']');
    }

    @Override
    public void visit(NBTCompound nbtCompound) {
        builder.append('{');
        List<String> keys = new ArrayList<>(nbtCompound.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (i != 0)
                builder.append(',');

            builder.append(handleEscape(key)).append(':');
            builder.append(new NBTStringVisitor().visitNBT(nbtCompound.get((Object) key)));
        }

        builder.append('}');
    }

    @Override
    public void visit(NBTEnd nbtEnd) {}

    @Override
    public String toString() {
        return builder.toString();
    }

    public void clear() {
        builder.delete(0, builder.length() - 1);
    }

    private static String handleEscape(String string) {
        return SIMPLE_VALUE.matcher(string).matches() ? string : NBTString.quoteAndEscape(string);
    }

}
