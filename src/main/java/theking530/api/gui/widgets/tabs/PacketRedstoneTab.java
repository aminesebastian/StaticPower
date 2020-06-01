package theking530.api.gui.widgets.tabs;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.utilities.RedstoneModeList.RedstoneMode;

public class PacketRedstoneTab extends NetworkMessage {
	private RedstoneMode redstoneMode;
	private BlockPos position;

	public PacketRedstoneTab() {
	}

	public PacketRedstoneTab(RedstoneMode mode, BlockPos pos) {
		redstoneMode = mode;
		position = pos;
	}

	@Override
	public void decode(PacketBuffer buf) {
		redstoneMode = RedstoneMode.values()[buf.readInt()];
		position = buf.readBlockPos();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeInt(redstoneMode.ordinal());
		buf.writeBlockPos(position);
	}

	@Override
	public void handle(Supplier<Context> context) {
		TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(position);
		if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
			TileEntityBase tileEntity = (TileEntityBase) rawTileEntity;
			// Ensure this tile entity is valid and has the requested component.
			if (tileEntity.hasComponentOfType(RedstoneControlComponent.class)) {
				// Get a reference to the redstone control component.
				RedstoneControlComponent component = tileEntity.getComponent(RedstoneControlComponent.class);

				// Set the mode.
				component.setRedstoneMode(redstoneMode);
			}
		}
	}
}
