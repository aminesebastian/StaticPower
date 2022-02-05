package theking530.staticpower.tileentities.powered.solarpanels;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.power.PowerDistributionComponent;

public class TileEntitySolarPanel extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntitySolarPanel> TYPE_BASIC = new BlockEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator, pos, state) -> new TileEntitySolarPanel(allocator, pos, state, StaticPowerTiers.BASIC),
			ModBlocks.SolarPanelBasic);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntitySolarPanel> TYPE_ADVANCED = new BlockEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator, pos, state) -> new TileEntitySolarPanel(allocator, pos, state, StaticPowerTiers.ADVANCED),
			ModBlocks.SolarPanelAdvanced);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntitySolarPanel> TYPE_STATIC = new BlockEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator, pos, state) -> new TileEntitySolarPanel(allocator, pos, state, StaticPowerTiers.STATIC),
			ModBlocks.SolarPanelStatic);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntitySolarPanel> TYPE_ENERGIZED = new BlockEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator, pos, state) -> new TileEntitySolarPanel(allocator, pos, state, StaticPowerTiers.ENERGIZED),
			ModBlocks.SolarPanelEnergized);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntitySolarPanel> TYPE_LUMUM = new BlockEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator, pos, state) -> new TileEntitySolarPanel(allocator, pos, state, StaticPowerTiers.LUMUM),
			ModBlocks.SolarPanelLumum);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntitySolarPanel> TYPE_CREATIVE = new BlockEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator, pos, state) -> new TileEntitySolarPanel(allocator, pos, state, StaticPowerTiers.CREATIVE),
			ModBlocks.SolarPanelCreative);

	public EnergyStorageComponent energyStorage;
	public SideConfigurationComponent sideConfiguration;
	private final boolean isCreative;

	public TileEntitySolarPanel(BlockEntityTypeAllocator<TileEntitySolarPanel> allocator, BlockPos pos,
			BlockState state, ResourceLocation tierType) {
		super(allocator, pos, state);
		// Set the values based on the tier.
		StaticPowerTier tier = StaticPowerConfig.getTier(tierType);
		isCreative = tierType == StaticPowerTiers.CREATIVE;

		// Set the energy storage.
		registerComponent(energyStorage = new EnergyStorageComponent("PowerBuffer", tier.solarPanelPowerStorage.get(),
				tier.solarPanelPowerGeneration.get(), tier.solarPanelPowerGeneration.get()));

		// Don't let the storage recieve from outside sources.
		energyStorage.getStorage().setCanRecieve(false);

		// Set the side config to only output on the bottom and disable on the rest.
		registerComponent(sideConfiguration = new SideConfigurationComponent("SideConfig",
				new MachineSideMode[] { MachineSideMode.Output, MachineSideMode.Disabled, MachineSideMode.Disabled,
						MachineSideMode.Disabled, MachineSideMode.Disabled, MachineSideMode.Disabled }));

		// Set the distribution component.
		registerComponent(new PowerDistributionComponent("PowerDistribution", energyStorage.getStorage()));
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			generateRF();
		}
	}

	// Functionality
	public void generateRF() {
		if (isGenerating() && energyStorage.canAcceptPower(1)) {
			if (energyStorage.getStorage().getStoredPower() < energyStorage.getStorage().getCapacity()) {
				long generateAmount = getLevel().isRaining() ? energyStorage.getStorage().getMaxReceive() / 2
						: energyStorage.getStorage().getMaxReceive();
				energyStorage.getStorage().setCanRecieve(true);
				energyStorage.getStorage().receivePower(generateAmount, false);
				energyStorage.getStorage().setCanRecieve(false);
			}
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

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerSolarPanel(windowId, inventory, this);
	}
}
