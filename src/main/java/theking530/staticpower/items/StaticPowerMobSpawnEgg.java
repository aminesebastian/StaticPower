package theking530.staticpower.items;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;

public class StaticPowerMobSpawnEgg extends SpawnEggItem {

	public StaticPowerMobSpawnEgg(String registryName, EntityType<?> typeIn, Color primaryColorIn, Color secondaryColorIn) {
		super(typeIn, secondaryColorIn.encodeInInteger(), primaryColorIn.encodeInInteger(), new Item.Properties().tab(StaticPower.CREATIVE_TAB));
		this.setRegistryName(StaticPower.MOD_ID, registryName);
	}
}
