package theking530.staticpower.cables.attachments.digistore.craftingterminal;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.utilities.ItemUtilities;

public class PacketRestorePreviousCraftingRecipe extends NetworkMessage {
	protected int windowId;
	private ItemStack[] itemStacks;

	public PacketRestorePreviousCraftingRecipe() {

	}

	public PacketRestorePreviousCraftingRecipe(int windowId, ItemStack[] itemStacks) {
		this.windowId = windowId;
		this.itemStacks = itemStacks;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeByte(this.windowId);
		buffer.writeShort(this.itemStacks.length);

		for (ItemStack itemstack : this.itemStacks) {
			ItemUtilities.writeLargeStackItemToBuffer(itemstack, false, buffer);
		}

	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		this.windowId = buffer.readUnsignedByte();
		int i = buffer.readShort();
		this.itemStacks = new ItemStack[i];

		for (int j = 0; j < i; j++) {
			this.itemStacks[j] = ItemUtilities.readLargeStackItemFromBuffer(buffer);
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			// Get the sender and ensure the windows are the same.
			ServerPlayer sender = ctx.get().getSender();
			if (sender.containerMenu.containerId == windowId) {
				// Allocate a structure for the recipe and transform the array we have into the
				// 2D array the method expects.
				ItemStack[][] recipe = new ItemStack[itemStacks.length][1];
				for (int i = 0; i < itemStacks.length; i++) {
					recipe[i][0] = itemStacks[i];
				}
				// Consume the recipe.
				((ContainerDigistoreCraftingTerminal) sender.containerMenu).consumeJEITransferRecipe(sender, recipe);
			}
		});
	}
}
