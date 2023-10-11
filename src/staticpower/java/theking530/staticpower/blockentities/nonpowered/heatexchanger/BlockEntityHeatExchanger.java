package theking530.staticpower.blockentities.nonpowered.heatexchanger;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.heat.HeatInfo;
import theking530.api.heat.HeatInfoType;
import theking530.api.heat.HeatUtilities;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipe;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityHeatExchanger extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHeatExchanger> TYPE = new BlockEntityTypeAllocator<BlockEntityHeatExchanger>(
			"heat_exchanger", (type, pos, state) -> new BlockEntityHeatExchanger(pos, state), ModBlocks.HeatExchanger);

	public final FluidTankComponent tank;

	public final HeatStorageComponent heatStorage;
	public final SideConfigurationComponent ioSideConfiguration;

	public BlockEntityHeatExchanger(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration",
				DefaultMachineNoFacePreset.INSTANCE));

		StaticCoreTier tierObject = getTierObject();

		registerComponent(tank = new FluidTankComponent("Tank", tierObject.defaultTankCapacity.get())
				.setCapabilityExposedModes(MachineSideMode.Input, MachineSideMode.Output));

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, tank, MachineSideMode.Input));
		registerComponent(
				new FluidOutputServoComponent("FluidOutputServoComponent", 100, tank, MachineSideMode.Output));

		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", tierObject));
	}

	@Override
	public void preProcess() {
		if (getLevel().isClientSide()) {
			return;
		}

		heatStorage.setMaxConductivity(400);

		float drained = Math.abs(tank.getStorage().getDrainedPerTick());
		if (drained <= 0 || tank.isEmpty()) {
			return;

		}

		ThermalConductivityRecipe recipe = HeatUtilities.getThermalPropertiesForFluid(getLevel(), tank.getFluid())
				.orElse(null);
		if (recipe == null) {
			return;
		}

		HeatInfo sourceHeatInfo = new HeatInfo(heatStorage);
		HeatInfo ambientProperties = HeatUtilities.getAmbientProperties(getLevel(), getBlockPos());
		HeatInfo waterHeatInfo = new HeatInfo(recipe.getMass(),
				recipe.hasActiveTemperature() ? recipe.getTemperature() : ambientProperties.temperature(),
				recipe.getConductivity(), recipe.getSpecificHeat(), null, HeatInfoType.FLUID);

		float transferedFlux = HeatUtilities.calculateHeatFluxTransfer(sourceHeatInfo, waterHeatInfo);
		heatStorage.cool(transferedFlux, HeatTransferAction.EXECUTE);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerHeatExchanger(windowId, inventory, this);
	}
}
