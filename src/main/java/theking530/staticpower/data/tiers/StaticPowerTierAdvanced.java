package theking530.staticpower.data.tiers;

import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierAdvanced extends StaticPowerTier {

	public StaticPowerTierAdvanced(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "advanced");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.advanced";
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 2;
	}

	@Override
	protected double getPortableBatteryCapacity() {
		return 5000;
	}

	@Override
	protected List<Double> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(5.0, 12.0);
	}

	@Override
	protected double getPortableBatteryMaxOutputCurrent() {
		return 2;
	}

	@Override
	protected double getPortableBatteryOutputVoltage() {
		return 12;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 2;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return 10;
	}

	@Override
	protected int getPumpRate() {
		return 80;
	}

	@Override
	protected int getCableFilterSlots() {
		return 5;
	}

	@Override
	protected int getCableExtractorRate() {
		return 40;
	}

	@Override
	protected int getCableExtractionStackSize() {
		return 8;
	}

	@Override
	protected int getCableExtractionFluidRate() {
		return 64;
	}

	@Override
	protected int getCableExtractionFilterSlots() {
		return 5;
	}

	@Override
	protected double getExtractedItemInitialSpeed() {
		return 0.5;
	}

	@Override
	protected int getCableRetrievalRate() {
		return 40;
	}

	@Override
	protected int getCableRetrievalStackSize() {
		return 8;
	}

	@Override
	protected int getCableRetrievalFilterSlots() {
		return 2;
	}

	@Override
	protected double getRetrievedItemInitialSpeed() {
		return 0.5;
	}

	@Override
	protected double getCablePowerMaxCurrent() {
		return 15;
	}

	@Override
	protected double getCablePowerResistancePerBlock() {
		return 5;
	}

	@Override
	protected double getCableIndustrialPowerMaxCurrent() {
		return 200;
	}

	@Override
	protected double getCableIndustrialPowerResistancePerBlock() {
		return 50;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 16384;
	}

	@Override
	protected double getBatteryCapacity() {
		return 6400;
	}

	@Override
	protected List<Double> internalGetBatteryInputVoltageRange() {
		return Arrays.asList(0.0, 48.0);
	}

	@Override
	protected List<Double> internalGetBatteryOutputVoltageRange() {
		return Arrays.asList(24.0, 48.0);
	}

	@Override
	protected double getBatteryMaximumOutputCurrent() {
		return 10.0;
	}

	@Override
	protected double getItemCableMaxSpeed() {
		return 1.0;
	}

	@Override
	protected int getItemFilterSlots() {
		return 5;
	}

	@Override
	protected double getItemCableAcceleration() {
		return 1.25f;
	}

	@Override
	protected double getItemCableFriction() {
		return 2.25f;
	}

	@Override
	protected int getDrillBitUses() {
		return 6500;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 13000;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 4.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 2.5f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 1.5f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 1.5f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 850;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 3.0f;
	}

	@Override
	protected double getDefaultMachinePowerCapacity() {
		return 250;
	}

	@Override
	protected List<Double> internalGetDefaultMachineInputVoltageRange() {
		return Arrays.asList(12.0, 24.0);
	}

	@Override
	protected double getDefaultMachineMaximumInputCurrent() {
		return 2;
	}

	@Override
	protected double getDefaultMachineMaximumVoltageOutput() {
		return 24;
	}

	@Override
	protected double getDefaultMachineMaximumCurrentOutput() {
		return 2;
	}

	@Override
	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(350);
	}

	@Override
	protected int getDefaultMachineMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(500);
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 7500;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 8000;
	}

	@Override
	protected int getCableFluidCapacity() {
		return 250;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return 2500;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 1.5;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 216000;
	}

	@Override
	protected double getMagnetPowerCapacity() {
		return getPortableBatteryCapacity() * 2;
	}

	@Override
	protected int getMagnetRadius() {
		return 3;
	}

	@Override
	protected float getDrillSpeedMultiplier() {
		return 0.75f;
	}

	@Override
	protected float getChainsawSpeedMultiplier() {
		return 0.75f;
	}

	@Override
	protected double getConveyorSpeedMultiplier() {
		return 1.5;
	}

	@Override
	protected int getConveyorSupplierStackSize() {
		return 8;
	}

	@Override
	protected int getConveyorExtractorStackSize() {
		return 8;
	}
}
