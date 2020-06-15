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
	public static final ResourceLocation CABLE_ITEM_EXTRACTOR_ATTACHMENT = registerModel("block/cables/item/extractor_attachment");

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
