package theking530.staticpower.items;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;

public class StaticPowerMobSpawnEgg extends ForgeSpawnEggItem {

	public StaticPowerMobSpawnEgg(EntityType<? extends Mob> typeIn, Color primaryColorIn, Color secondaryColorIn) {
		super(() -> typeIn, secondaryColorIn.encodeInInteger(), primaryColorIn.encodeInInteger(), new Item.Properties().tab(StaticPower.CREATIVE_TAB));
	}
}
