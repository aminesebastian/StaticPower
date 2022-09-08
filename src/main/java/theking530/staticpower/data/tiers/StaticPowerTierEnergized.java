package theking530.staticpower.data.tiers;

import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierEnergized extends StaticPowerTier {

	public StaticPowerTierEnergized(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "energized");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.energized";
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 4;
	}

	@Override
	protected double getPortableBatteryCapacity() {
		return 50000;
	}

	@Override
	protected List<Double> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(24.0, 48.0);
	}

	@Override
	protected double getPortableBatteryMaxOutputCurrent() {
		return 10;
	}

	@Override
	protected double getPortableBatteryOutputVoltage() {
		return 48;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 8;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return 40;
	}

	@Override
	protected int getPumpRate() {
		return 40;
	}

	@Override
	protected int getCableFilterSlots() {
		return 9;
	}

	@Override
	protected int getCableExtractorRate() {
		return 20;
	}

	@Override
	protected int getCableExtractionStackSize() {
		return 32;
	}

	@Override
	protected int getCableExtractionFluidRate() {
		return 512;
	}

	@Override
	protected int getCableExtractionFilterSlots() {
		return 9;
	}

	@Override
	protected double getExtractedItemInitialSpeed() {
		return 2;
	}

	@Override
	protected int getCableRetrievalRate() {
		return 20;
	}

	@Override
	protected int getCableRetrievalStackSize() {
		return 32;
	}

	@Override
	protected int getCableRetrievalFilterSlots() {
		return 4;
	}

	@Override
	protected double getRetrievedItemInitialSpeed() {
		return 2;
	}

	@Override
	protected double getCablePowerMaxCurrent() {
		return 35;
	}

	@Override
	protected double getCablePowerResistancePerBlock() {
		return 0.5;
	}

	@Override
	protected double getCableIndustrialPowerMaxCurrent() {
		return 400;
	}

	@Override
	protected double getCableIndustrialPowerResistancePerBlock() {
		return 5;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 131072;
	}

	@Override
	protected double getBatteryCapacity() {
		return 50000;
	}

	@Override
	protected List<Double> internalGetBatteryInputVoltageRange() {
		return Arrays.asList(0.0, 240.0);
	}

	@Override
	protected List<Double> internalGetBatteryOutputVoltageRange() {
		return Arrays.asList(120.0, 240.0);
	}

	@Override
	protected double getBatteryMaximumOutputCurrent() {
		return 20.0;
	}

	@Override
	protected double getItemCableMaxSpeed() {
		return 2;
	}

	@Override
	protected int getItemFilterSlots() {
		return 9;
	}

	@Override
	protected double getItemCableAcceleration() {
		return 1.75f;
	}

	@Override
	protected double getItemCableFriction() {
		return 1.75f;
	}

	@Override
	protected int getDrillBitUses() {
		return 12000;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 24000;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 8.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 10.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 8.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 5.0f;
	}

	@Override
	public float getHeatCapacityUpgrade() {
		return 4.0f;
	}

	@Override
	public float getHeatConductivityUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 6.0f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 10f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 2.0f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 1250;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 5.0f;
	}

	@Override
	protected double getDefaultMachinePowerCapacity() {
		return 1000;
	}

	@Override
	protected List<Double> internalGetDefaultMachineInputVoltageRange() {
		return Arrays.asList(48.0, 120.0);
	}

	@Override
	protected double getDefaultMachineMaximumInputCurrent() {
		return 4;
	}

	@Override
	protected double getDefaultMachineMaximumVoltageOutput() {
		return 120;
	}

	@Override
	protected double getDefaultMachineMaximumCurrentOutput() {
		return 4;
	}

	@Override
	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(1400);
	}

	@Override
	protected int getDefaultMachineMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(2000);
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 15000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 32000;
	}

	@Override
	protected int getCableFluidCapacity() {
		return 2500;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return 25000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 2.5;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 864000;
	}

	@Override
	protected double getMagnetPowerCapacity() {
		return getPortableBatteryCapacity() * 2;
	}

	@Override
	protected int getMagnetRadius() {
		return 6;
	}

	@Override
	protected float getDrillSpeedMultiplier() {
		return 1.5f;
	}

	@Override
	protected float getChainsawSpeedMultiplier() {
		return 1.5f;
	}

	@Override
	protected double getConveyorSpeedMultiplier() {
		return 3;
	}

	@Override
	protected int getConveyorSupplierStackSize() {
		return 32;
	}

	@Override
	protected int getConveyorExtractorStackSize() {
		return 32;
	}
}
