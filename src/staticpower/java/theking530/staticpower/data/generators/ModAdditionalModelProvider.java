package theking530.staticpower.data.generators;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.generators.ModAdditionalModelProvider.AdditionalModelBuilder;

public class ModAdditionalModelProvider extends ModelProvider<AdditionalModelBuilder> {
	public static final String MODELS_FOLDER = "models";

	public ModAdditionalModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, StaticPower.MOD_ID, MODELS_FOLDER, AdditionalModelBuilder::new, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		pumpAttachment("iron");
		pumpAttachment("basic");
		pumpAttachment("advanced");
		pumpAttachment("static");
		pumpAttachment("energized");
		pumpAttachment("lumum");
		pumpAttachment("creative");
	}

	private AdditionalModelBuilder pumpAttachment(String tier) {
		ResourceLocation texture = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/pumps/pump_" + tier);
		return withExistingParent("pump_connector_" + tier, new ResourceLocation(StaticPower.MOD_ID, "block/base_models/pump_connector")).texture("texture", texture);
	}

	@Override
	public String getName() {
		return "Additional Models: " + modid;
	}

	public static class AdditionalModelBuilder extends ModelBuilder<AdditionalModelBuilder> {
		protected AdditionalModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
			super(outputLocation, existingFileHelper);
		}
	}
}
