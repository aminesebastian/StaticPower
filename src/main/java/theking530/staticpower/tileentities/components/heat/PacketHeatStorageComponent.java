package theking530.staticpower.tileentities.components.heat;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class PacketHeatStorageComponent extends NetworkMessage {
	private CompoundNBT heatComponentNBT;
	private BlockPos position;
	private String componentName;

	public PacketHeatStorageComponent() {
	}

	public PacketHeatStorageComponent(HeatStorageComponent energyComponent, BlockPos pos, String componentName) {
		heatComponentNBT = new CompoundNBT();
		energyComponent.serializeUpdateNbt(heatComponentNBT, true);
		position = pos;
		this.componentName = componentName;
	}

	@Override
	public void decode(PacketBuffer buf) {
		heatComponentNBT = buf.readCompoundTag();
		position = buf.readBlockPos();
		componentName = readStringOnServer(buf);
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeCompoundTag(heatComponentNBT);
		buf.writeBlockPos(position);
		writeStringOnServer(componentName, buf);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.openContainer == Minecraft.getInstance().player.container) {
				if (Minecraft.getInstance().player.world.isAreaLoaded(position, 1)) {
					TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(position);

					ComponentUtilities.getComponent(HeatStorageComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
						// Set the mode.
						comp.deserializeUpdateNbt(heatComponentNBT, true);
					});
				}
			}
		});
	}
}
