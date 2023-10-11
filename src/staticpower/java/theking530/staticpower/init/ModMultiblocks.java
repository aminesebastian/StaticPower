package theking530.staticpower.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockType;
import theking530.staticcore.blockentity.components.multiblock.newstyle.fixed.FixedMultiblockPattern;
import theking530.staticpower.StaticPower;

public class ModMultiblocks {

	private static final DeferredRegister<MultiblockType<?>> MULTIBLOCK_TYPE_REGISTRY = DeferredRegister
			.create(StaticCoreRegistries.MULTIBLOCK_TYPE_REGISTRY_KEY, StaticPower.MOD_ID);

	public static final RegistryObject<MultiblockType<FixedMultiblockPattern>> BLAST_FURNACE;
	static {
		FixedMultiblockPattern pattern = new FixedMultiblockPattern();
		pattern.addDefinition('f', ModBlocks.BlastFurnace);
		// @formatter:off
		pattern.addLayer()
		.addRow("fff")
		.addRow("fff")
		.addRow("fff");
		pattern.addLayer()
		.addRow("fff")
		.addRow("f f")
		.addRow("fff");
		pattern.addLayer()
		.addRow("fff")
		.addRow("f f")
		.addRow("fff");
		pattern.addLayer()
		.addRow("fff")
		.addRow("fff")
		.addRow("fff");
		// @formatter:on
		BLAST_FURNACE = MULTIBLOCK_TYPE_REGISTRY.register("blast_furnace",
				() -> new MultiblockType<FixedMultiblockPattern>(pattern));
	}

	public static void init(IEventBus eventBus) {
		MULTIBLOCK_TYPE_REGISTRY.register(eventBus);
	}
}
