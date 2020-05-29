package theking530.staticpower.tileentity.solarpanels;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import theking530.staticpower.energy.PowerDistributor;
import theking530.staticpower.energy.StaticEnergyStorage;
import theking530.staticpower.initialization.ModTileEntityTypes;

public class TileEntityBasicSolarPanel extends TileEntity implements ITickableTileEntity {

	public StaticEnergyStorage energyStorage;
	public PowerDistributor energyDistributor;

	public TileEntityBasicSolarPanel() {
		super(ModTileEntityTypes.SOLAR_PANEL_BASIC);
		initializeSolarPanel();
		energyDistributor = new PowerDistributor(this, energyStorage);
	}

	public void initializeSolarPanel() {
		energyStorage = new StaticEnergyStorage(64);
		energyStorage.setMaxReceive(10);
		energyStorage.setMaxExtract(10 * 2);
	}

	@Override
	public void tick() {
		if (!getWorld().isRemote) {
			generateRF();
			if (energyStorage.getEnergyStored() > 0) {
				energyDistributor.provideRF(Direction.DOWN, energyStorage.getMaxReceive());
			}
		}
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

	/* CAPABILITIES */
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY) {
			if (energyStorage != null) {
				return LazyOptional.of(() -> {
					return energyStorage;
				}).cast();
			}
		}
		return super.getCapability(cap, side);
	}
}
