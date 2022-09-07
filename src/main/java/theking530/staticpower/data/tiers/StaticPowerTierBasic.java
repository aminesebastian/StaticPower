package theking530.staticpower.data.tiers;

import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierBasic extends StaticPowerTier {

	public StaticPowerTierBasic(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "basic");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.basic";
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 1;
	}

	@Override
	protected double getPortableBatteryCapacity() {
		return 1000;
	}

	@Override
	protected List<Double> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(0.0, 5.0);
	}

	@Override
	protected double getPortableBatteryMaxCurrent() {
		return 1;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 1;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return 5;
	}

	@Override
	protected int getPumpRate() {
		return 100;
	}

	@Override
	protected int getCableFilterSlots() {
		return 3;
	}

	@Override
	protected int getCableExtractorRate() {
		return 40;
	}

	@Override
	protected int getCableExtractionStackSize() {
		return 4;
	}

	@Override
	protected int getCableExtractionFluidRate() {
		return 32;
	}

	@Override
	protected int getCableExtractionFilterSlots() {
		return 3;
	}

	@Override
	protected double getExtractedItemInitialSpeed() {
		return .2;
	}

	@Override
	protected int getCableRetrievalRate() {
		return 40;
	}

	@Override
	protected int getCableRetrievalStackSize() {
		return 4;
	}

	@Override
	protected int getCableRetrievalFilterSlots() {
		return 1;
	}

	@Override
	protected double getRetrievedItemInitialSpeed() {
		return .2;
	}

	@Override
	protected double getCablePowerMaxCurrent() {
		return 10;
	}

	@Override
	protected double getCablePowerResistancePerBlock() {
		return 10;
	}

	@Override
	protected double getCableIndustrialPowerMaxCurrent() {
		return 100;
	}

	@Override
	protected double getCableIndustrialPowerResistancePerBlock() {
		return 100;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 4096;
	}

	@Override
	protected double getBatteryCapacity() {
		return 3200;
	}

	@Override
	protected double getItemCableMaxSpeed() {
		return .4;
	}

	@Override
	protected int getItemFilterSlots() {
		return 3;
	}

	@Override
	protected double getItemCableAcceleration() {
		return 1.1f;
	}

	@Override
	protected double getItemCableFriction() {
		return 2.5f;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 2.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 3.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 1.5f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 2.0f;
	}

	@Override
	public float getHeatCapacityUpgrade() {
		return 1f;
	}

	@Override
	public float getHeatConductivityUpgrade() {
		return 1f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 2f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 2f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 1.25f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 1.25f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 750;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 2.0f;
	}

	@Override
	protected double getDefaultMachinePowerCapacity() {
		return 100;
	}

	@Override
	protected List<Double> internalGetDefaultMachineInputVoltageRange() {
		return Arrays.asList(0.0, 12.0);
	}

	@Override
	protected double getDefaultMachineMaximumInputCurrent() {
		return 1;
	}

	@Override
	protected double getDefaultMachineMaximumVoltageOutput() {
		return 12;
	}

	@Override
	protected double getDefaultMachineMaximumCurrentOutput() {
		return 1;
	}

	@Override
	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(125);
	}

	@Override
	protected int getDefaultMachineMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(250);
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 5000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 4000;
	}

	@Override
	protected int getCableFluidCapacity() {
		return 100;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return 1000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 1.0;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 108000;
	}

	@Override
	protected double getMagnetPowerCapacity() {
		return getPortableBatteryCapacity() * 2;
	}

	@Override
	protected int getMagnetRadius() {
		return 2;
	}

	@Override
	protected float getDrillSpeedMultiplier() {
		return 0.5f;
	}

	@Override
	protected float getChainsawSpeedMultiplier() {
		return 0.5f;
	}

	@Override
	protected double getConveyorSpeedMultiplier() {
		return 1;
	}

	@Override
	protected int getConveyorSupplierStackSize() {
		return 4;
	}

	@Override
	protected int getConveyorExtractorStackSize() {
		return 4;
	}
}
