package theking530.staticpower.blockentities.powered.solarpanels;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntitySolarPanel extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			(allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state), ModBlocks.SolarPanelBasic);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_ADVANCED = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			(allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state), ModBlocks.SolarPanelAdvanced);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_STATIC = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			(allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state), ModBlocks.SolarPanelStatic);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_ENERGIZED = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			(allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state), ModBlocks.SolarPanelEnergized);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_LUMUM = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			(allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state), ModBlocks.SolarPanelLumum);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolarPanel> TYPE_CREATIVE = new BlockEntityTypeAllocator<BlockEntitySolarPanel>(
			(allocator, pos, state) -> new BlockEntitySolarPanel(allocator, pos, state), ModBlocks.SolarPanelCreative);

	public PowerStorageComponent powerStorage;
	public SideConfigurationComponent sideConfiguration;
	private final boolean isCreative;
	private double generationPerTick;

	public BlockEntitySolarPanel(BlockEntityTypeAllocator<BlockEntitySolarPanel> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		// Set the values based on the tier.
		StaticPowerTier tier = getTierObject();
		isCreative = getTier() == StaticPowerTiers.CREATIVE;
		generationPerTick = tier.solarPanelPowerGeneration.get();

		// Set the energy storage.
		registerComponent(powerStorage = new PowerStorageComponent("PowerBuffer", tier.solarPanelPowerStorage.get(), Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
				tier.solarPanelPowerGeneration.get(), 1));
		powerStorage.setSideConfiguration(sideConfiguration);
		powerStorage.setCanAcceptPower(false);

		// Set the side config to only output on the bottom and disable on the rest.
		registerComponent(sideConfiguration = new SideConfigurationComponent("SideConfig", this::sideConfigCallback, this::sideModeFilter,
				SideConfigurationComponent.ALL_SIDES_NEVER.copy().setSide(BlockSide.BOTTOM, true, MachineSideMode.Output)));

		// Set the distribution component.
		registerComponent(new PowerDistributionComponent("PowerDistribution", powerStorage));
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			generateRF();
		}
	}

	// Functionality
	public void generateRF() {
		if (isGenerating()) {
			double generateAmount = generationPerTick;
			if (getLevel().isRaining()) {
				generateAmount /= 2;
			}

			// No need to check if we the power storage can take this power. If it can't
			// this will just be a no-op.
			powerStorage.addPower(powerStorage.getVoltageOutput(), generateAmount, false);
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
