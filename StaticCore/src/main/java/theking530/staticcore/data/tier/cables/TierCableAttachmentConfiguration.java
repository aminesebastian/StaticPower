package theking530.staticcore.data.tier.cables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public abstract class TierCableAttachmentConfiguration {
	/***************************
	 * Extraction Configuration
	 ***************************/
	public final ConfigValue<Integer> cableExtractorRate;
	public final ConfigValue<Integer> cableExtractionStackSize;
	public final ConfigValue<Integer> cableExtractionFluidRate;
	public final ConfigValue<Integer> cableExtractionFilterSlots;
	public final ConfigValue<Double> cableExtractedItemInitialSpeed;

	/*************************
	 * Retrieval Configuration
	 *************************/
	public final ConfigValue<Integer> cableRetrievalRate;
	public final ConfigValue<Integer> cableRetrievalStackSize;
	public final ConfigValue<Integer> cableRetrievalFilterSlots;
	public final ConfigValue<Double> cableRetrievedItemInitialSpeed;

	/**********************
	 * Filter Configuration
	 **********************/
	public final ConfigValue<Integer> cableFilterSlots;

	public TierCableAttachmentConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		builder.push("Cables_Attachments");
		builder.push("Extractor");
		cableExtractorRate = builder.comment("How many ticks inbetween each extraction. The lower, the more frequently it extracts. Lower values impact performance.")
				.translation(modId + ".config." + "cableExtractorRate").define("CableExtractorRate", this.getCableExtractorRate());

		cableExtractionStackSize = builder.comment("The number of items that are extracted per extraction.")
				.translation(modId + ".config." + "cableExtractionStackSize").define("CableExtractionStackSize", this.getCableExtractionStackSize());

		cableExtractionFluidRate = builder.comment("The amount of fluid extracted per extraction").translation(modId + ".config." + "cableExtractionFluidRate")
				.define("CableExtractionFluidRate", this.getCableExtractionFluidRate());

		cableExtractionFilterSlots = builder.comment("The number of filter slots available on an extractor of this tier.")
				.translation(modId + ".config." + "cableExtractionFilterSlots").define("CableExtractionFilterSlots", this.getCableExtractionFilterSlots());

		cableExtractedItemInitialSpeed = builder.comment("The initial speed of the item that is input into the tube when an extractor of this tier extracts an item.")
				.translation(modId + ".config." + "cableExtractedItemInitialSpeed").define("CableExtractedItemInitialSpeed", this.getExtractedItemInitialSpeed());
		builder.pop();

		builder.push("Retriever");
		cableRetrievalRate = builder.comment("How many ticks inbetween each extraction. The lower, the more frequently it extracts. Lower values impact performance.")
				.translation(modId + ".config." + "cableRetrievalRate").define("CableRetrievalRate", this.getCableRetrievalRate());

		cableRetrievalStackSize = builder.comment("The number of items that are retrieved per operation.").translation(modId + ".config." + "cableRetrievalStackSize")
				.define("CableRetrievalStackSize", this.getCableRetrievalStackSize());

		cableRetrievalFilterSlots = builder.comment("The number of filter slots available on a retriever of this tier. Higher numbers will impact performance.")
				.translation(modId + ".config." + "cableRetrievalFilterSlots").define("CableRetrievalFilterSlots", this.getCableRetrievalFilterSlots());

		cableRetrievedItemInitialSpeed = builder.comment("The initial speed of the item that is input into the tube when an retriever of this tier retrieves an item.")
				.translation(modId + ".config." + "cableRetrievedItemInitialSpeed").define("CableRetrievedItemInitialSpeed", this.getRetrievedItemInitialSpeed());
		builder.pop();

		builder.push("Filter");
		cableFilterSlots = builder.comment("The number slots in a filter of this tier.").translation(modId + ".config." + "cableFilterSlots")
				.define("CableFilterSlots", this.getCableFilterSlots());
		builder.pop();
		builder.pop();
	}

	protected abstract int getCableExtractorRate();

	protected abstract int getCableExtractionStackSize();

	protected abstract int getCableExtractionFluidRate();

	protected abstract int getCableExtractionFilterSlots();

	protected abstract double getExtractedItemInitialSpeed();

	protected abstract int getCableRetrievalRate();

	protected abstract int getCableRetrievalStackSize();

	protected abstract int getCableRetrievalFilterSlots();

	protected abstract double getRetrievedItemInitialSpeed();

	protected abstract int getCableFilterSlots();
}
