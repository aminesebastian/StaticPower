package theking530.staticpower.tileentities.powered.refinery.powertap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.refinery.RefineryNetworkModule;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.powered.refinery.BaseRefineryTileEntity;

public class TileEntityRefineryPowerTap extends BaseRefineryTileEntity {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRefineryPowerTap> TYPE = new BlockEntityTypeAllocator<TileEntityRefineryPowerTap>(
			(type, pos, state) -> new TileEntityRefineryPowerTap(pos, state), ModBlocks.RefineryPowerTap);

	public final EnergyStorageComponent energyStorage;

	public TileEntityRefineryPowerTap(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ADVANCED);
		disableFaceInteraction();
		StaticPowerTier tierObject = StaticPowerConfig.getTier(getTier());
		registerComponent(energyStorage = new EnergyStorageComponent("PowerInput", tierObject.defaultMachinePowerCapacity.get(), tierObject.defaultMachinePowerInput.get(),
				tierObject.defaultMachinePowerOutput.get()));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			refineryCableComponent.<RefineryNetworkModule>getNetworkModule(CableNetworkModuleTypes.REFINERY_NETWORK_MODULE).ifPresent(network -> {
				if (!network.getEnergyStorage().isFull()) {
					long added = network.getEnergyStorage().addPowerIgnoreTransferRate(Math.min(this.energyStorage.getStorage().getStoredPower(), 10));
					energyStorage.getStorage().usePowerIgnoreTransferRate(added);
				}
			});
		}
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Input;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRefineryPowerTap(windowId, inventory, this);
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.ALL_SIDES_INPUT;
	}
}
