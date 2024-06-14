package net.permutated.pylons.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.machines.base.AbstractPylonTile;
import net.permutated.pylons.util.ResourceUtil;

public record PacketButtonClicked(ButtonType buttonType, BlockPos blockPos) implements CustomPacketPayload {

    public static final Type<PacketButtonClicked> TYPE = new Type<>(ResourceUtil.prefix("button_clicked"));

    public static final StreamCodec<FriendlyByteBuf, PacketButtonClicked> STREAM_CODEC = StreamCodec.of(PacketButtonClicked::encode, PacketButtonClicked::decode);

    private static void encode(FriendlyByteBuf buf, PacketButtonClicked packetButtonClicked) {
        buf.writeEnum(packetButtonClicked.buttonType);
        buf.writeBlockPos(packetButtonClicked.blockPos);
    }

    private static PacketButtonClicked decode(FriendlyByteBuf buf) {
        return new PacketButtonClicked(buf.readEnum(ButtonType.class), buf.readBlockPos());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SuppressWarnings("java:S1172")
    public static void handle(final PacketButtonClicked event, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player().getCommandSenderWorld() instanceof ServerLevel serverLevel && serverLevel.isLoaded(event.blockPos)) {
                var be = serverLevel.getBlockEntity(event.blockPos);
                if (be instanceof AbstractPylonTile pylonTile) {
                    switch (event.buttonType) {
                        case RANGE -> pylonTile.handleRangePacket();
                        case WORK -> pylonTile.handleWorkPacket();
                        default -> Pylons.LOGGER.error("PacketButtonClicked called with invalid button type!");
                    }
                }
            }
        });
    }

    public enum ButtonType {
        RANGE, WORK
    }
}
