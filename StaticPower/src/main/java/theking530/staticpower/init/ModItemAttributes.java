package theking530.staticpower.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.attributes.rendering.AttributeRenderLayer;
import theking530.api.attributes.rendering.ItemAttributeType;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.attribiutes.Attributes;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class ModItemAttributes {
	private static final DeferredRegister<ItemAttributeType> ITEM_ATTRIBUTES = DeferredRegister.create(StaticCoreRegistries.ITEM_ATTRIBUTE_REGISTRY_KEY, StaticPower.MOD_ID);

	public static final RegistryObject<ItemAttributeType> DrillHardened = ITEM_ATTRIBUTES.register("drill_hardened",
			() -> new ItemAttributeType(Attributes.Smelting, ModItems.BasicMiningDrill, new AttributeRenderLayer(StaticPowerAdditionalModels.BLADE_SMELTING, 2)));

	public static void init(IEventBus eventBus) {
		ITEM_ATTRIBUTES.register(eventBus);
	}
}
