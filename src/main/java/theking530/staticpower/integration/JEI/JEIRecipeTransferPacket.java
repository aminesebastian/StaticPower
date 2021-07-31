package theking530.staticpower.integration.JEI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.StaticPower;
import theking530.staticpower.network.NetworkMessage;

public class JEIRecipeTransferPacket extends NetworkMessage {
	protected int windowId;
	private ItemStack[][] recipe;
	private CompoundNBT serializedRecipe;
	private int itemCount;

	public JEIRecipeTransferPacket() {

	}

	public JEIRecipeTransferPacket(int windowId, int itemCount, CompoundNBT serializedRecipe) {
		this.windowId = windowId;
		this.itemCount = itemCount;
		this.serializedRecipe = serializedRecipe;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeInt(itemCount);

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed(serializedRecipe, out);
			buffer.writeByteArray(out.toByteArray());
		} catch (Exception e) {
			StaticPower.LOGGER.error("An error occured when attempting to serialize a JEI recipe for display in an IJEIReipceTransferHandler.", e);
		}
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		itemCount = buffer.readInt();

		try {
			byte[] compressedData = buffer.readByteArray();
			serializedRecipe = CompressedStreamTools.readCompressed(new ByteArrayInputStream(compressedData));
			if (serializedRecipe != null) {
				recipe = new ItemStack[itemCount][];
				for (int x = 0; x < this.recipe.length; x++) {
					final ListNBT list = serializedRecipe.getList("#" + x, 10);
					if (list.size() > 0) {
						recipe[x] = new ItemStack[list.size()];
						for (int y = 0; y < list.size(); y++) {
							recipe[x][y] = ItemStack.read(list.getCompound(y));
						}
					}
				}
			}
		} catch (Exception e) {
			StaticPower.LOGGER.error("An error occured when attempting to deserialize a JEI recipe for display in an IJEIReipceTransferHandler.", e);
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			if (player.openContainer.windowId == windowId) {
				if (player.openContainer instanceof IJEIReipceTransferHandler) {
					((IJEIReipceTransferHandler) player.openContainer).consumeJEITransferRecipe(player, recipe);
				}
			}
		});
	}
}
