package theking530.staticpower.tileentities.powered.solarpanels;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.PowerDistributionComponent;

public class TileEntitySolarPanel extends TileEntityBase implements ITickableTileEntity {

	public EnergyStorageComponent energyStorage;

	public TileEntitySolarPanel() {
		super(ModTileEntityTypes.SOLAR_PANEL_BASIC);
		registerComponent(energyStorage = new EnergyStorageComponent("PowerBuffer", 0, 10, 10));
		energyStorage.getStorage().setCanRecieve(false);

		registerComponent(new PowerDistributionComponent("PowerDistribution", energyStorage.getStorage()));
	}

	@Override
	public void process() {
		// Perform the generation on both the client and the server. The client
		// generation is only for immediate visual response.
		generateRF();
	}

	@Override
	public void onInitializedInWorld(World world, BlockPos pos) {
		super.onInitializedInWorld(world, pos);
		if (energyStorage.getStorage().getMaxEnergyStored() == 0) {
			BlockSolarPanel solarPanelBlock = (BlockSolarPanel) world.getBlockState(pos).getBlock();
			StaticPowerTier tier = StaticPowerDataRegistry.getTier(solarPanelBlock.tierType);
			energyStorage.getStorage().setCapacity(tier.getSolarPanelPowerStorage());
			energyStorage.getStorage().setMaxExtract(tier.getSolarPanelPowerGeneration());
			energyStorage.getStorage().setMaxReceive(tier.getSolarPanelPowerGeneration());
		}
	}

	// Functionality
	public void generateRF() {
		if (getWorld().canBlockSeeSky(pos)) {
			if (energyStorage.getStorage().getEnergyStored() < energyStorage.getStorage().getMaxEnergyStored()) {
				energyStorage.getStorage().setCanRecieve(true);
				energyStorage.getStorage().receiveEnergy(energyStorage.getStorage().getMaxReceive(), false);
				energyStorage.getStorage().setCanRecieve(false);
			}
		}
	}

	public boolean isGenerating() {
		if (!getWorld().canBlockSeeSky(pos)) {
			return false;
		} else if (energyStorage.getStorage().getEnergyStored() < energyStorage.getStorage().getMaxEnergyStored()) {
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
