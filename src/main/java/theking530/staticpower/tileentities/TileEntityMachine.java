package theking530.staticpower.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;

/**
 * @author Amine
 *
 */
public abstract class TileEntityMachine extends TileEntityConfigurable {
	public final EnergyStorageComponent energyStorage;
	private final ResourceLocation tier;

	public boolean isUpdateQueued = true;

	public TileEntityMachine(BlockEntityTypeAllocator<? extends TileEntityMachine> allocator, BlockPos pos,
			BlockState state) {
		this(allocator, pos, state, StaticPowerTiers.BASIC);
	}

	public TileEntityMachine(BlockEntityTypeAllocator<? extends TileEntityMachine> allocator, BlockPos pos,
			BlockState state, ResourceLocation tier) {
		super(allocator, pos, state);
		this.tier = tier;
		disableFaceInteraction();
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage",
				tierObject.defaultMachinePowerCapacity.get(), tierObject.defaultMachinePowerInput.get(),
				tierObject.defaultMachinePowerOutput.get()));
	}

	public ResourceLocation getTier() {
		return tier;
	}
}
