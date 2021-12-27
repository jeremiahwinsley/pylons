package net.permutated.pylons.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.permutated.pylons.tile.AbstractPylonTile;

import java.util.Optional;
import java.util.function.Supplier;

public class PacketButtonClicked {
    private final BlockPos blockPos;

    public PacketButtonClicked(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public PacketButtonClicked(FriendlyByteBuf buffer) {
        blockPos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
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
                    pylonTile.handleRangePacket();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
