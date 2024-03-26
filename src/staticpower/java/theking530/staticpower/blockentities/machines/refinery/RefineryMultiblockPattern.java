package theking530.staticpower.blockentities.machines.refinery;

import theking530.staticcore.blockentity.components.multiblock.MultiBlockFormationStatus;
import theking530.staticcore.blockentity.components.multiblock.freeform.FreeformMultiblockPattern;
import theking530.staticpower.init.ModBlocks;

public class RefineryMultiblockPattern extends FreeformMultiblockPattern {
	public static final MultiBlockFormationStatus MULTIPLE_CONTROLLERS = MultiBlockFormationStatus
			.failed("gui.staticpower.refinery_status_multiple_controllers");
	public static final MultiBlockFormationStatus MISSING_BOILER = MultiBlockFormationStatus
			.failed("gui.staticpower.refinery_missing_boiler");

	public RefineryMultiblockPattern() {
		super('c');
		addDefinition('c', ModBlocks.RefineryController, 1, 1);
		addDefinition('b', ModBlocks.RefineryBoiler);
		addDefinition('f', ModBlocks.RefineryFluidInput);
		addDefinition('h', ModBlocks.RefineryHeatVent, true);
		addDefinition('p', ModBlocks.RefineryPowerTap);
		addDefinition('d', ModBlocks.RefineryCondenser);
		addDefinition('t', ModBlocks.RefineryTower);
	}

}
