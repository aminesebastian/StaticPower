package theking530.staticcore.data.generators;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import theking530.staticcore.StaticCore;

public class StaticCoreBlockTagProvider extends BlockTagsProvider {

	public StaticCoreBlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, StaticCore.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
	
	}
}
