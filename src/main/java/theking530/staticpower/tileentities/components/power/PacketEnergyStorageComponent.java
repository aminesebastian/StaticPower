package theking530.staticpower.tileentities.components.power;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class PacketEnergyStorageComponent extends NetworkMessage {
	private CompoundTag energyComponentNBT;
	private BlockPos position;
	private String componentName;

	public PacketEnergyStorageComponent() {
	}

	public PacketEnergyStorageComponent(EnergyStorageComponent energyComponent, BlockPos pos, String componentName) {
		energyComponentNBT = new CompoundTag();
		energyComponent.serializeUpdateNbt(energyComponentNBT, true);
		position = pos;
		this.componentName = componentName;
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		energyComponentNBT = buf.readNbt();
		position = buf.readBlockPos();
		componentName = readStringOnServer(buf);
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(energyComponentNBT);
		buf.writeBlockPos(position);
		writeStringOnServer(componentName, buf);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.containerMenu == Minecraft.getInstance().player.inventoryMenu) {
				if (Minecraft.getInstance().player.level.isAreaLoaded(position, 1)) {
					BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(position);

					ComponentUtilities.getComponent(EnergyStorageComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
						// Set the mode.
						comp.deserializeUpdateNbt(energyComponentNBT, true);
					});
				}
			}
		});
	}
}
