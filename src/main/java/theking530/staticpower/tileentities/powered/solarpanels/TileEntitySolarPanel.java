package theking530.staticpower.tileentities.powered.solarpanels;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
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
	public static final TileEntityTypeAllocator<TileEntitySolarPanel> TYPE_BASIC = new TileEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator) -> new TileEntitySolarPanel(allocator, StaticPowerTiers.BASIC), ModBlocks.SolarPanelBasic);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntitySolarPanel> TYPE_ADVANCED = new TileEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator) -> new TileEntitySolarPanel(allocator, StaticPowerTiers.ADVANCED), ModBlocks.SolarPanelAdvanced);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntitySolarPanel> TYPE_STATIC = new TileEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator) -> new TileEntitySolarPanel(allocator, StaticPowerTiers.STATIC), ModBlocks.SolarPanelStatic);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntitySolarPanel> TYPE_ENERGIZED = new TileEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator) -> new TileEntitySolarPanel(allocator, StaticPowerTiers.ENERGIZED), ModBlocks.SolarPanelEnergized);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntitySolarPanel> TYPE_LUMUM = new TileEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator) -> new TileEntitySolarPanel(allocator, StaticPowerTiers.LUMUM), ModBlocks.SolarPanelLumum);

	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntitySolarPanel> TYPE_CREATIVE = new TileEntityTypeAllocator<TileEntitySolarPanel>(
			(allocator) -> new TileEntitySolarPanel(allocator, StaticPowerTiers.CREATIVE), ModBlocks.SolarPanelCreative);

	public EnergyStorageComponent energyStorage;
	public SideConfigurationComponent sideConfiguration;
	private final boolean isCreative;

	public TileEntitySolarPanel(TileEntityTypeAllocator<TileEntitySolarPanel> allocator, ResourceLocation tierType) {
		super(allocator);
		// Set the values based on the tier.
		StaticPowerTier tier = StaticPowerConfig.getTier(tierType);
		isCreative = tierType == StaticPowerTiers.CREATIVE;

		// Set the energy storage.
		registerComponent(
				energyStorage = new EnergyStorageComponent("PowerBuffer", tier.solarPanelPowerStorage.get(), tier.solarPanelPowerGeneration.get(), tier.solarPanelPowerGeneration.get()));

		// Don't let the storage recieve from outside sources.
		energyStorage.getStorage().setCanRecieve(false);

		// Set the side config to only output on the bottom and disable on the rest.
		registerComponent(sideConfiguration = new SideConfigurationComponent("SideConfig", new MachineSideMode[] { MachineSideMode.Output, MachineSideMode.Disabled, MachineSideMode.Disabled,
				MachineSideMode.Disabled, MachineSideMode.Disabled, MachineSideMode.Disabled }));

		// Set the distribution component.
		registerComponent(new PowerDistributionComponent("PowerDistribution", energyStorage.getStorage()));
	}

	@Override
	public void process() {
		if (!world.isRemote) {
			generateRF();
		}
	}

	// Functionality
	public void generateRF() {
		if (isGenerating() && energyStorage.canAcceptPower(1)) {
			if (energyStorage.getStorage().getStoredPower() < energyStorage.getStorage().getCapacity()) {
				long generateAmount = getWorld().isRaining() ? energyStorage.getStorage().getMaxReceive() / 2 : energyStorage.getStorage().getMaxReceive();
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
		if (!getWorld().canBlockSeeSky(pos) || !getWorld().isDaytime()) {
			return false;
		}

		// If all of the above passed, return true.
		return true;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerSolarPanel(windowId, inventory, this);
	}
}
