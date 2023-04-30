package theking530.staticpower.blockentities.machines.refinery.powertap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.AllSidesInput;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlockEntity;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRefineryPowerTap extends BaseRefineryBlockEntity implements IStaticPowerStorage {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryPowerTap> TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryPowerTap>(
			"refinery_power_tap", (type, pos, state) -> new BlockEntityRefineryPowerTap(pos, state),
			ModBlocks.RefineryPowerTap);

	public final SideConfigurationComponent ioSideConfiguration;

	public BlockEntityRefineryPowerTap(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ADVANCED);
		registerComponent(
				ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", AllSidesInput.INSTANCE));
	}

	@Override
	public void process() {

	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		// Only provide the energy capability if we are not disabled on that side.
		if (cap == CapabilityStaticPower.STATIC_VOLT_CAPABILITY) {
			if (hasController()) {
				if (side != null) {
					MachineSideMode mode = getComponent(SideConfigurationComponent.class)
							.getWorldSpaceDirectionConfiguration(side);
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
	public double getMaximumPowerInput() {
		if (hasController()) {
			return getController().powerStorage.getMaximumPowerInput();
		}
		return 0;
	}

	@Override
	public boolean canAcceptCurrentType(CurrentType type) {
		if (hasController()) {
			return getController().powerStorage.canAcceptCurrentType(type);
		}
		return false;
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
	public StaticPowerVoltage getOutputVoltage() {
		if (hasController()) {
			return getController().powerStorage.getOutputVoltage();
		}
		return StaticPowerVoltage.ZERO;
	}

	@Override
	public double getMaximumPowerOutput() {
		if (hasController()) {
			return getController().powerStorage.getMaximumPowerOutput();
		}
		return 0;
	}

	@Override
	public CurrentType getOutputCurrentType() {
		if (hasController()) {
			return getController().powerStorage.getOutputCurrentType();
		}
		return CurrentType.DIRECT;
	}

	@Override
	public boolean canAcceptExternalPower() {
		if (hasController()) {
			return getController().powerStorage.canAcceptExternalPower();
		}
		return false;
	}

	@Override
	public boolean canOutputExternalPower() {
		if (hasController()) {
			return getController().powerStorage.canOutputExternalPower();
		}
		return false;
	}

	@Override
	public double addPower(PowerStack power, boolean simulate) {
		if (hasController()) {
			return getController().powerStorage.addPower(power, simulate);
		}
		return 0;
	}

	@Override
	public PowerStack drainPower(double power, boolean simulate) {
		if (hasController()) {
			return getController().powerStorage.drainPower(power, simulate);
		}
		return PowerStack.EMPTY;
	}
}
