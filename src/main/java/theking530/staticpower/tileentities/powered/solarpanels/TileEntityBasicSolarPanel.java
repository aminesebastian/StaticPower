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
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.PowerDistributionComponent;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;

public class TileEntityBasicSolarPanel extends TileEntityBase implements ITickableTileEntity {

	public EnergyStorageComponent energyStorage;

	public TileEntityBasicSolarPanel() {
		super(ModTileEntityTypes.SOLAR_PANEL_BASIC);
		registerComponent(energyStorage = new EnergyStorageComponent("PowerBuffer", 64));
		energyStorage.setMaxReceive(10);
		energyStorage.setMaxExtract(10);

		registerComponent(new PowerDistributionComponent("PowerDistribution", energyStorage));
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
			if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
				energyStorage.receiveEnergy(energyStorage.getMaxReceive(), false);
			}
		}
	}

	public boolean isGenerating() {
		if (!getWorld().canBlockSeeSky(pos)) {
			return false;
		} else if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
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
