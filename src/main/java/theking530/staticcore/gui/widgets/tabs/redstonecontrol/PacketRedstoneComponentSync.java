package theking530.staticcore.gui.widgets.tabs.redstonecontrol;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;

public class PacketRedstoneComponentSync extends NetworkMessage {
	private RedstoneMode redstoneMode;
	private BlockPos position;
	private String componentName;

	public PacketRedstoneComponentSync() {
	}

	public PacketRedstoneComponentSync(RedstoneMode mode, BlockPos pos, String componentName) {
		redstoneMode = mode;
		position = pos;
		this.componentName = componentName;
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		redstoneMode = RedstoneMode.values()[buf.readInt()];
		position = buf.readBlockPos();
		componentName = readStringOnServer(buf);
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(redstoneMode.ordinal());
		buf.writeBlockPos(position);
		writeStringOnServer(componentName, buf);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			BlockEntity rawTileEntity = context.get().getSender().level.getBlockEntity(position);

			if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
				TileEntityBase tileEntity = (TileEntityBase) rawTileEntity;

				// Ensure this tile entity is valid and has the requested component.
				if (tileEntity.hasComponentOfType(RedstoneControlComponent.class)) {
					// Get a reference to the redstone control component.
					RedstoneControlComponent component = tileEntity.getComponent(RedstoneControlComponent.class, componentName);

					// Set the mode.
					component.setRedstoneMode(redstoneMode);
				}
			}
		});
	}
}
