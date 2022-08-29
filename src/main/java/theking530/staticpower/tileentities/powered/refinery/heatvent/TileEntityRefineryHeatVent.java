package theking530.staticpower.tileentities.powered.refinery.heatvent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.powered.refinery.BaseRefineryTileEntity;

public class TileEntityRefineryHeatVent extends BaseRefineryTileEntity implements IHeatStorage {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRefineryHeatVent> TYPE = new BlockEntityTypeAllocator<TileEntityRefineryHeatVent>(
			(type, pos, state) -> new TileEntityRefineryHeatVent(pos, state), ModBlocks.RefineryHeatVent);

	public TileEntityRefineryHeatVent(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ADVANCED);
		enableFaceInteraction();
	}

	@Override
	public void process() {
		if (hasController() && !getLevel().isClientSide()) {
			getController().heatStorage.getStorage().transferWithSurroundings(getLevel(), getBlockPos());
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
			if (side != null && hasController()) {
				return getController().heatStorage.manuallyGetCapability(cap, side);
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRefineryHeatVent(windowId, inventory, this);
	}

	@Override
	public int getCurrentHeat() {
		if (hasController()) {
			return getController().heatStorage.getStorage().getCurrentHeat();
		}
		return 0;
	}

	@Override
	public int getOverheatThreshold() {
		if (hasController()) {
			return getController().heatStorage.getStorage().getOverheatThreshold();
		}
		return 0;
	}

	@Override
	public float getConductivity() {
		if (hasController()) {
			return getController().heatStorage.getStorage().getConductivity();
		}
		return 0;
	}

	@Override
	public int heat(int amountToHeat, boolean simulate) {
		// Cannot heat directly through this.
		return 0;
	}

	@Override
	public int cool(int amountToCool, boolean simulate) {
		// Cannot cool directly through this.
		return 0;
	}
}