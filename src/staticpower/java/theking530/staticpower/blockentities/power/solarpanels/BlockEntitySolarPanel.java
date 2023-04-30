package theking530.staticpower.blockentities.power.solarpanels;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.ProductionTrackingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.blockentity.components.energy.PowerDistributionComponent;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponent;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.product.power.PowerProducer;
import theking530.staticcore.teams.ServerTeam;
import theking530.staticpower.blockentities.components.TieredPowerStorageComponent;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntitySolarPanel extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			"solar_panel_basic", (allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state),
			ModBlocks.SolarPanelBasic);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_ADVANCED = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			"solar_panel_advanced", (allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state),
			ModBlocks.SolarPanelAdvanced);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_STATIC = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			"solar_panel_static", (allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state),
			ModBlocks.SolarPanelStatic);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_ENERGIZED = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			"solar_panel_energized", (allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state),
			ModBlocks.SolarPanelEnergized);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_LUMUM = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			"solar_panel_lumum", (allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state),
			ModBlocks.SolarPanelLumum);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_CREATIVE = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			"solar_panel_creative", (allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state),
			ModBlocks.SolarPanelCreative);

	private final ProductionTrackingComponent trackingComponent;
	private final PowerProducer powerProductionStack;

	public PowerStorageComponent powerStorage;
	public SideConfigurationComponent sideConfiguration;
	private final boolean isCreative;
	private double generationPerTick;

	public BlockEntitySolarPanel(BlockEntityTypeAllocator<BlockEntitySolarPanel> allocator, BlockPos pos,
			BlockState state) {
		super(allocator, pos, state);
		// Set the values based on the tier.
		isCreative = getTier() == StaticPowerTiers.CREATIVE;
		generationPerTick = getTierObject().powerConfiguration.solarPanelPowerGeneration.get();

		// Set the energy storage.
		registerComponent(powerStorage = new TieredPowerStorageComponent("Powerbuffer", getTier(), false, true));
		powerStorage.setCapacity(getTierObject().powerConfiguration.solarPanelPowerStorage.get());
		powerStorage.setOutputVoltage(StaticPowerVoltage.LOW);
		powerStorage.setMaximumOutputPower(generationPerTick);
		powerStorage.setInputVoltageRange(new StaticVoltageRange(StaticPowerVoltage.LOW, StaticPowerVoltage.LOW));
		powerStorage.setSideConfiguration(sideConfiguration);

		// Set the side config to only output on the bottom and disable on the rest.
		registerComponent(
				sideConfiguration = new SideConfigurationComponent("SideConfig", SolarPanelSideConfiguration.INSTANCE));

		// Set the distribution component.
		registerComponent(
				new PowerDistributionComponent("PowerDistribution", powerStorage).setProvideAlternatingCurrent(true));
		registerComponent(trackingComponent = new ProductionTrackingComponent("ProductionTracker"));

		powerProductionStack = new PowerProducer(getBlockState().getBlock());
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			generateRF();
		}
	}

	// Functionality
	public void generateRF() {
		ProductionTrackingToken<PowerProducer> powerToken = trackingComponent
				.getToken(StaticCoreProductTypes.Power.get());
		if (isGenerating()) {
			double maxGeneration = getTierObject().powerConfiguration.solarPanelPowerGeneration.get();
			double actualGeneration = maxGeneration;
			if (getLevel().isRaining()) {
				actualGeneration /= 2;
			}

			// No need to check if we the power storage can take this power. If it can't
			// this will just be a no-op.
			double added = powerStorage.addPower(new PowerStack(actualGeneration, powerStorage.getOutputVoltage()),
					false);
			powerToken.setProductionPerSecond((ServerTeam) getTeamComponent().getOwningTeam(), powerProductionStack,
					added * 20, actualGeneration * 20);
			powerToken.produced((ServerTeam) getTeamComponent().getOwningTeam(), powerProductionStack, added);
		} else {
			powerToken.invalidate();
		}
	}

	public boolean isGenerating() {
		// Always returns true if in creative.
		if (isCreative) {
			return true;
		}

		// If we can't see the sky, isnt day time, or is raining, or can't store the
		// power, return false.
		if (!getLevel().canSeeSkyFromBelowWater(worldPosition) || !getLevel().isDay()) {
			return false;
		}

		// If all of the above passed, return true.
		return true;
	}

	protected boolean sideModeFilter(BlockSide side, MachineSideMode mode) {
		if (side == BlockSide.BOTTOM && mode == MachineSideMode.Output) {
			return true;
		} else if (mode == MachineSideMode.Never) {
			return true;
		}
		return false;
	}

	protected void sideConfigCallback(BlockSide side, MachineSideMode mode) {

	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerSolarPanel(windowId, inventory, this);
	}
}
