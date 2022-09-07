package theking530.staticpower.data.tiers;

import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierStatic extends StaticPowerTier {

	public StaticPowerTierStatic(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "static");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.static";
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 3;
	}

	@Override
	protected double getPortableBatteryCapacity() {
		return 10000;
	}

	@Override
	protected List<Double> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(12.0, 24.0);
	}

	@Override
	protected double getPortableBatteryMaxCurrent() {
		return 5;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 4;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return 20;
	}

	@Override
	protected int getPumpRate() {
		return 60;
	}

	@Override
	protected int getCableFilterSlots() {
		return 7;
	}

	@Override
	protected int getCableExtractorRate() {
		return 20;
	}

	@Override
	protected int getCableExtractionStackSize() {
		return 16;
	}

	@Override
	protected int getCableExtractionFluidRate() {
		return 256;
	}

	@Override
	protected int getCableExtractionFilterSlots() {
		return 7;
	}

	@Override
	protected double getExtractedItemInitialSpeed() {
		return 1.0;
	}

	@Override
	protected int getCableRetrievalRate() {
		return 20;
	}

	@Override
	protected int getCableRetrievalStackSize() {
		return 16;
	}

	@Override
	protected int getCableRetrievalFilterSlots() {
		return 3;
	}

	@Override
	protected double getRetrievedItemInitialSpeed() {
		return 1.0;
	}

	@Override
	protected double getCablePowerMaxCurrent() {
		return 20;
	}

	@Override
	protected double getCablePowerResistancePerBlock() {
		return 1;
	}

	@Override
	protected double getCableIndustrialPowerMaxCurrent() {
		return 300;
	}

	@Override
	protected double getCableIndustrialPowerResistancePerBlock() {
		return 10;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 65536;
	}

	@Override
	protected double getBatteryCapacity() {
		return 25000;
	}

	@Override
	protected double getItemCableMaxSpeed() {
		return 1.0;
	}

	@Override
	protected int getItemFilterSlots() {
		return 7;
	}

	@Override
	protected double getItemCableAcceleration() {
		return 1.4f;
	}

	@Override
	protected double getItemCableFriction() {
		return 2.0f;
	}

	@Override
	protected int getDrillBitUses() {
		return 8000;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 16000;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 4.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 6.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 4.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 4.0f;
	}

	@Override
	public float getHeatCapacityUpgrade() {
		return 2.0f;
	}

	@Override
	public float getHeatConductivityUpgrade() {
		return 2.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 4f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 6f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 2.0f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 1.75f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 1000;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 4.0f;
	}

	@Override
	protected double getDefaultMachinePowerCapacity() {
		return 500;
	}

	@Override
	protected List<Double> internalGetDefaultMachineInputVoltageRange() {
		return Arrays.asList(24.0, 48.0);
	}

	@Override
	protected double getDefaultMachineMaximumInputCurrent() {
		return 3;
	}

	@Override
	protected double getDefaultMachineMaximumVoltageOutput() {
		return 48;
	}

	@Override
	protected double getDefaultMachineMaximumCurrentOutput() {
		return 3;
	}

	@Override
	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(700);
	}

	@Override
	protected int getDefaultMachineMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(1000);
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 10000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 16000;
	}

	@Override
	protected int getCableFluidCapacity() {
		return 1000;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return 10000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 2;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 432000;
	}

	@Override
	protected double getMagnetPowerCapacity() {
		return getPortableBatteryCapacity() * 2;
	}

	@Override
	protected int getMagnetRadius() {
		return 4;
	}

	@Override
	protected float getDrillSpeedMultiplier() {
		return 1.0f;
	}

	@Override
	protected float getChainsawSpeedMultiplier() {
		return 1.0f;
	}

	@Override
	protected double getConveyorSpeedMultiplier() {
		return 2;
	}

	@Override
	protected int getConveyorSupplierStackSize() {
		return 16;
	}

	@Override
	protected int getConveyorExtractorStackSize() {
		return 16;
	}
}
