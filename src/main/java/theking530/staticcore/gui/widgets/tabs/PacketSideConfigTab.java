package theking530.staticcore.gui.widgets.tabs;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;

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
	public void encode(PacketBuffer buf) {
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
	public void decode(PacketBuffer buf) {
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
			TileEntity te = context.get().getSender().world.getTileEntity(position);

			if (te instanceof TileEntityBase) {
				TileEntityBase tileEntity = (TileEntityBase) te;
				if (!tileEntity.hasComponentOfType(SideConfigurationComponent.class)) {
					return;
				}
				SideConfigurationComponent sideComp = tileEntity.getComponent(SideConfigurationComponent.class);
				sideComp.setWorldSpaceConfiguration(configuration);
			}
		});
	}
}
