package theking530.staticcore.utilities;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class StaticPowerRarities {
	private static final Map<String, Rarity> RARITY_MAP = new HashMap<String, Rarity>();

	public static final Rarity BASIC = registerRarity(new ResourceLocation("staticpower", "tier_basic"), TextFormatting.WHITE);
	public static final Rarity ADVANCED = registerRarity(new ResourceLocation("staticpower", "tier_advanced"), TextFormatting.YELLOW);
	public static final Rarity STATIC = registerRarity(new ResourceLocation("staticpower", "tier_static"), TextFormatting.GREEN);
	public static final Rarity ENERGIZED = registerRarity(new ResourceLocation("staticpower", "tier_energized"), TextFormatting.AQUA);
	public static final Rarity LUMUM = registerRarity(new ResourceLocation("staticpower", "tier_lumum"), TextFormatting.DARK_PURPLE);
	public static final Rarity CREATIVE = registerRarity(new ResourceLocation("staticpower", "tier_creative"), TextFormatting.LIGHT_PURPLE);

	public static Rarity registerRarity(ResourceLocation tierId, TextFormatting color) {
		Rarity output = Rarity.create(tierId.toString(), color);
		RARITY_MAP.put(tierId.toString(), output);
		return output;
	}

	public static Rarity getRarityForTier(ResourceLocation tierId) {
		if (!RARITY_MAP.containsKey(tierId.toString())) {
			return Rarity.COMMON;
		}
		return RARITY_MAP.get(tierId.toString());
	}
}
