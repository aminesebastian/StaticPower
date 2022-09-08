package theking530.staticpower.blockentities.powered.refinery.heatvent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.HeatStorageUtilities;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.powered.refinery.BaseRefineryBlockEntity;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRefineryHeatVent extends BaseRefineryBlockEntity implements IHeatStorage {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryHeatVent> TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryHeatVent>(
			(type, pos, state) -> new BlockEntityRefineryHeatVent(pos, state), ModBlocks.RefineryHeatVent);

	public BlockEntityRefineryHeatVent(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ADVANCED);
		enableFaceInteraction();
	}

	@Override
	public void process() {
		if (hasController()) {
			if (!getLevel().isClientSide()) {
				HeatStorageUtilities.transferHeatWithSurroundings(getController().heatStorage, getLevel(), getBlockPos(), HeatTransferAction.EXECUTE);
			}
		}
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Never;
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.ALL_SIDES_NEVER;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityHeatable.HEAT_STORAGE_CAPABILITY) {
			if (hasController()) {
				return getController().heatStorage.manuallyGetCapability(cap, side);
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		if (hasController()) {
			return getController().createMenu(windowId, inventory, player);
		}
		return null;
	}

	public BlockPos getContainerReferencedBlockPos() {
		if (hasController()) {
			return getController().getContainerReferencedBlockPos();
		}
		return super.getContainerReferencedBlockPos();
	}

	@Override
	public int getCurrentHeat() {
		if (hasController()) {
			return getController().heatStorage.getCurrentHeat();
		}
		return 0;
	}

	@Override
	public int getOverheatThreshold() {
		if (hasController()) {
			return getController().heatStorage.getOverheatThreshold();
		}
		return 0;
	}

	@Override
	public int getMaximumHeat() {
		if (hasController()) {
			return getController().heatStorage.getMaximumHeat();
		}
		return 0;
	}

	@Override
	public float getConductivity() {
		if (hasController()) {
			return getController().heatStorage.getConductivity();
		}
		return 0;
	}

	@Override
	public int heat(int amountToHeat, HeatTransferAction action) {
		// Cannot heat directly through this.
		return 0;
	}

	@Override
	public int cool(int amountToCool, HeatTransferAction action) {
		// Cannot cool directly through this.
		return 0;
	}
}
