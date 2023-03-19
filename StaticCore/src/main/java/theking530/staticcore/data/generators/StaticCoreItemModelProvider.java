package theking530.staticcore.data.generators;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.StaticCore;
import theking530.staticcore.init.StaticCoreItems;

public class StaticCoreItemModelProvider extends ItemModelProvider {
	public StaticCoreItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, StaticCore.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		simpleItem(StaticCoreItems.ResearchTier1.get(), "research/research_tier_1");
		simpleItem(StaticCoreItems.ResearchTier2.get(), "research/research_tier_2");
		simpleItem(StaticCoreItems.ResearchTier3.get(), "research/research_tier_3");
		simpleItem(StaticCoreItems.ResearchTier4.get(), "research/research_tier_4");
		simpleItem(StaticCoreItems.ResearchTier5.get(), "research/research_tier_5");
		simpleItem(StaticCoreItems.ResearchTier6.get(), "research/research_tier_6");
		simpleItem(StaticCoreItems.ResearchTier7.get(), "research/research_tier_7");
	}

	private ItemModelBuilder simpleItem(Item item, String texturePath) {
		return withExistingParent(name(item), new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation(StaticCore.MOD_ID, "items/" + texturePath));
	}

	private String name(Item item) {
		return key(item).getPath();
	}

	private ResourceLocation key(Item item) {
		return ForgeRegistries.ITEMS.getKey(item);
	}
}