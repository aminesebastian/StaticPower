package theking530.staticpower.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.blockentity.components.multiblock.newstyle.AbstractMultiblockPattern;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.nonpowered.blastfurnace.BlastFurnaceMultiblockPattern;

public class ModMultiblocks {

	private static final DeferredRegister<AbstractMultiblockPattern> MULTIBLOCK_TYPE_REGISTRY = DeferredRegister
			.create(StaticCoreRegistries.MULTIBLOCK_TYPE_REGISTRY_KEY, StaticPower.MOD_ID);

	public static final RegistryObject<BlastFurnaceMultiblockPattern> BLAST_FURNACE = MULTIBLOCK_TYPE_REGISTRY
			.register("blast_furnace", () -> new BlastFurnaceMultiblockPattern());

	public static void init(IEventBus eventBus) {
		MULTIBLOCK_TYPE_REGISTRY.register(eventBus);
	}
}
