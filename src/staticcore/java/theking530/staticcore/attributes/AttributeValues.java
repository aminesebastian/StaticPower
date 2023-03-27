package theking530.staticcore.attributes;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.attributes.values.AttributeValueType;
import theking530.api.attributes.values.BooleanAttributeType;
import theking530.api.attributes.values.NumberAttributeType;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;

public class AttributeValues {
	private static final DeferredRegister<AttributeValueType<?>> ATTRIBUTE_VALUES = DeferredRegister.create(StaticCoreRegistries.ATTRIBUTE_VALUE_REGISTRY_KEY, StaticCore.MOD_ID);

	public static final RegistryObject<NumberAttributeType> Number = ATTRIBUTE_VALUES.register("number", () -> new NumberAttributeType());
	public static final RegistryObject<BooleanAttributeType> Boolean = ATTRIBUTE_VALUES.register("boolean", () -> new BooleanAttributeType());

	public static void init(IEventBus eventBus) {
		ATTRIBUTE_VALUES.register(eventBus);
	}
}
