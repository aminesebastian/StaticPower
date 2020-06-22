package theking530.staticpower.items.itemfilter;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.network.NetworkMessage;

public class PacketItemFilter extends NetworkMessage {
	private boolean WHITE_LIST_MODE;
	private boolean MATCH_METADATA;
	private boolean MATCH_NBT;
	private boolean MATCH_ORE_DICT;

	public PacketItemFilter() {
	}

	public PacketItemFilter(boolean listMode, boolean checkMeta, boolean checkNBT, boolean checkOreDict) {
		WHITE_LIST_MODE = listMode;
		MATCH_METADATA = checkMeta;
		MATCH_NBT = checkNBT;
		MATCH_ORE_DICT = checkOreDict;
	}

	@Override
	public void decode(PacketBuffer buf) {
		WHITE_LIST_MODE = buf.readBoolean();
		MATCH_METADATA = buf.readBoolean();
		MATCH_NBT = buf.readBoolean();
		MATCH_ORE_DICT = buf.readBoolean();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBoolean(WHITE_LIST_MODE);
		buf.writeBoolean(MATCH_METADATA);
		buf.writeBoolean(MATCH_NBT);
		buf.writeBoolean(MATCH_ORE_DICT);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			ItemStack heldItem = Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND);
			if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemFilter) {
				heldItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((IItemHandler handler) -> {
					InventoryItemFilter filterInv = (InventoryItemFilter) handler;
					filterInv.setWhiteListMode(WHITE_LIST_MODE);
					filterInv.setMatchMetadata(MATCH_METADATA);
					filterInv.setMatchNBT(MATCH_NBT);
					filterInv.setMatchMetadata(MATCH_ORE_DICT);
				});
			}
		});
	}
}
