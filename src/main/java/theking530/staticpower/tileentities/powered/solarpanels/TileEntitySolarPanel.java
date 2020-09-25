package theking530.staticpower.tileentities.powered.solarpanels;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.power.PowerDistributionComponent;

public class TileEntitySolarPanel extends TileEntityBase implements ITickableTileEntity {
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

	public TileEntitySolarPanel(TileEntityTypeAllocator<TileEntitySolarPanel> allocator, ResourceLocation tierType) {
		super(allocator);
		// Set the values based on the tier.
		StaticPowerTier tier = TierReloadListener.getTier(tierType);
		registerComponent(energyStorage = new EnergyStorageComponent("PowerBuffer", tier.getSolarPanelPowerStorage(), tier.getSolarPanelPowerGeneration(), tier.getSolarPanelPowerGeneration()));
		energyStorage.getStorage().setCanRecieve(false);

		registerComponent(new PowerDistributionComponent("PowerDistribution", energyStorage.getStorage()));

		energyStorage.getStorage().setCapacity(tier.getSolarPanelPowerStorage());
		energyStorage.getStorage().setMaxExtract(tier.getSolarPanelPowerGeneration());
		energyStorage.getStorage().setMaxReceive(tier.getSolarPanelPowerGeneration());
	}

	@Override
	public void process() {
		// Perform the generation on both the client and the server. The client
		// generation is only for immediate visual response.
		generateRF();
	}

	// Functionality
	public void generateRF() {
		if (getWorld().canBlockSeeSky(pos)) {
			if (energyStorage.getStorage().getStoredPower() < energyStorage.getStorage().getCapacity()) {
				energyStorage.getStorage().setCanRecieve(true);
				energyStorage.getStorage().receivePower(energyStorage.getStorage().getMaxReceive(), false);
				energyStorage.getStorage().setCanRecieve(false);
			}
		}
	}

	public boolean isGenerating() {
		if (!getWorld().canBlockSeeSky(pos)) {
			return false;
		} else if (energyStorage.getStorage().getStoredPower() < energyStorage.getStorage().getCapacity()) {
			return true;
		}
		return false;
	}

	// Light
	public float lightRatio() {
		return calculateLightRatio(getWorld(), pos);
	}

	public float calculateLightRatio(World world, BlockPos pos) {
		int lightValue = 128;
		float sunAngle = world.getCelestialAngleRadians(1.0F);
		if (sunAngle < (float) Math.PI) {
			sunAngle += (0.0F - sunAngle) * 0.2F;
		} else {
			sunAngle += (((float) Math.PI * 2F) - sunAngle) * 0.2F;
		}

		lightValue = Math.round(lightValue * MathHelper.cos(sunAngle));

		lightValue = MathHelper.clamp(lightValue, 0, 15);
		return lightValue / 15f;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return null;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModBlocks.SolarPanelBasic.getTranslationKey());
	}
}
