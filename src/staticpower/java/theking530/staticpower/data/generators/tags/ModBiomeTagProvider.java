package theking530.staticpower.data.generators.tags;

import javax.annotation.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.tags.ModBiomeTags;

public class ModBiomeTagProvider extends BiomeTagsProvider {

	public ModBiomeTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, StaticPower.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(ModBiomeTags.OIL_DEPOSIT_SUITABLE).addTag(BiomeTags.IS_BADLANDS).addTag(BiomeTags.IS_OCEAN)
				.addTag(BiomeTags.IS_DEEP_OCEAN).addTag(Tags.Biomes.IS_DESERT).add(Biomes.SWAMP).add(Biomes.ICE_SPIKES)
				.add(Biomes.FROZEN_OCEAN);

		tag(ModBiomeTags.RUBBER_TREE_SUITABLE).addTag(Tags.Biomes.IS_SWAMP).addTag(Tags.Biomes.IS_CONIFEROUS)
				.addTag(BiomeTags.IS_FOREST);
	}
}
