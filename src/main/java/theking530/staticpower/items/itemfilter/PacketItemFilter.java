package theking530.staticpower.items.itemfilter;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.network.NetworkMessage;

public class PacketItemFilter extends NetworkMessage {
	private boolean whitelist;
	private boolean matchNbt;
	private boolean matchTags;
	private boolean matchMod;

	public PacketItemFilter() {
	}

	public PacketItemFilter(boolean whitelist, boolean matchNbt, boolean matchTags, boolean matchMod) {
		this.whitelist = whitelist;
		this.matchNbt = matchNbt;
		this.matchTags = matchTags;
		this.matchMod = matchMod;
	}

	@Override
	public void decode(PacketBuffer buf) {
		whitelist = buf.readBoolean();
		matchNbt = buf.readBoolean();
		matchTags = buf.readBoolean();
		matchMod = buf.readBoolean();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBoolean(whitelist);
		buf.writeBoolean(matchNbt);
		buf.writeBoolean(matchTags);
		buf.writeBoolean(matchMod);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			ItemStack heldItem = context.get().getSender().getHeldItem(Hand.MAIN_HAND);
			if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemFilter) {
				heldItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((IItemHandler handler) -> {
					ItemFilter filter = (ItemFilter) heldItem.getItem();
					filter.setWhitelistMode(heldItem, whitelist);
					filter.setFilterForNBT(heldItem, matchNbt);
					filter.setFilterForTag(heldItem, matchTags);
					filter.setFilterForMod(heldItem, matchMod);
				});
			}
		});
	}
}
