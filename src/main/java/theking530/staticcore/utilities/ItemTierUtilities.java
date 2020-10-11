package theking530.staticcore.utilities;

import net.minecraft.item.ItemTier;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemTierUtilities {
	public static IFormattableTextComponent getNameForItemTier(ItemTier tier) {
		switch (tier) {
		case WOOD:
			return new TranslationTextComponent("gui.staticpower.item_tier.wood").mergeStyle(TextFormatting.GOLD);
		case STONE:
			return new TranslationTextComponent("gui.staticpower.item_tier.stone").mergeStyle(TextFormatting.DARK_GRAY);
		case IRON:
			return new TranslationTextComponent("gui.staticpower.item_tier.iron").mergeStyle(TextFormatting.GRAY);
		case DIAMOND:
			return new TranslationTextComponent("gui.staticpower.item_tier.diamond").mergeStyle(TextFormatting.AQUA);
		case GOLD:
			return new TranslationTextComponent("gui.staticpower.item_tier.gold").mergeStyle(TextFormatting.YELLOW);
		case NETHERITE:
			return new TranslationTextComponent("gui.staticpower.item_tier.netherite").mergeStyle(TextFormatting.DARK_RED);
		}
		return new TranslationTextComponent("**ERROR**");
	}
}
