package theking530.staticpower.integration.JEI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.StaticPower;

public class JEIRecipeTransferPacket extends NetworkMessage {
	protected int windowId;
	private ItemStack[][] recipe;
	private CompoundTag serializedRecipe;
	private int itemCount;

	public JEIRecipeTransferPacket() {

	}

	public JEIRecipeTransferPacket(int windowId, int itemCount, CompoundTag serializedRecipe) {
		this.windowId = windowId;
		this.itemCount = itemCount;
		this.serializedRecipe = serializedRecipe;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
		buffer.writeInt(itemCount);

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			NbtIo.writeCompressed(serializedRecipe, out);
			buffer.writeByteArray(out.toByteArray());
		} catch (Exception e) {
			StaticPower.LOGGER.error("An error occured when attempting to serialize a JEI recipe for display in an IJEIReipceTransferHandler.", e);
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
		itemCount = buffer.readInt();

		try {
			byte[] compressedData = buffer.readByteArray();
			serializedRecipe = NbtIo.readCompressed(new ByteArrayInputStream(compressedData));
			if (serializedRecipe != null) {
				recipe = new ItemStack[itemCount][];
				for (int x = 0; x < this.recipe.length; x++) {
					final ListTag list = serializedRecipe.getList("#" + x, 10);
					if (list.size() > 0) {
						recipe[x] = new ItemStack[list.size()];
						for (int y = 0; y < list.size(); y++) {
							recipe[x][y] = ItemStack.of(list.getCompound(y));
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
			ServerPlayer player = ctx.get().getSender();
			if (player.containerMenu.containerId == windowId) {
				if (player.containerMenu instanceof IJEIReipceTransferHandler) {
					((IJEIReipceTransferHandler) player.containerMenu).consumeJEITransferRecipe(player, recipe);
				}
			}
		});
	}
}
