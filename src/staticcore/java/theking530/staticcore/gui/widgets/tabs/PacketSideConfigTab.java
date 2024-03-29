package theking530.staticcore.gui.widgets.tabs;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.world.WorldUtilities;

public class PacketSideConfigTab extends NetworkMessage {
	private MachineSideMode[] configuration;
	private BlockPos position;

	public PacketSideConfigTab() {
	}

	public PacketSideConfigTab(MachineSideMode[] sideModes, BlockPos pos) {
		configuration = sideModes;
		position = pos;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(position);

		// Convert the configuration to an array of ordinals.
		int[] configOrdinals = new int[configuration.length];
		for (int i = 0; i < configOrdinals.length; i++) {
			configOrdinals[i] = configuration[i].ordinal();
		}
		// Write the ordinals to the buffer.
		buf.writeVarIntArray(configOrdinals);
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		position = buf.readBlockPos();

		// Get the config ordinals.
		int[] configOrdinals = buf.readVarIntArray();

		// Create the configuration array.
		configuration = new MachineSideMode[configOrdinals.length];

		// Populate the values in the configuration array.
		for (int i = 0; i < configOrdinals.length; i++) {
			configuration[i] = MachineSideMode.values()[configOrdinals[i]];
		}
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (WorldUtilities.isBlockPosInLoadedChunk(context.get().getSender().level, position)) {
				BlockEntity te = context.get().getSender().level.getBlockEntity(position);

				if (te instanceof BlockEntityBase) {
					BlockEntityBase tileEntity = (BlockEntityBase) te;
					if (!tileEntity.hasComponentOfType(SideConfigurationComponent.class)) {
						return;
					}

					SideConfigurationComponent sideComp = tileEntity.getComponent(SideConfigurationComponent.class);
					for (Direction side : Direction.values()) {
						sideComp.setWorldSpaceDirectionConfiguration(side, configuration[side.ordinal()]);
					}
				}
			}
		});
	}
}
