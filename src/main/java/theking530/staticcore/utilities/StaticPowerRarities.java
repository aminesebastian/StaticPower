package theking530.staticcore.utilities;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.item.Rarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;

public class StaticPowerRarities {
	private static final Map<String, Rarity> RARITY_MAP = new HashMap<String, Rarity>();

	public static final Rarity BASIC = registerRarity(new ResourceLocation("staticpower", "tier_basic"), ChatFormatting.WHITE);
	public static final Rarity ADVANCED = registerRarity(new ResourceLocation("staticpower", "tier_advanced"), ChatFormatting.YELLOW);
	public static final Rarity STATIC = registerRarity(new ResourceLocation("staticpower", "tier_static"), ChatFormatting.GREEN);
	public static final Rarity ENERGIZED = registerRarity(new ResourceLocation("staticpower", "tier_energized"), ChatFormatting.AQUA);
	public static final Rarity LUMUM = registerRarity(new ResourceLocation("staticpower", "tier_lumum"), ChatFormatting.DARK_PURPLE);
	public static final Rarity CREATIVE = registerRarity(new ResourceLocation("staticpower", "tier_creative"), ChatFormatting.LIGHT_PURPLE);

	public static Rarity registerRarity(ResourceLocation tierId, ChatFormatting color) {
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
