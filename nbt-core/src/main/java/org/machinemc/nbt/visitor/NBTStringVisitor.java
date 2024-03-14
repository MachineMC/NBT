package org.machinemc.nbt.visitor;

import org.machinemc.nbt.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NBTStringVisitor implements NBTVisitor {

    private final StringBuilder builder = new StringBuilder();

    public String visitNBT(NBT<?> nbt) {
        nbt.accept(this);
        return toString();
    }

    @Override
    public void visit(NBTString nbtString) {
        builder.append(NBTString.quoteAndEscape(nbtString.revert()));
    }

    @Override
    public void visit(NBTByte nbtByte) {
        builder.append(nbtByte.revert()).append('b');
    }

    @Override
    public void visit(NBTShort nbtShort) {
        builder.append(nbtShort.revert()).append('s');
    }

    @Override
    public void visit(NBTInt nbtInt) {
        builder.append(nbtInt.revert());
    }

    @Override
    public void visit(NBTLong nbtLong) {
        builder.append(nbtLong.revert()).append('L');
    }

    @Override
    public void visit(NBTFloat nbtFloat) {
        builder.append(nbtFloat.revert()).append('f');
    }

    @Override
    public void visit(NBTDouble nbtDouble) {
        builder.append(nbtDouble.revert()).append('d');
    }

    @Override
    public void visit(NBTByteArray nbtByteArray) {
        builder.append("[B;");
        byte[] bytes = nbtByteArray.revert();

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
        int[] ints = nbtIntArray.revert();

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
        long[] longs = nbtLongArray.revert();

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
            builder.append(new NBTStringVisitor().visitNBT(nbtCompound.getNBT(key)));
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
        return isSimpleValue(string) ? string : NBTString.quoteAndEscape(string);
    }

    private static boolean isSimpleValue(String string) {
        if (string.isEmpty()) return false;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            switch (c) {
                case '_', '-', '.', '+' -> {}
                default -> {
                    if (('0' > c || c > '9') && ('a' > c || c > 'z') && ('A' > c || c > 'Z')) return false;
                }
            }
        }
        return true;
    }

}
