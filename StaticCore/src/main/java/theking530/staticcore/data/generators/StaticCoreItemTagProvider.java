package theking530.staticcore.data.generators;

import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import theking530.staticcore.StaticCore;
import theking530.staticcore.init.ItemTags;
import theking530.staticcore.init.StaticCoreItems;

public class StaticCoreItemTagProvider extends ItemTagsProvider {

	public StaticCoreItemTagProvider(DataGenerator generator, StaticCoreBlockTagProvider blockTags, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, blockTags, StaticCore.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(ItemTags.RESEARCH).add(StaticCoreItems.ResearchTier1.get()).add(StaticCoreItems.ResearchTier2.get()).add(StaticCoreItems.ResearchTier3.get()).add(StaticCoreItems.ResearchTier4.get())
				.add(StaticCoreItems.ResearchTier5.get()).add(StaticCoreItems.ResearchTier6.get()).add(StaticCoreItems.ResearchTier7.get());
	}

	public static Item[] toArray(Stream<? extends Item> stream) {
		return stream.toList().toArray(Item[]::new);
	}
}
