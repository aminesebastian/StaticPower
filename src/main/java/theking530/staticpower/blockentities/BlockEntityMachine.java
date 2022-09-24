package theking530.staticpower.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;

/**
 * @author Amine
 *
 */
public abstract class BlockEntityMachine extends BlockEntityConfigurable {
	public final PowerStorageComponent powerStorage;

	public BlockEntityMachine(BlockEntityTypeAllocator<? extends BlockEntityMachine> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		disableFaceInteraction();
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier()));
	}
}
