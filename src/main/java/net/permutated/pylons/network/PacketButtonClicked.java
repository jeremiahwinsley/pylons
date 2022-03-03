package net.permutated.pylons.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
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

    public PacketButtonClicked(PacketBuffer buffer) {
        buttonType = buffer.readEnum(ButtonType.class);
        blockPos = buffer.readBlockPos();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeEnum(this.buttonType);
        buffer.writeBlockPos(this.blockPos);
    }

    @SuppressWarnings("java:S1172")
    public static void handle(PacketButtonClicked event, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Optional<PlayerEntity> player = Optional.ofNullable(ctx.get().getSender());
            Optional<World> world = player.map(PlayerEntity::getCommandSenderWorld);

            if (player.isPresent() && world.get() instanceof ServerWorld && world.get().isLoaded(event.blockPos)) {
                TileEntity be = world.get().getBlockEntity(event.blockPos);
                if (be instanceof AbstractPylonTile) {
                    AbstractPylonTile pylonTile = (AbstractPylonTile) be;
                    if (ButtonType.RANGE.equals(event.buttonType)) {
                        pylonTile.handleRangePacket();
                    } else if (ButtonType.WORK.equals(event.buttonType)) {
                        pylonTile.handleWorkPacket();
                    } else {
                        Pylons.LOGGER.error("PacketButtonClicked called with invalid button type!");
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
