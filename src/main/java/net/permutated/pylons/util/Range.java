package net.permutated.pylons.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.nio.ByteBuffer;

public class Range {
    private static final Codec<byte[]> BYTE_ARRAY_CODEC = Codec.BYTE_BUFFER.xmap(ByteBuffer::array, ByteBuffer::wrap);
    public static final Codec<Range> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BYTE_ARRAY_CODEC.fieldOf(Constants.NBT.CONTENTS).forGetter(Range::contents),
            Codec.BYTE.fieldOf(Constants.NBT.POSITION).forGetter(Range::position)
        ).apply(instance, Range::new));
    }

    private byte[] contents;
    private byte position = 0;

    public Range(byte[] contents, byte position) {
        this.contents = contents;
        this.position = position;
    }

    public Range(byte[] contents) {
        this.contents = contents;
    }

    private byte position() {
        return position;
    }

    private byte[] contents() {
        return contents;
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

    public byte max() {
        return contents[contents.length - 1];
    }

    public int toRadius() {
        return (contents[position] - 1) / 2;
    }

    public void serialize(ValueOutput output) {
        output.store(Constants.NBT.RANGE, CODEC, this);
    }

    public void deserialize(ValueInput input) {
        input.read(Constants.NBT.RANGE, CODEC).ifPresent(range -> {
            this.contents = range.contents;
            this.position = range.position;
        });
    }
}
