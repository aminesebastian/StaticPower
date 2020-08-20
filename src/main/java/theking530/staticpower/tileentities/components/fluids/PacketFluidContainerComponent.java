package theking530.staticpower.tileentities.components.fluids;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.fluids.FluidContainerInventoryComponent.FluidContainerInteractionMode;

public class PacketFluidContainerComponent extends NetworkMessage {
	private CompoundNBT fluidComponentNBT;
	private BlockPos position;
	private String componentName;
	private int modeOrdinal;

	public PacketFluidContainerComponent() {
	}

	public PacketFluidContainerComponent(FluidContainerInventoryComponent fluidContainerComponent, FluidContainerInteractionMode mode, BlockPos pos) {
		fluidComponentNBT = new CompoundNBT();
		modeOrdinal = mode.ordinal();
		position = pos;
		this.componentName = fluidContainerComponent.getComponentName();
	}

	@Override
	public void decode(PacketBuffer buf) {
		fluidComponentNBT = buf.readCompoundTag();
		position = buf.readBlockPos();
		componentName = buf.readString();
		modeOrdinal = buf.readInt();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeCompoundTag(fluidComponentNBT);
		buf.writeBlockPos(position);
		buf.writeString(componentName);
		buf.writeInt(modeOrdinal);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (context.get().getSender().world.isAreaLoaded(position, 1)) {
				TileEntity rawTileEntity = context.get().getSender().world.getTileEntity(position);

				// Get the component.
				ComponentUtilities.getComponent(FluidContainerInventoryComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
					// Set the mode.
					comp.setMode(FluidContainerInteractionMode.values()[modeOrdinal]);
				});
			}
		});
	}
}
