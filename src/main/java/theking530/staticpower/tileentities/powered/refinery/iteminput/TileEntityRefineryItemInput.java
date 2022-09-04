package theking530.staticpower.tileentities.powered.refinery.iteminput;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.api.power.IStaticVoltHandler;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.powered.refinery.BaseRefineryTileEntity;

public class TileEntityRefineryItemInput extends BaseRefineryTileEntity implements IStaticVoltHandler {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRefineryItemInput> TYPE = new BlockEntityTypeAllocator<TileEntityRefineryItemInput>(
			(type, pos, state) -> new TileEntityRefineryItemInput(pos, state), ModBlocks.RefineryItemInput);

	public TileEntityRefineryItemInput(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ADVANCED);
		enableFaceInteraction();
	}

	@Override
	public void process() {

	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Input;
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.ALL_SIDES_INPUT;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && hasController()) {
			if ((side != null && ioSideConfiguration.getWorldSpaceDirectionConfiguration(side) == MachineSideMode.Input)) {
				return getController().catalystInventory.manuallyProvideCapability(cap, side);
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public long getStoredPower() {
		if (hasController()) {
			return getController().energyStorage.getStoredPower();
		}
		return 0;
	}

	@Override
	public long getCapacity() {
		if (hasController()) {
			return getController().energyStorage.getCapacity();
		}
		return 0;
	}

	@Override
	public long getMaxReceive() {
		if (hasController()) {
			return getController().energyStorage.getMaxReceive();
		}
		return 0;
	}

	@Override
	public long getMaxDrain() {
		if (hasController()) {
			return getController().energyStorage.getMaxDrain();
		}
		return 0;
	}

	@Override
	public long receivePower(long power, boolean simulate) {
		if (hasController()) {
			return getController().energyStorage.receivePower(power, simulate);
		}
		return 0;
	}

	@Override
	public long drainPower(long power, boolean simulate) {
		if (hasController()) {
			return getController().energyStorage.drainPower(power, simulate);
		}
		return 0;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRefineryItemInput(windowId, inventory, this);
	}
}
