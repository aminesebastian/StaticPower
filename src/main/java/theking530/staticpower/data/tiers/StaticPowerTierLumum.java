package theking530.staticpower.data.tiers;

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
	protected long getPortableBatteryCapacity() {
		return 10000000;
	}

	@Override
	protected long getSolarPanelPowerGeneration() {
		return 16000;
	}

	@Override
	protected long getSolarPanelPowerStorage() {
		return 32000;
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
		return 100;
	}

	@Override
	protected double getCablePowerResistancePerBlock() {
		return 0.0001;
	}

	@Override
	protected double getCableIndustrialPowerMaxCurrent() {
		return 500;
	}

	@Override
	protected double getCableIndustrialPowerResistancePerBlock() {
		return 0.001;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 262144;
	}

	@Override
	protected long getBatteryCapacity() {
		return 100000000;
	}

	@Override
	protected long getBatteryMaxIO() {
		return 2048000;
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
	protected long getDefaultMachinePowerCapacity() {
		return 10000000;
	}

	@Override
	protected long getDefaultMachinePowerInput() {
		return 128000;
	}

	@Override
	protected long getDefaultMachinePowerOutput() {
		return 128000;
	}

	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(2800);
	}

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
	protected long getMagnetPowerCapacity() {
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
