package theking530.staticpower.data.tiers;

import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierLumum extends StaticPowerTier {

	public StaticPowerTierLumum(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "lumum");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.lumum";
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 5;
	}

	@Override
	protected double getPortableBatteryCapacity() {
		return 100000;
	}

	@Override
	protected List<Double> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(48.0, 120.0);
	}

	@Override
	protected double getPortableBatteryMaxOutputCurrent() {
		return 5;
	}

	@Override
	protected double getPortableBatteryOutputVoltage() {
		return 120;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 16;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return 80;
	}

	@Override
	protected int getPumpRate() {
		return 20;
	}

	@Override
	protected int getCableFilterSlots() {
		return 12;
	}

	@Override
	protected int getCableExtractorRate() {
		return 20;
	}

	@Override
	protected int getCableExtractionStackSize() {
		return 64;
	}

	@Override
	protected int getCableExtractionFluidRate() {
		return 1024;
	}

	@Override
	protected int getCableExtractionFilterSlots() {
		return 12;
	}

	@Override
	protected double getExtractedItemInitialSpeed() {
		return 2;
	}

	@Override
	protected int getCableRetrievalRate() {
		return 2;
	}

	@Override
	protected int getCableRetrievalStackSize() {
		return 64;
	}

	@Override
	protected int getCableRetrievalFilterSlots() {
		return 5;
	}

	@Override
	protected double getRetrievedItemInitialSpeed() {
		return 2;
	}

	@Override
	protected double getCablePowerMaxCurrent() {
		return 50;
	}

	@Override
	protected double getCablePowerResistancePerBlock() {
		return 0.1;
	}

	@Override
	protected double getCableIndustrialPowerMaxCurrent() {
		return 500;
	}

	@Override
	protected double getCableIndustrialPowerResistancePerBlock() {
		return 2;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 262144;
	}

	@Override
	protected double getBatteryCapacity() {
		return 100000;
	}

	@Override
	protected List<Double> internalGetBatteryInputVoltageRange() {
		return Arrays.asList(0.0, 480.0);
	}

	@Override
	protected List<Double> internalGetBatteryOutputVoltageRange() {
		return Arrays.asList(240.0, 480.0);
	}

	@Override
	protected double getBatteryMaximumOutputCurrent() {
		return 30.0;
	}

	@Override
	protected double getItemCableMaxSpeed() {
		return 5;
	}

	@Override
	protected int getItemFilterSlots() {
		return 18;
	}

	@Override
	protected double getItemCableAcceleration() {
		return 2.0f;
	}

	@Override
	protected double getItemCableFriction() {
		return 1.5f;
	}

	@Override
	protected int getDrillBitUses() {
		return 16000;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 32000;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 14.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 16.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 10.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 6.0f;
	}

	@Override
	public float getHeatCapacityUpgrade() {
		return 6.0f;
	}

	@Override
	public float getHeatConductivityUpgrade() {
		return 4.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 8.0f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 16.0f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 4.0f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 2.5f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 1500;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 6.0f;
	}

	@Override
	protected double getDefaultMachinePowerCapacity() {
		return 5000;
	}

	@Override
	protected List<Double> internalGetDefaultMachineInputVoltageRange() {
		return Arrays.asList(120.0, 240.0);
	}

	@Override
	protected double getDefaultMachineMaximumInputCurrent() {
		return 5;
	}

	@Override
	protected double getDefaultMachineMaximumVoltageOutput() {
		return 240;
	}

	@Override
	protected double getDefaultMachineMaximumCurrentOutput() {
		return 5;
	}

	@Override
	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(2800);
	}

	@Override
	protected int getDefaultMachineMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(4000);
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 20000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 64000;
	}

	@Override
	protected int getCableFluidCapacity() {
		return 5000;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return 50000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 3.0;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 1728000;
	}

	@Override
	protected double getMagnetPowerCapacity() {
		return getPortableBatteryCapacity() * 2;
	}

	@Override
	protected int getMagnetRadius() {
		return 8;
	}

	@Override
	protected float getDrillSpeedMultiplier() {
		return 2f;
	}

	@Override
	protected float getChainsawSpeedMultiplier() {
		return 2f;
	}

	@Override
	protected double getConveyorSpeedMultiplier() {
		return 4;
	}

	@Override
	protected int getConveyorSupplierStackSize() {
		return 64;
	}

	@Override
	protected int getConveyorExtractorStackSize() {
		return 64;
	}
}
