package theking530.staticpower.tileentities.nonpowered.solderingtable;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.utilities.ItemUtilities;

public class PacketSyncSolderingFakeSlotRecipe extends NetworkMessage {
	protected int windowId;
	private ItemStack[] recipe;

	public PacketSyncSolderingFakeSlotRecipe() {

	}

	public PacketSyncSolderingFakeSlotRecipe(int windowId, ItemStack[] recipe) {
		this.windowId = windowId;
		this.recipe = recipe;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeByte(this.windowId);
		buffer.writeShort(this.recipe.length);

		for (ItemStack itemstack : this.recipe) {
			ItemUtilities.writeLargeStackItemToBuffer(itemstack, false, buffer);
		}

	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		this.windowId = buffer.readUnsignedByte();
		int i = buffer.readShort();
		this.recipe = new ItemStack[i];

		for (int j = 0; j < i; j++) {
			this.recipe[j] = ItemUtilities.readLargeStackItemFromBuffer(buffer);
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			// Get the sender and ensure the windows are the same.
			ServerPlayer sender = ctx.get().getSender();
			if (sender.containerMenu.containerId == windowId && sender.containerMenu instanceof AbstractContainerSolderingTable) {
				// The the container.
				@SuppressWarnings("unchecked")
				AbstractContainerSolderingTable<AbstractSolderingTable> container = (AbstractContainerSolderingTable<AbstractSolderingTable>) sender.containerMenu;

				// Update the recipe.
				for (int i = 0; i < 9; i++) {
					container.getTileEntity().patternInventory.setStackInSlot(i, recipe[i]);
				}
			}
		});
	}
}
