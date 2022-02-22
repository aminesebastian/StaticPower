package theking530.staticpower.tileentities.components.heat;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class PacketHeatStorageComponent extends NetworkMessage {
	private CompoundTag heatComponentNBT;
	private BlockPos position;
	private String componentName;

	public PacketHeatStorageComponent() {
	}

	public PacketHeatStorageComponent(HeatStorageComponent energyComponent, BlockPos pos, String componentName) {
		heatComponentNBT = new CompoundTag();
		energyComponent.serializeUpdateNbt(heatComponentNBT, true);
		position = pos;
		this.componentName = componentName;
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		heatComponentNBT = buf.readNbt();
		position = buf.readBlockPos();
		componentName = readStringOnServer(buf);
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(heatComponentNBT);
		buf.writeBlockPos(position);
		writeStringOnServer(componentName, buf);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.containerMenu == Minecraft.getInstance().player.inventoryMenu) {
				if (Minecraft.getInstance().player.level.isAreaLoaded(position, 1)) {
					BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(position);

					ComponentUtilities.getComponent(HeatStorageComponent.class, componentName, rawTileEntity).ifPresent(comp -> {
						// Set the mode.
						comp.deserializeUpdateNbt(heatComponentNBT, true);
					});
				}
			}
		});
	}
}
