package net.permutated.pylons.util;

import net.minecraft.nbt.CompoundTag;

public class Range {
    private byte[] contents;
    private byte position = 0;

    public Range(byte[] contents) {
        this.contents = contents;
    }

    public void next() {
        this.position = (byte) (++this.position % contents.length);
    }

    public byte get() {
        return contents[position];
    }

    public byte get(byte at) {
        return contents[at % contents.length];
    }

    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.putByteArray(Constants.NBT.CONTENTS, this.contents);
        tag.putByte(Constants.NBT.POSITION, this.position);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        contents = getOrDefault(tag.getByteArray(Constants.NBT.CONTENTS));
        position = tag.getByte(Constants.NBT.POSITION);
    }

    private static byte[] getOrDefault(byte[] test) {
        return test.length > 0 ? test : new byte[]{1};
    }
}
