package theking530.staticcore.blockentity.components.fluids;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.world.WorldUtilities;

public class PacketFluidContainerComponent extends NetworkMessage {
	private CompoundTag fluidComponentNBT;
	private BlockPos position;
	private String componentName;
	private int modeOrdinal;

	public PacketFluidContainerComponent() {
	}

	public PacketFluidContainerComponent(FluidContainerInventoryComponent fluidContainerComponent,
			FluidContainerInteractionMode mode, BlockPos pos) {
		fluidComponentNBT = new CompoundTag();
		modeOrdinal = mode.ordinal();
		position = pos;
		this.componentName = fluidContainerComponent.getComponentName();
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		fluidComponentNBT = buf.readNbt();
		position = buf.readBlockPos();
		componentName = buf.readUtf();
		modeOrdinal = buf.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(fluidComponentNBT);
		buf.writeBlockPos(position);
		buf.writeUtf(componentName);
		buf.writeInt(modeOrdinal);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (WorldUtilities.isBlockPosInLoadedChunk(context.get().getSender().level, position)) {
				BlockEntity rawTileEntity = context.get().getSender().level.getBlockEntity(position);

				// Get the component.
				ComponentUtilities.getComponent(FluidContainerInventoryComponent.class, componentName, rawTileEntity)
						.ifPresent(comp -> {
							// Set the mode.
							comp.setMode(FluidContainerInteractionMode.values()[modeOrdinal]);
						});
			}
		});
	}
}
