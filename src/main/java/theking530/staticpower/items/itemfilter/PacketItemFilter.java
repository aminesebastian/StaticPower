package theking530.staticpower.items.itemfilter;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkEvent.Context;
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
	public void decode(FriendlyByteBuf buf) {
		whitelist = buf.readBoolean();
		matchNbt = buf.readBoolean();
		matchTags = buf.readBoolean();
		matchMod = buf.readBoolean();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(whitelist);
		buf.writeBoolean(matchNbt);
		buf.writeBoolean(matchTags);
		buf.writeBoolean(matchMod);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			ItemStack heldItem = context.get().getSender().getItemInHand(InteractionHand.MAIN_HAND);
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
