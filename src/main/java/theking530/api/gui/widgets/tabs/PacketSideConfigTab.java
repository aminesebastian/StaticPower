package theking530.api.gui.widgets.tabs;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ISideConfigurable;

public class PacketSideConfigTab extends NetworkMessage {
	private MachineSideMode side0;
	private MachineSideMode side1;
	private MachineSideMode side2;
	private MachineSideMode side3;
	private MachineSideMode side4;
	private MachineSideMode side5;
	private BlockPos position;

	public PacketSideConfigTab() {
	}

	public PacketSideConfigTab(MachineSideMode[] sideModes, BlockPos pos) {
		side0 = sideModes[0];
		side1 = sideModes[1];
		side2 = sideModes[2];
		side3 = sideModes[3];
		side4 = sideModes[4];
		side5 = sideModes[5];
		this.position = pos;
	}

	@Override
	public void decode(PacketBuffer buf) {
		// the order is important
		this.side0 = MachineSideMode.values()[buf.readInt()];
		this.side1 = MachineSideMode.values()[buf.readInt()];
		this.side2 = MachineSideMode.values()[buf.readInt()];
		this.side3 = MachineSideMode.values()[buf.readInt()];
		this.side4 = MachineSideMode.values()[buf.readInt()];
		this.side5 = MachineSideMode.values()[buf.readInt()];

		position = buf.readBlockPos();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeInt(side0.ordinal());
		buf.writeInt(side1.ordinal());
		buf.writeInt(side2.ordinal());
		buf.writeInt(side3.ordinal());
		buf.writeInt(side4.ordinal());
		buf.writeInt(side5.ordinal());
		buf.writeBlockPos(position);
	}

	@Override
	public void handle(Supplier<Context> context) {
		TileEntity te = Minecraft.getInstance().player.world.getTileEntity(position);
		if (te != null && te instanceof ISideConfigurable) {
			ISideConfigurable entity = (ISideConfigurable) te;
			entity.getSideConfigurations()[0] = side0;
			entity.getSideConfigurations()[1] = side1;
			entity.getSideConfigurations()[2] = side2;
			entity.getSideConfigurations()[3] = side3;
			entity.getSideConfigurations()[4] = side4;
			entity.getSideConfigurations()[5] = side5;
		}
	}
}
