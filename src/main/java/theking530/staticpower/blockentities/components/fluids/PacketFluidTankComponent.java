package theking530.staticpower.blockentities.components.fluids;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.blockentities.components.ComponentUtilities;

public class PacketFluidTankComponent extends NetworkMessage {
	private CompoundTag fluidComponentNBT;
	private BlockPos position;
	private String componentName;

	public PacketFluidTankComponent() {
	}

	public PacketFluidTankComponent(FluidTankComponent fluidTankComponent, BlockPos pos, String componentName) {
		fluidComponentNBT = new CompoundTag();
		fluidTankComponent.serializeUpdateNbt(fluidComponentNBT, true);
		position = pos;
		this.componentName = componentName;
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		fluidComponentNBT = buf.readNbt();
		position = buf.readBlockPos();
		componentName =buf.readUtf();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(fluidComponentNBT);
		buf.writeBlockPos(position);
		buf.writeUtf(componentName);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.containerMenu == Minecraft.getInstance().player.inventoryMenu) {
				if (Minecraft.getInstance().player.level.isAreaLoaded(position, 1)) {
					BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(position);

					ComponentUtilities.getComponent(FluidTankComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
						// Set the mode.
						comp.deserializeUpdateNbt(fluidComponentNBT, true);
					});
				}
			}
		});
	}
}
