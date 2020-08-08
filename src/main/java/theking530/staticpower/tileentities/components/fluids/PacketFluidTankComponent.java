package theking530.staticpower.tileentities.components.fluids;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class PacketFluidTankComponent extends NetworkMessage {
	private CompoundNBT fluidComponentNBT;
	private BlockPos position;
	private String componentName;

	public PacketFluidTankComponent() {
	}

	public PacketFluidTankComponent(FluidTankComponent fluidTankComponent, BlockPos pos, String componentName) {
		fluidComponentNBT = new CompoundNBT();
		fluidTankComponent.serializeUpdateNbt(fluidComponentNBT, true);
		position = pos;
		this.componentName = componentName;
	}

	@Override
	public void decode(PacketBuffer buf) {
		fluidComponentNBT = buf.readCompoundTag();
		position = buf.readBlockPos();
		componentName = buf.readString();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeCompoundTag(fluidComponentNBT);
		buf.writeBlockPos(position);
		buf.writeString(componentName);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.openContainer == Minecraft.getInstance().player.container) {
				if (Minecraft.getInstance().player.world.isAreaLoaded(position, 1)) {
					TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(position);

					ComponentUtilities.getComponent(FluidTankComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
						// Set the mode.
						comp.deserializeUpdateNbt(fluidComponentNBT, true);
					});
				}
			}
		});
	}
}
