package mx.kenzie.nbt.visitor;

import mx.kenzie.nbt.*;

public interface NBTVisitor {

    void visit(NBTString nbtString);

    void visit(NBTByte nbtByte);

    void visit(NBTShort nbtShort);

    void visit(NBTInt nbtInt);

    void visit(NBTLong nbtLong);

    void visit(NBTFloat nbtFloat);

    void visit(NBTDouble nbtDouble);

    void visit(NBTByteArray nbtByteArray);

    void visit(NBTIntArray nbtIntArray);

    void visit(NBTLongArray nbtLongArray);

    void visit(NBTList nbtList);

    void visit(NBTCompound nbtCompound);

    void visit(NBTEnd nbtEnd);

}
