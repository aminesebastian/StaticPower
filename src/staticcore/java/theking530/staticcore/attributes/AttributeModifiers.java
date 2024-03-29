package theking530.staticcore.attributes;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.attributes.modifiers.AttributeModifierType;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.attributes.modifiers.AdditiveNumberAttributeModifierType;
import theking530.staticcore.attributes.modifiers.BooleanAttributeModifierType;
import theking530.staticcore.attributes.modifiers.MultiplicativeNumberAttributeModifierType;

public class AttributeModifiers {
	private static final DeferredRegister<AttributeModifierType<?>> ATTRIBUTE_MODIFIERS = DeferredRegister.create(StaticCoreRegistries.ATTRIBUTE_MODIFIER_REGISTRY_KEY, StaticCore.MOD_ID);

	public static final RegistryObject<AdditiveNumberAttributeModifierType> NumberAdditive = ATTRIBUTE_MODIFIERS.register("number_additive", () -> new AdditiveNumberAttributeModifierType());
	public static final RegistryObject<MultiplicativeNumberAttributeModifierType> NumberMultiplicative = ATTRIBUTE_MODIFIERS.register("number_multiplicative",
			() -> new MultiplicativeNumberAttributeModifierType());
	public static final RegistryObject<BooleanAttributeModifierType> Boolean = ATTRIBUTE_MODIFIERS.register("boolean", () -> new BooleanAttributeModifierType());

	public static void init(IEventBus eventBus) {
		ATTRIBUTE_MODIFIERS.register(eventBus);
	}
}
