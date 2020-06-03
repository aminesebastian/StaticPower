package theking530.api.gui.widgets.tabs;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

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
		TileEntity te = context.get().getSender().world.getTileEntity(position);

		if (te instanceof TileEntityBase) {
			TileEntityBase tileEntity = (TileEntityBase) te;
			if (!tileEntity.hasComponentOfType(SideConfigurationComponent.class)) {
				return;
			}
			SideConfigurationComponent sideComp = tileEntity.getComponent(SideConfigurationComponent.class);
			sideComp.setWorldSpaceDirectionConfiguration(Direction.DOWN, side0);
			sideComp.setWorldSpaceDirectionConfiguration(Direction.UP, side1);
			sideComp.setWorldSpaceDirectionConfiguration(Direction.NORTH, side2);
			sideComp.setWorldSpaceDirectionConfiguration(Direction.SOUTH, side3);
			sideComp.setWorldSpaceDirectionConfiguration(Direction.WEST, side4);
			sideComp.setWorldSpaceDirectionConfiguration(Direction.EAST, side5);
		}
	}
}
