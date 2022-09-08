package theking530.staticpower.blockentities.powered.refinery.powertap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.powered.refinery.BaseRefineryBlockEntity;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRefineryPowerTap extends BaseRefineryBlockEntity implements IStaticPowerStorage {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryPowerTap> TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryPowerTap>(
			(type, pos, state) -> new BlockEntityRefineryPowerTap(pos, state), ModBlocks.RefineryPowerTap);

	public BlockEntityRefineryPowerTap(BlockPos pos, BlockState state) {
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
		// Only provide the energy capability if we are not disabled on that side.
		if (cap == CapabilityStaticPower.STATIC_VOLT_CAPABILITY) {
			if (hasController()) {
				if (side != null) {
					MachineSideMode mode = getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(side);
					if (mode == MachineSideMode.Input) {
						return getController().powerStorage.manuallyGetCapability(cap, side);
					}
				} else {
					return getController().powerStorage.manuallyGetCapability(cap, side);
				}
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRefineryPowerTap(windowId, inventory, this);
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		if (hasController()) {
			return getController().powerStorage.getInputVoltageRange();
		}
		return StaticVoltageRange.ZERO_VOLTAGE;
	}

	@Override
	public double getMaximumCurrentInput() {
		if (hasController()) {
			return getController().powerStorage.getMaximumCurrentInput();
		}
		return 0;
	}

	@Override
	public double getStoredPower() {
		if (hasController()) {
			return getController().powerStorage.getStoredPower();
		}
		return 0;
	}

	@Override
	public double getCapacity() {
		if (hasController()) {
			return getController().powerStorage.getCapacity();
		}
		return 0;
	}

	@Override
	public double getVoltageOutput() {
		if (hasController()) {
			return getController().powerStorage.getVoltageOutput();
		}
		return 0;
	}

	@Override
	public double getMaximumCurrentOutput() {
		if (hasController()) {
			return getController().powerStorage.getMaximumCurrentOutput();
		}
		return 0;
	}

	@Override
	public double addPower(double voltage, double power, boolean simulate) {
		if (hasController()) {
			return getController().powerStorage.addPower(voltage, power, simulate);
		}
		return 0;
	}

	@Override
	public double drainPower(double power, boolean simulate) {
		if (hasController()) {
			return getController().powerStorage.drainPower(power, simulate);
		}
		return 0;
	}

	@Override
	public boolean canAcceptPower() {
		if (hasController()) {
			return getController().powerStorage.canAcceptPower();
		}
		return false;
	}

	@Override
	public boolean doesProvidePower() {
		if (hasController()) {
			return getController().powerStorage.doesProvidePower();
		}
		return false;
	}
}
