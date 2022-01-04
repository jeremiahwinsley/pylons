package net.permutated.pylons.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.tile.AbstractPylonTile;

import java.util.Optional;
import java.util.function.Supplier;

public class PacketButtonClicked {
    private final ButtonType buttonType;
    private final BlockPos blockPos;

    public PacketButtonClicked(ButtonType buttonType, BlockPos blockPos) {
        this.buttonType = buttonType;
        this.blockPos = blockPos;
    }

    public PacketButtonClicked(FriendlyByteBuf buffer) {
        buttonType = buffer.readEnum(ButtonType.class);
        blockPos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.buttonType);
        buffer.writeBlockPos(this.blockPos);
    }

    @SuppressWarnings("java:S1172")
    public static void handle(PacketButtonClicked event, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Optional<Player> player = Optional.ofNullable(ctx.get().getSender());
            Optional<Level> world = player.map(Player::getCommandSenderWorld);

            if (player.isPresent() && world.get() instanceof ServerLevel serverLevel && serverLevel.isLoaded(event.blockPos)) {
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
        ctx.get().setPacketHandled(true);
    }

    public enum ButtonType {
        RANGE, WORK
    }
}
