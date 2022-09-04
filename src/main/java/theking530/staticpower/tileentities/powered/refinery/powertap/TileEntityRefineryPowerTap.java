package theking530.staticpower.tileentities.powered.refinery.powertap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.power.IStaticVoltHandler;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.powered.refinery.BaseRefineryTileEntity;

public class TileEntityRefineryPowerTap extends BaseRefineryTileEntity implements IStaticVoltHandler {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRefineryPowerTap> TYPE = new BlockEntityTypeAllocator<TileEntityRefineryPowerTap>(
			(type, pos, state) -> new TileEntityRefineryPowerTap(pos, state), ModBlocks.RefineryPowerTap);

	@SuppressWarnings("unused")
	private final long transferRate;

	public TileEntityRefineryPowerTap(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ADVANCED);
		enableFaceInteraction();
		StaticPowerTier tier = StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED);
		this.transferRate = tier.defaultMachinePowerInput.get();
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
		if (cap == CapabilityEnergy.ENERGY || cap == CapabilityStaticVolt.STATIC_VOLT_CAPABILITY) {
			if (hasController()) {
				if(side != null) {
					MachineSideMode mode = getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(side);
					if (mode == MachineSideMode.Input) {
						return getController().energyStorage.manuallyGetCapability(cap, side);
					}
				}else {
					return getController().energyStorage.manuallyGetCapability(cap, side);					
				}
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
		return new ContainerRefineryPowerTap(windowId, inventory, this);
	}
}
