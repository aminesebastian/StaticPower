package theking530.staticcore.utilities;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Tiers;

public class ItemTierUtilities {
	public static MutableComponent getNameForItemTier(Tiers tier) {
		switch (tier) {
		case WOOD:
			return new TranslatableComponent("gui.staticpower.item_tier.wood").withStyle(ChatFormatting.GOLD);
		case STONE:
			return new TranslatableComponent("gui.staticpower.item_tier.stone").withStyle(ChatFormatting.DARK_GRAY);
		case IRON:
			return new TranslatableComponent("gui.staticpower.item_tier.iron").withStyle(ChatFormatting.GRAY);
		case DIAMOND:
			return new TranslatableComponent("gui.staticpower.item_tier.diamond").withStyle(ChatFormatting.AQUA);
		case GOLD:
			return new TranslatableComponent("gui.staticpower.item_tier.gold").withStyle(ChatFormatting.YELLOW);
		case NETHERITE:
			return new TranslatableComponent("gui.staticpower.item_tier.netherite").withStyle(ChatFormatting.DARK_RED);
		}
		return new TranslatableComponent("**ERROR**");
	}
}
