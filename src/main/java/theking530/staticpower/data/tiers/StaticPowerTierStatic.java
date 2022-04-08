package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
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
	protected long getPortableBatteryCapacity() {
		return 2500000;
	}

	@Override
	protected long getSolarPanelPowerGeneration() {
		return 4000;
	}

	@Override
	protected long getSolarPanelPowerStorage() {
		return 8000;
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
	protected long getCablePowerCapacity() {
		return 256000;
	}

	@Override
	protected long getCablePowerDelivery() {
		return 256000;
	}

	@Override
	protected long getCableIndustrialPowerCapacity() {
		return 2048000;
	}

	@Override
	protected long getCableIndustrialPowerDelivery() {
		return 2048000;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 65536;
	}

	@Override
	protected long getBatteryCapacity() {
		return 25000000;
	}

	@Override
	protected long getBatteryMaxIO() {
		return 512000;
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
		return 5.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 6.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 5.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 4.0f;
	}

	@Override
	public double getHeatCapacityUpgrade() {
		return 1.0f;
	}

	@Override
	public double getHeatConductivityUpgrade() {
		return 1.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 5.0f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 4.5f;
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
	protected long getDefaultMachinePowerCapacity() {
		return 2500000;
	}

	@Override
	protected long getDefaultMachinePowerInput() {
		return 32000;
	}

	@Override
	protected long getDefaultMachinePowerOutput() {
		return 32000;
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
	protected long getMagnetPowerCapacity() {
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
}
