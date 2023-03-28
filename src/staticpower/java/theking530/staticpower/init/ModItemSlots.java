package theking530.staticpower.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.item.compound.slot.CompoundItemSlot;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticpower.StaticPower;

public class ModItemSlots {
	private static final DeferredRegister<CompoundItemSlot> SLOTS = DeferredRegister.create(StaticCoreRegistries.ITEM_SLOT_KEY, StaticPower.MOD_ID);

	public static final RegistryObject<CompoundItemSlot> DRILL_BIT = SLOTS.register("drill_bit", () -> new CompoundItemSlot("slot.staticpower.drill_bit"));
	public static final RegistryObject<CompoundItemSlot> CHAINSAW_BLADE = SLOTS.register("chainsaw_blade", () -> new CompoundItemSlot("slot.staticpower.chainsaw_blade"));

	public static void init(IEventBus eventBus) {
		SLOTS.register(eventBus);
	}
}
