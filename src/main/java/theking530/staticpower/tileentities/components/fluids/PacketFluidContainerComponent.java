package theking530.staticpower.tileentities.components.fluids;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;

public class PacketFluidContainerComponent extends NetworkMessage {
	private CompoundTag fluidComponentNBT;
	private BlockPos position;
	private String componentName;
	private int modeOrdinal;

	public PacketFluidContainerComponent() {
	}

	public PacketFluidContainerComponent(FluidContainerInventoryComponent fluidContainerComponent, FluidContainerInteractionMode mode, BlockPos pos) {
		fluidComponentNBT = new CompoundTag();
		modeOrdinal = mode.ordinal();
		position = pos;
		this.componentName = fluidContainerComponent.getComponentName();
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		fluidComponentNBT = buf.readNbt();
		position = buf.readBlockPos();
		componentName = readStringOnServer(buf);
		modeOrdinal = buf.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(fluidComponentNBT);
		buf.writeBlockPos(position);
		writeStringOnServer(componentName, buf);
		buf.writeInt(modeOrdinal);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (context.get().getSender().level.isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = context.get().getSender().level.getBlockEntity(position);

				// Get the component.
				ComponentUtilities.getComponent(FluidContainerInventoryComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
					// Set the mode.
					comp.setMode(FluidContainerInteractionMode.values()[modeOrdinal]);
				});
			}
		});
	}
}
