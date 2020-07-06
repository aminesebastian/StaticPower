package theking530.staticpower.data;

import net.minecraft.util.ResourceLocation;

public class StaticPowerTier {
	private ResourceLocation tierId;
	private String unlocalizedTierName;
	private int portableBatteryCapacity;
	private int digistoreItemCapacityUpgrade;
	private int solarPanelPowerGeneration;
	private int solarPanelPowerStorage;
	private int cableExtractorRate;
	private int cableExtractionStackSize;
	private int cableExtractionFluidRate;
	private int cableExtractionFilterSize;
	private int cableRetrievalRate;
	private int cableRetrievalStackSize;
	private int cableRetrievalFilterSize;
	private int cableFilterSize;

	public int getCableFilterSize() {
		return cableFilterSize;
	}

	public ResourceLocation getTierId() {
		return tierId;
	}

	public String getUnlocalizedTierName() {
		return unlocalizedTierName;
	}

	public int getPortableBatteryCapacity() {
		return portableBatteryCapacity;
	}

	public int getDigistoreItemCapacityUpgrade() {
		return digistoreItemCapacityUpgrade;
	}

	public int getSolarPanelPowerGeneration() {
		return solarPanelPowerGeneration;
	}

	public int getSolarPanelPowerStorage() {
		return solarPanelPowerStorage;
	}

	public int getCableExtractorRate() {
		return cableExtractorRate;
	}

	public int getCableExtractionStackSize() {
		return cableExtractionStackSize;
	}

	public int getCableExtractionFluidRate() {
		return cableExtractionFluidRate;
	}

	public int getCableExtractionFilterSize() {
		return cableExtractionFilterSize;
	}

	public int getCableRetrievalRate() {
		return cableRetrievalRate;
	}

	public int getCableRetrievalStackSize() {
		return cableRetrievalStackSize;
	}

	public int getCableRetrievalFilterSize() {
		return cableRetrievalFilterSize;
	}

}