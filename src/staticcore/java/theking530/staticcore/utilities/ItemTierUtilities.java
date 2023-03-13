package theking530.staticcore.utilities;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public class ItemTierUtilities {
	public static MutableComponent getNameForItemTier(Tier tier) {
		if (tier == Tiers.WOOD) {
			return Component.translatable("gui.staticpower.item_tier.wood").withStyle(ChatFormatting.GOLD);
		}
		if (tier == Tiers.STONE) {
			return Component.translatable("gui.staticpower.item_tier.stone").withStyle(ChatFormatting.DARK_GRAY);
		}
		if (tier == Tiers.IRON) {
			return Component.translatable("gui.staticpower.item_tier.iron").withStyle(ChatFormatting.GRAY);
		}
		if (tier == Tiers.DIAMOND) {
			return Component.translatable("gui.staticpower.item_tier.diamond").withStyle(ChatFormatting.AQUA);
		}
		if (tier == Tiers.GOLD) {
			return Component.translatable("gui.staticpower.item_tier.gold").withStyle(ChatFormatting.YELLOW);
		}
		if (tier == Tiers.NETHERITE) {
			return Component.translatable("gui.staticpower.item_tier.netherite").withStyle(ChatFormatting.DARK_RED);
		}
		return Component.translatable("**ERROR**");
	}
}
