package theking530.staticpower.client;

import java.util.HashSet;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import theking530.staticpower.utilities.Reference;

public class StaticPowerAdditionalModels {
	public static final HashSet<ResourceLocation> MODELS = new HashSet<ResourceLocation>();

	public static final ResourceLocation CABLE_POWER_STRAIGHT = registerModel("block/cables/power/straight");
	public static final ResourceLocation CABLE_POWER_EXTENSION = registerModel("block/cables/power/extension");
	public static final ResourceLocation CABLE_POWER_ATTACHMENT = registerModel("block/cables/power/attachment");

	public static final ResourceLocation CABLE_ITEM_STRAIGHT = registerModel("block/cables/item/straight");
	public static final ResourceLocation CABLE_ITEM_EXTENSION = registerModel("block/cables/item/extension");
	public static final ResourceLocation CABLE_ITEM_ATTACHMENT = registerModel("block/cables/item/attachment");
	
	public static final ResourceLocation CABLE_FLUID_STRAIGHT = registerModel("block/cables/fluid/straight");
	public static final ResourceLocation CABLE_FLUID_EXTENSION = registerModel("block/cables/fluid/extension");
	public static final ResourceLocation CABLE_FLUID_ATTACHMENT = registerModel("block/cables/fluid/attachment");
	
	public static final ResourceLocation CABLE_BASIC_EXTRACTOR_ATTACHMENT = registerModel("block/cables/attachments/basic_extractor");
	public static final ResourceLocation CABLE_ADVANCED_EXTRACTOR_ATTACHMENT = registerModel("block/cables/attachments/advanced_extractor");
	public static final ResourceLocation CABLE_STATIC_EXTRACTOR_ATTACHMENT = registerModel("block/cables/attachments/static_extractor");
	public static final ResourceLocation CABLE_ENERGIZED_EXTRACTOR_ATTACHMENT = registerModel("block/cables/attachments/energized_extractor");
	public static final ResourceLocation CABLE_LUMUM_EXTRACTOR_ATTACHMENT = registerModel("block/cables/attachments/lumum_extractor");

	public static final ResourceLocation CABLE_BASIC_FILTER_ATTACHMENT = registerModel("block/cables/attachments/basic_filter");
	public static final ResourceLocation CABLE_ADVANCED_FILTER_ATTACHMENT = registerModel("block/cables/attachments/advanced_filter");
	public static final ResourceLocation CABLE_STATIC_FILTER_ATTACHMENT = registerModel("block/cables/attachments/static_filter");
	public static final ResourceLocation CABLE_ENERGIZED_FILTER_ATTACHMENT = registerModel("block/cables/attachments/energized_filter");
	public static final ResourceLocation CABLE_LUMUM_FILTER_ATTACHMENT = registerModel("block/cables/attachments/lumum_filter");
	
	public static final ResourceLocation CABLE_BASIC_RETRIEVER_ATTACHMENT = registerModel("block/cables/attachments/basic_retriever");
	public static final ResourceLocation CABLE_ADVANCED_RETRIEVER_ATTACHMENT = registerModel("block/cables/attachments/advanced_retriever");
	public static final ResourceLocation CABLE_STATIC_RETRIEVER_ATTACHMENT = registerModel("block/cables/attachments/static_retriever");
	public static final ResourceLocation CABLE_ENERGIZED_RETRIEVER_ATTACHMENT = registerModel("block/cables/attachments/energized_retriever");
	public static final ResourceLocation CABLE_LUMUM_RETRIEVER_ATTACHMENT = registerModel("block/cables/attachments/lumum_retriever");
	
	public static void regsiterModels() {
		for (ResourceLocation model : MODELS) {
			ModelLoader.addSpecialModel(model);
		}
	}

	private static ResourceLocation registerModel(String path) {
		ResourceLocation sprite = new ResourceLocation(Reference.MOD_ID, path);
		MODELS.add(sprite);
		return sprite;
	}
}
