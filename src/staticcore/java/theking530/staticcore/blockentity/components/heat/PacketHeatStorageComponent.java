package theking530.staticcore.blockentity.components.heat;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.network.NetworkMessage;

public class PacketHeatStorageComponent extends NetworkMessage {
	private CompoundTag heatComponentNBT;
	private BlockPos position;
	private String componentName;

	public PacketHeatStorageComponent() {
	}

	public PacketHeatStorageComponent(HeatStorageComponent heatComponent, BlockPos pos, String componentName) {
		heatComponentNBT = new CompoundTag();
		heatComponent.serializeUpdateNbt(heatComponentNBT, true);
		position = pos;
		this.componentName = componentName;
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		heatComponentNBT = buf.readNbt();
		position = buf.readBlockPos();
		componentName = buf.readUtf();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(heatComponentNBT);
		buf.writeBlockPos(position);
		buf.writeUtf(componentName);
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
