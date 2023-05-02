package theking530.staticpower.items;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.init.ModCreativeTabs;

public class StaticPowerMobSpawnEgg extends ForgeSpawnEggItem {

	public StaticPowerMobSpawnEgg(EntityType<? extends Mob> typeIn, SDColor primaryColorIn, SDColor secondaryColorIn) {
		super(() -> typeIn, secondaryColorIn.encodeInInteger(), primaryColorIn.encodeInInteger(),
				new Item.Properties().tab(ModCreativeTabs.GENERAL));
	}
}
