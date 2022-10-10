package theking530.staticpower.init;

import com.mojang.serialization.Codec;

import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.loot.StaticPowerLootModifier;

public class ModLootSerializers {
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
			StaticPower.MOD_ID);

	public static final RegistryObject<Codec<StaticPowerLootModifier>> STATIC_GRASS = LOOT_MODIFIERS.register("static_grass", StaticPowerLootModifier.CODEC);

	public static void init(IEventBus eventBus) {
		LOOT_MODIFIERS.register(eventBus);
	}
}
