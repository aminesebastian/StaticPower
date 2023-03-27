package theking530.staticpower.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticpower.attributes.DiamondAttributeType;
import theking530.staticpower.attributes.EmeraldAttributeType;
import theking530.staticpower.attributes.FortuneAttributeType;
import theking530.staticpower.attributes.GrindingAttributeType;
import theking530.staticpower.attributes.HasteAttributeType;
import theking530.staticpower.attributes.PromotedAttributeType;
import theking530.staticpower.attributes.RubyAttributeType;
import theking530.staticpower.attributes.SapphireAttributeType;
import theking530.staticpower.attributes.SilkTouchAttributeType;
import theking530.staticpower.attributes.SmeltingAttributeType;

public class ModAttributes {
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
