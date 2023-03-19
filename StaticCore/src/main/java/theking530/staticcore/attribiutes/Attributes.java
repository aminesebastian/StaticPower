package theking530.staticcore.attribiutes;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.attribiutes.types.DiamondAttributeType;
import theking530.staticcore.attribiutes.types.EmeraldAttributeType;
import theking530.staticcore.attribiutes.types.FortuneAttributeType;
import theking530.staticcore.attribiutes.types.GrindingAttributeType;
import theking530.staticcore.attribiutes.types.HasteAttributeType;
import theking530.staticcore.attribiutes.types.PromotedAttributeType;
import theking530.staticcore.attribiutes.types.RubyAttributeType;
import theking530.staticcore.attribiutes.types.SapphireAttributeType;
import theking530.staticcore.attribiutes.types.SilkTouchAttributeType;
import theking530.staticcore.attribiutes.types.SmeltingAttributeType;

public class Attributes {
	private static final DeferredRegister<AttributeType<?>> ATTRIBUTES = DeferredRegister.create(StaticCoreRegistries.ATTRIBUTE_REGISTRY_KEY, StaticCore.MOD_ID);

	public static final RegistryObject<DiamondAttributeType> DiamondHardened = ATTRIBUTES.register("hardened_diamond", () -> new DiamondAttributeType());
	public static final RegistryObject<EmeraldAttributeType> EmeraldHardened = ATTRIBUTES.register("hardened_emerald", () -> new EmeraldAttributeType());
	public static final RegistryObject<RubyAttributeType> RubyHardened = ATTRIBUTES.register("hardened_ruby", () -> new RubyAttributeType());
	public static final RegistryObject<SapphireAttributeType> SapphireHardened = ATTRIBUTES.register("hardened_sapphire", () -> new SapphireAttributeType());

	public static final RegistryObject<FortuneAttributeType> Fortune = ATTRIBUTES.register("fortune", () -> new FortuneAttributeType());
	public static final RegistryObject<GrindingAttributeType> Grinding = ATTRIBUTES.register("grinding", () -> new GrindingAttributeType());
	public static final RegistryObject<HasteAttributeType> Haste = ATTRIBUTES.register("haste", () -> new HasteAttributeType());
	public static final RegistryObject<PromotedAttributeType> Promoted = ATTRIBUTES.register("promoted", () -> new PromotedAttributeType());
	public static final RegistryObject<SilkTouchAttributeType> SilkTouch = ATTRIBUTES.register("silk_touch", () -> new SilkTouchAttributeType());
	public static final RegistryObject<SmeltingAttributeType> Smelting = ATTRIBUTES.register("smelting", () -> new SmeltingAttributeType());

	public static void init(IEventBus eventBus) {
		ATTRIBUTES.register(eventBus);
	}
}
