package theking530.staticpower.tileentities.components.power;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class PacketEnergyStorageComponent extends NetworkMessage {
	private CompoundNBT energyComponentNBT;
	private BlockPos position;
	private String componentName;

	public PacketEnergyStorageComponent() {
	}

	public PacketEnergyStorageComponent(EnergyStorageComponent energyComponent, BlockPos pos, String componentName) {
		energyComponentNBT = new CompoundNBT();
		energyComponent.serializeUpdateNbt(energyComponentNBT, true);
		position = pos;
		this.componentName = componentName;
	}

	@Override
	public void decode(PacketBuffer buf) {
		energyComponentNBT = buf.readCompoundTag();
		position = buf.readBlockPos();
		componentName = readStringOnServer(buf);
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeCompoundTag(energyComponentNBT);
		buf.writeBlockPos(position);
		writeStringOnServer(componentName, buf);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.openContainer == Minecraft.getInstance().player.container) {
				if (Minecraft.getInstance().player.world.isAreaLoaded(position, 1)) {
					TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(position);

					ComponentUtilities.getComponent(EnergyStorageComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
						// Set the mode.
						comp.deserializeUpdateNbt(energyComponentNBT, true);
					});
				}
			}
		});
	}
}
